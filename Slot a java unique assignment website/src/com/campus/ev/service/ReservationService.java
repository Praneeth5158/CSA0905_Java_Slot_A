package com.campus.ev.service;

import com.campus.ev.dao.ActivityLogDAO;
import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.ReservationDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.Reservation;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.getAllReservations();
    }

    public List<Reservation> getReservationsForPoint(int pointId, Date date) throws SQLException {
        return reservationDAO.getReservationsByPointAndDate(pointId, date);
    }

    /**
     * Validates and creates a new reservation with strict conflict avoidance.
     */
    public int createReservation(int userId, int vehicleId, int pointId, int stationId, 
                                 Timestamp start, Timestamp end, double estKwh, 
                                 String userName, String userCode, String vehicleNum, String pointCode) throws Exception {
        
        // 1. Validation: Start must be before End
        if (start == null || end == null || !end.after(start)) {
            throw new IllegalArgumentException("Reservation end time must be strictly after start time.");
        }

        // 2. Validation: Cannot book in past
        long nowMs = System.currentTimeMillis() - 5 * 60 * 1000; // 5 min grace
        if (start.getTime() < nowMs) {
            throw new IllegalArgumentException("Cannot create reservations for past time slots.");
        }

        // 3. Validation: Minimum 15 min, Maximum 4 hours
        long durationMs = end.getTime() - start.getTime();
        if (durationMs < 15 * 60 * 1000) {
            throw new IllegalArgumentException("Minimum reservation duration is 15 minutes.");
        }
        if (durationMs > 4 * 60 * 60 * 1000) {
            throw new IllegalArgumentException("Maximum single reservation slot cannot exceed 4 hours.");
        }

        // 4. Point validation: Check status
        ChargingPoint cp = pointDAO.getPointById(pointId);
        if (cp == null) {
            throw new IllegalArgumentException("Selected charging point does not exist.");
        }
        if ("MAINTENANCE".equalsIgnoreCase(cp.getStatus())) {
            throw new IllegalStateException("Charging point " + cp.getPointCode() + " is currently under MAINTENANCE and cannot be reserved.");
        }

        // 5. Conflict Validation: Check point overlapping reservations
        if (reservationDAO.hasConflictingReservation(pointId, start, end, 0)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            throw new IllegalStateException("Charging point " + cp.getPointCode() + 
                " already has a conflicting reservation in the window " + sdf.format(start) + " - " + sdf.format(end) + ".");
        }

        // 6. Conflict Validation: Check vehicle availability
        if (reservationDAO.isVehicleBusy(vehicleId, start, end, 0)) {
            throw new IllegalStateException("Vehicle " + vehicleNum + " is already booked for another charging point during this time window.");
        }

        // 7. Generate Code
        String resCode = "RES-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + "-" + (int)(Math.random() * 900 + 100);

        Reservation r = new Reservation();
        r.setReservationCode(resCode);
        r.setUserId(userId);
        r.setVehicleId(vehicleId);
        r.setPointId(pointId);
        r.setStationId(stationId);
        r.setStartTime(start);
        r.setEndTime(end);
        r.setEstimatedKwh(estKwh > 0 ? estKwh : 15.0);
        r.setStatus("CONFIRMED");

        int resId = reservationDAO.insertReservation(r);

        // Update point status to RESERVED if reservation is imminent (within next 30 min)
        if (start.getTime() - System.currentTimeMillis() < 30 * 60 * 1000 && "AVAILABLE".equalsIgnoreCase(cp.getStatus())) {
            pointDAO.updatePointStatus(pointId, "RESERVED");
        }

        // Log
        logDAO.insertLog("RESERVATION_CREATED", 
            "Reservation #" + resCode + " confirmed for " + userName + " (" + vehicleNum + ") on " + cp.getPointCode(), 
            cp.getPointCode(), userCode, vehicleNum, "INFO");

        return resId;
    }

    public boolean cancelReservation(int reservationId, String pointCode, String userCode, String vehicleNum) throws SQLException {
        boolean success = reservationDAO.cancelReservation(reservationId);
        if (success) {
            logDAO.insertLog("RESERVATION_CANCELLED", "Reservation #" + reservationId + " was cancelled.", pointCode, userCode, vehicleNum, "WARNING");
        }
        return success;
    }
}
