# System Workflows & Core Algorithm Pseudocode

## 1. Reservation Conflict Avoidance Algorithm
```text
FUNCTION createReservation(userId, vehicleId, pointId, stationId, startTime, endTime, estKwh):
    IF startTime IS NULL OR endTime IS NULL OR endTime <= startTime:
        THROW "End time must be strictly after start time"
    
    IF startTime < CURRENT_TIME - 5_MINUTES:
        THROW "Cannot reserve past time intervals"
        
    IF (endTime - startTime) < 15_MINUTES:
        THROW "Minimum reservation duration is 15 minutes"
        
    IF (endTime - startTime) > 4_HOURS:
        THROW "Maximum reservation duration is 4 hours"
        
    point = pointDAO.getPointById(pointId)
    IF point.status == "MAINTENANCE":
        THROW "Charging point is currently under MAINTENANCE"
        
    // Conflict Check 1: Check point overlaps
    conflictCount = QUERY "SELECT COUNT(*) FROM reservations 
                           WHERE point_id = pointId AND status IN ('CONFIRMED', 'CHECKED_IN')
                           AND ((start_time < endTime AND end_time > startTime) 
                             OR (start_time >= startTime AND start_time < endTime))"
    IF conflictCount > 0:
        THROW "Point is already booked during this time window"
        
    // Conflict Check 2: Check vehicle busy
    vehicleConflict = QUERY "SELECT COUNT(*) FROM reservations 
                             WHERE vehicle_id = vehicleId AND status IN ('CONFIRMED', 'CHECKED_IN')
                             AND ((start_time < endTime AND end_time > startTime)
                               OR (start_time >= startTime AND start_time < endTime))"
    IF vehicleConflict > 0:
        THROW "Vehicle is already reserved for another charging point"
        
    reservationCode = GENERATE_UNIQUE_CODE("RES-")
    resId = reservationDAO.insertReservation(reservationCode, userId, vehicleId, pointId, stationId, startTime, endTime, estKwh, "CONFIRMED")
    
    IF (startTime - CURRENT_TIME) < 30_MINUTES AND point.status == "AVAILABLE":
        pointDAO.updatePointStatus(pointId, "RESERVED")
        
    logDAO.insertLog("RESERVATION_CREATED", "Confirmed booking #" + reservationCode, point.code, user.code, vehicle.number)
    RETURN resId
END FUNCTION
```

---

## 2. Live Session Initiation Workflow (ACID Transaction)
```text
FUNCTION startChargingSession(pointId, vehicleId, userId, reservationId, initialSoc, paymentMethod):
    point = pointDAO.getPointById(pointId)
    IF point.status == "OCCUPIED":
        THROW "Node is already occupied"
    IF point.status == "MAINTENANCE":
        THROW "Node is under maintenance"
        
    activeTariff = tariffDAO.getActiveTariff()
    sessionCode = GENERATE_UNIQUE_CODE("SES-")
    
    BEGIN TRANSACTION (conn.setAutoCommit(false))
        TRY:
            // 1. Insert charging session record
            sessionId = INSERT INTO charging_sessions (
                session_code = sessionCode,
                point_id = pointId,
                vehicle_id = vehicleId,
                user_id = userId,
                tariff_id = activeTariff.id,
                start_time = NOW(),
                initial_soc_percent = initialSoc,
                status = 'CHARGING'
            )
            
            // 2. Set node dispenser status to OCCUPIED
            UPDATE charging_points SET status = 'OCCUPIED' WHERE point_id = pointId
            
            // 3. Update reservation status if checked in
            IF reservationId IS NOT NULL:
                UPDATE reservations SET status = 'CHECKED_IN' WHERE reservation_id = reservationId
                
            // 4. Record audit log
            INSERT INTO activity_logs (event_type, description, point_code, severity)
            VALUES ('SESSION_STARTED', 'Session #' + sessionCode + ' launched', point.code, 'SUCCESS')
            
            COMMIT TRANSACTION
            RETURN sessionId
        CATCH SQLException:
            ROLLBACK TRANSACTION
            THROW "Failed to launch session"
END FUNCTION
```

---

## 3. Session Termination & Automated Settlement (ACID Transaction)
```text
FUNCTION stopChargingSession(sessionId, paymentMethod):
    session = sessionDAO.getSessionById(sessionId)
    endTime = NOW()
    durationMinutes = MAX(1, (endTime - session.startTime) / 60)
    
    energyKwh = session.totalEnergyKwh
    tariffRate = session.tariffRate
    energyCost = ROUND(energyKwh * tariffRate, 2)
    
    parkingFee = 0.00
    IF durationMinutes > 60:
        overstayHours = (durationMinutes - 60) / 60.0
        parkingFee = ROUND(overstayHours * 10.00, 2)
        
    totalAmount = energyCost + parkingFee
    invoiceNumber = GENERATE_UNIQUE_CODE("INV-")
    txnRef = GENERATE_UNIQUE_CODE("TXN-")
    
    BEGIN TRANSACTION (conn.setAutoCommit(false))
        TRY:
            // 1. Finalize session record
            UPDATE charging_sessions SET 
                end_time = endTime,
                duration_minutes = durationMinutes,
                energy_cost = energyCost,
                parking_fee = parkingFee,
                total_amount = totalAmount,
                status = 'COMPLETED'
            WHERE session_id = sessionId
            
            // 2. Free charging point to AVAILABLE
            UPDATE charging_points SET status = 'AVAILABLE' WHERE point_id = session.pointId
            
            // 3. Create settled payment invoice
            paymentId = INSERT INTO billing_payments (
                invoice_number = invoiceNumber,
                session_id = sessionId,
                user_id = session.userId,
                amount = totalAmount,
                payment_method = paymentMethod,
                transaction_ref = txnRef,
                payment_status = 'PAID',
                payment_time = endTime
            )
            
            // 4. Update reservation if present
            IF session.reservationId IS NOT NULL:
                UPDATE reservations SET status = 'COMPLETED' WHERE reservation_id = session.reservationId
                
            // 5. Activity log
            INSERT INTO activity_logs (event_type, description, severity)
            VALUES ('SESSION_COMPLETED', 'Session finalized. Total: ₹' + totalAmount, 'SUCCESS')
            
            COMMIT TRANSACTION
            RETURN paymentObject
        CATCH SQLException:
            ROLLBACK TRANSACTION
            THROW "Session termination transaction failed"
END FUNCTION
```

---

## 4. Station Utilization via CallableStatement
```text
FUNCTION getStationUtilizationReport(stationId):
    CallableStatement cs = conn.prepareCall("{CALL sp_get_station_utilization(?)}")
    IF stationId > 0:
        cs.setInt(1, stationId)
    ELSE:
        cs.setNull(1, Types.INTEGER)
        
    ResultSet rs = cs.executeQuery()
    List<StationUtilizationDTO> list = NEW List()
    WHILE rs.next():
        dto = NEW StationUtilizationDTO()
        dto.stationId = rs.getInt("station_id")
        dto.stationName = rs.getString("station_name")
        dto.totalPoints = rs.getInt("total_points")
        dto.occupiedPoints = rs.getInt("occupied_points")
        dto.availablePoints = rs.getInt("available_points")
        dto.totalEnergyKwh = rs.getDouble("total_energy_delivered_kwh")
        dto.utilizationPercent = rs.getDouble("current_utilization_percent")
        list.add(dto)
    RETURN list
END FUNCTION
```
