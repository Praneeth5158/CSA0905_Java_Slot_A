# Comprehensive 20-Point Testing Checklist & Rubric Verification

| Test ID | Test Scenario | Description | Target Statement / API | Verification Result |
|---|---|---|---|---|
| **TEST 01** | Register Campus User | Insert new student/faculty profile with valid fields | `PreparedStatement` (`UserDAO.insertUser`) | **PASS** (Auto-generated Key verified) |
| **TEST 02** | Prevent Duplicate User | Validate uniqueness on user code and university email | `PreparedStatement` (`UserDAO.isUserCodeExists`) | **PASS** (Duplicate rejected with error dialog) |
| **TEST 03** | Register EV Vehicle | Add vehicle with battery capacity, kW rate, connector | `PreparedStatement` (`VehicleDAO.insertVehicle`) | **PASS** (Linked to valid User ID) |
| **TEST 04** | Prevent Duplicate Vehicle | Attempt registering existing vehicle plate number | `PreparedStatement` (`VehicleDAO.isVehicleNumberExists`) | **PASS** (Duplicate plate detected & rejected) |
| **TEST 05** | Create Charging Station | Create campus station hub with power capacity & solar tag | `PreparedStatement` (`ChargingStationDAO.insertStation`) | **PASS** (Station record persisted) |
| **TEST 06** | Create Charging Point | Create dispenser node linked to parent station | `PreparedStatement` (`ChargingPointDAO.insertPoint`) | **PASS** (FK constraint validated) |
| **TEST 07** | Reserve Available Point | Book valid 1-4 hour future slot on available point | `PreparedStatement` (`ReservationService.createReservation`) | **PASS** (Confirmation ID generated) |
| **TEST 08** | Detect Conflicting Slot | Attempt reserving overlapping time interval on same node | `PreparedStatement` (`ReservationDAO.hasConflictingReservation`)| **PASS** (Conflict detected & blocked) |
| **TEST 09** | Start Charging Session | Initiate charging on point and update status to OCCUPIED | `PreparedStatement` (ACID Transaction) | **PASS** (Session created & CP marked OCCUPIED) |
| **TEST 10** | Record Energy Telemetry | Stream voltage, current, power kW, and cumulative kWh | `PreparedStatement` (`EnergyUsageDAO.insertReading`) | **PASS** (Telemetry samples recorded) |
| **TEST 11** | Stop Charging Session | Terminate session, free point to AVAILABLE | `PreparedStatement` (ACID Transaction) | **PASS** (Session finalized atomically) |
| **TEST 12** | Calculate Charging Cost | Multiply kWh by active tariff rate + overstay parking fee | `sp_calculate_session_billing` / Java Service | **PASS** (Accurate INR computation) |
| **TEST 13** | Create Payment Record | Insert invoice record with method and transaction ref | `PreparedStatement` (`PaymentDAO`) | **PASS** (Invoice generated with PAID status) |
| **TEST 14** | Node Status Lifecycle | Transition node to MAINTENANCE mode and restore | `PreparedStatement` (`ChargingPointDAO.updatePointStatus`)| **PASS** (Status toggled and UI updated) |
| **TEST 15** | Station Utilization Report | Execute stored procedure `sp_get_station_utilization` | `CallableStatement` (`ReportDAO.getStationUtilizationByProcedure`)| **PASS** (Returns utilization % and session totals) |
| **TEST 16** | Energy Consumption Report| Query daily kWh and average power flow for date range | `PreparedStatement` (`ReportDAO.getEnergyConsumptionReport`)| **PASS** (Aggregated date breakdown) |
| **TEST 17** | Revenue & Settlement | Group settled payments by payment gateway / channel | `PreparedStatement` (`ReportDAO.getRevenueReport`) | **PASS** (Summary by wallet/UPI/card) |
| **TEST 18** | Search & Filter Engine | Search vehicles/users by plate, role, connector | `PreparedStatement` (Dynamic parameter binding) | **PASS** (Instant filtered results) |
| **TEST 19** | Input Validation Guards | Validate emails, phone format, positive numeric limits | `InputValidator` Regex Engine | **PASS** (Catches invalid values with toast error) |
| **TEST 20** | Statement Demonstration| Query campus summary counters and operational metadata | `Statement` (`ReportDAO.getOperationalSummary`) | **PASS** (Populates top command bar telemetry) |
