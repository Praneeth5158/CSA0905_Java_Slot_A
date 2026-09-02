-- =====================================================================
-- SMART CAMPUS EV CHARGING MANAGEMENT SYSTEM
-- Comprehensive Realistic Sample Data (MySQL 8.0+)
-- =====================================================================

USE campus_ev_db;

-- ---------------------------------------------------------------------
-- 1. TARIFFS
-- ---------------------------------------------------------------------
INSERT INTO tariffs (tariff_id, tariff_code, tariff_name, rate_per_kwh, base_parking_fee_per_hour, peak_hour_multiplier, effective_from, status, description) VALUES
(1, 'TAR-STD-2026', 'Standard Academic Tariff', 7.50, 10.00, 1.00, '2026-01-01', 'ACTIVE', 'Standard day rate for students and faculty'),
(2, 'TAR-SOLAR-GRN', 'Solar Green Off-Peak', 4.50, 5.00, 0.80, '2026-01-01', 'ACTIVE', 'Subsidized green solar energy during 10:00 - 15:00'),
(3, 'TAR-PEAK-PWR', 'Campus Peak Grid Tariff', 11.00, 15.00, 1.25, '2026-01-01', 'ACTIVE', 'Evening peak tariff for high demand periods'),
(4, 'TAR-FLEET-ZERO', 'Campus Shuttle & Fleet Special', 0.00, 0.00, 1.00, '2026-01-01', 'ACTIVE', 'Internal campus electric shuttles & facility maintenance');

-- ---------------------------------------------------------------------
-- 2. CAMPUS USERS
-- ---------------------------------------------------------------------
INSERT INTO campus_users (user_id, user_code, full_name, email, phone, department, campus_role, status, rfid_card_uid) VALUES
(1, 'USR-FAC-101', 'Dr. Arvind Sharma', 'arvind.sharma@university.edu', '+91 9876543210', 'Electrical & Electronics Engg', 'FACULTY', 'ACTIVE', 'RFID-A1B2C3D4'),
(2, 'USR-STU-204', 'Priya Nandakumar', 'priya.n@student.university.edu', '+91 9812345678', 'Computer Science & AI', 'STUDENT', 'ACTIVE', 'RFID-E5F6A7B8'),
(3, 'USR-FLT-001', 'Ramesh Kumar (Fleet Lead)', 'fleet.ops@university.edu', '+91 9845011223', 'Campus Logistics & Transport', 'CAMPUS_FLEET', 'ACTIVE', 'RFID-C9D0E1F2'),
(4, 'USR-FAC-305', 'Prof. Meera Raghavan', 'meera.raghavan@university.edu', '+91 9765432109', 'Renewable Energy Research Inst', 'FACULTY', 'ACTIVE', 'RFID-3A4B5C6D'),
(5, 'USR-STU-412', 'Rohan Verma', 'rohan.v@student.university.edu', '+91 9901122334', 'Mechanical & Aerospace Engg', 'STUDENT', 'ACTIVE', 'RFID-7E8F9A0B'),
(6, 'USR-STF-088', 'Sunita Deshmukh', 'facility.mgr@university.edu', '+91 9823344556', 'Campus Facilities Management', 'FACILITY_STAFF', 'ACTIVE', 'RFID-1C2D3E4F'),
(7, 'USR-VIS-901', 'Vikramaditya Sengupta', 'vikram.guest@greentech.org', '+91 9877112233', 'External Research Visitor', 'VISITOR', 'ACTIVE', 'RFID-5A6B7C8D');

-- ---------------------------------------------------------------------
-- 3. EV VEHICLES
-- ---------------------------------------------------------------------
INSERT INTO ev_vehicles (vehicle_id, vehicle_number, user_id, vehicle_type, brand, model, battery_capacity_kwh, max_charge_rate_kw, connector_type, status) VALUES
(1, 'KA-01-EV-2024', 1, '4-WHEELER_SEDAN', 'Tata', 'Nexon EV Max', 40.50, 50.00, 'CCS_2_DC', 'ACTIVE'),
(2, 'KA-03-EQ-9912', 2, '2-WHEELER_SCOOTER', 'Ather', '450X Gen 3', 3.70, 3.30, 'TYPE_2_AC', 'ACTIVE'),
(3, 'KA-01-BUS-010', 3, 'CAMPUS_BUS_SHUTTLE', 'Olectra', 'K6 Electric Shuttle', 120.00, 80.00, 'GB_T_DC', 'ACTIVE'),
(4, 'KA-05-MG-4411', 4, '4-WHEELER_SUV', 'MG', 'ZS EV Long Range', 50.30, 50.00, 'CCS_2_DC', 'ACTIVE'),
(5, 'KA-04-OL-8123', 5, '2-WHEELER_SCOOTER', 'Ola', 'S1 Pro Gen 2', 4.00, 3.30, 'BHARAT_AC_001', 'ACTIVE'),
(6, 'KA-01-VAN-005', 6, 'FACILITY_UTILITY_VAN', 'Mahindra', 'e-Supro Cargo', 20.00, 7.40, 'TYPE_2_AC', 'ACTIVE'),
(7, 'DL-01-HY-5520', 7, '4-WHEELER_SEDAN', 'Hyundai', 'Ioniq 5 Ultra', 72.60, 150.00, 'CCS_2_DC', 'ACTIVE');

-- ---------------------------------------------------------------------
-- 4. CHARGING STATIONS
-- ---------------------------------------------------------------------
INSERT INTO charging_stations (station_id, station_code, station_name, campus_zone, location_description, total_points, max_grid_capacity_kw, operating_status, solar_powered, latitude, longitude) VALUES
(1, 'STN-MAIN-01', 'Main Academic Block Hub', 'Central Campus', 'Behind Main Admin & Lecture Halls Complex', 4, 150.00, 'OPERATIONAL', TRUE, 12.971600, 77.594600),
(2, 'STN-LIB-02', 'Central Library Green Plaza', 'North Quad', 'Adjacent to University Central Library Canopy', 2, 60.00, 'OPERATIONAL', TRUE, 12.972500, 77.595200),
(3, 'STN-HSTL-03', 'Hostel Zone Energy Station', 'South Campus', 'Between Boys Hostel-B and Girls Hostel-C', 3, 75.00, 'OPERATIONAL', FALSE, 12.969800, 77.593800),
(4, 'STN-RSRCH-04', 'Research & Innovation Park', 'West Wing', 'Near Advanced Photonics & EV Labs', 3, 120.00, 'OPERATIONAL', TRUE, 12.973200, 77.592100),
(5, 'STN-SPRT-05', 'Sports Complex & Stadium Arena', 'East Zone', 'Indoor Sports Complex & Gymnasium Parking', 2, 50.00, 'OPERATIONAL', FALSE, 12.970500, 77.597500),
(6, 'STN-PARK-06', 'Main Campus Multi-Level Parking', 'South-East Gate', 'Ground Deck dedicated EV Charging Zone', 4, 200.00, 'OPERATIONAL', TRUE, 12.968900, 77.596000);

-- ---------------------------------------------------------------------
-- 5. CHARGING POINTS
-- ---------------------------------------------------------------------
INSERT INTO charging_points (point_id, point_code, station_id, point_number, connector_type, power_rating_kw, status, is_fast_charger, hardware_model, last_service_date) VALUES
-- Main Academic Block (STN-01)
(1, 'CP-01', 1, 1, 'CCS_2_DC', 60.00, 'OCCUPIED', TRUE, 'Delta SmartDC-60', '2026-08-15'),
(2, 'CP-02', 1, 2, 'TYPE_2_AC', 22.00, 'AVAILABLE', FALSE, 'Schneider EVlink 22kW', '2026-08-15'),
(3, 'CP-03', 1, 3, 'CCS_2_DC', 50.00, 'RESERVED', TRUE, 'ABB Terra 54 DC', '2026-08-10'),
(4, 'CP-04', 1, 4, 'BHARAT_AC_001', 3.30, 'AVAILABLE', FALSE, 'Exicom Bharat AC', '2026-07-28'),

-- Central Library (STN-02)
(5, 'CP-05', 2, 1, 'TYPE_2_AC', 22.00, 'OCCUPIED', FALSE, 'Schneider EVlink 22kW', '2026-08-01'),
(6, 'CP-06', 2, 2, 'BHARAT_AC_001', 3.30, 'AVAILABLE', FALSE, 'Exicom Bharat AC', '2026-08-01'),

-- Hostel Zone (STN-03)
(7, 'CP-07', 3, 1, 'TYPE_2_AC', 11.00, 'AVAILABLE', FALSE, 'Siemens VersiCharge', '2026-08-18'),
(8, 'CP-08', 3, 2, 'BHARAT_AC_001', 3.30, 'OCCUPIED', FALSE, 'Exicom Bharat AC', '2026-08-18'),
(9, 'CP-09', 3, 3, 'TYPE_2_AC', 7.40, 'MAINTENANCE', FALSE, 'Siemens VersiCharge', '2026-08-25'),

-- Research Park (STN-04)
(10, 'CP-10', 4, 1, 'CCS_2_DC', 60.00, 'AVAILABLE', TRUE, 'Delta UltraFast 60', '2026-08-20'),
(11, 'CP-11', 4, 2, 'GB_T_DC', 80.00, 'OCCUPIED', TRUE, 'StarCharge GB/T Fast', '2026-08-12'),
(12, 'CP-12', 4, 3, 'CHADEMO', 50.00, 'AVAILABLE', TRUE, 'ABB Terra DC Multi', '2026-08-12'),

-- Sports Complex (STN-05)
(13, 'CP-13', 5, 1, 'TYPE_2_AC', 22.00, 'AVAILABLE', FALSE, 'Schneider EVlink 22kW', '2026-07-20'),
(14, 'CP-14', 5, 2, 'BHARAT_AC_001', 3.30, 'AVAILABLE', FALSE, 'Exicom Bharat AC', '2026-07-20'),

-- Multi-Level Parking (STN-06)
(15, 'CP-15', 6, 1, 'CCS_2_DC', 120.00, 'AVAILABLE', TRUE, 'Tritium RTM 120 HighPower', '2026-08-22'),
(16, 'CP-16', 6, 2, 'CCS_2_DC', 60.00, 'AVAILABLE', TRUE, 'Delta SmartDC-60', '2026-08-22'),
(17, 'CP-17', 6, 3, 'TYPE_2_AC', 22.00, 'AVAILABLE', FALSE, 'Schneider EVlink 22kW', '2026-08-22'),
(18, 'CP-18', 6, 4, 'BHARAT_AC_001', 3.30, 'AVAILABLE', FALSE, 'Exicom Bharat AC', '2026-08-22');

-- ---------------------------------------------------------------------
-- 6. RESERVATIONS
-- ---------------------------------------------------------------------
INSERT INTO reservations (reservation_id, reservation_code, user_id, vehicle_id, point_id, station_id, start_time, end_time, estimated_kwh, status) VALUES
(1, 'RES-20260901-001', 4, 4, 3, 1, '2026-09-01 10:00:00', '2026-09-01 11:30:00', 25.00, 'CONFIRMED'),
(2, 'RES-20260901-002', 2, 2, 6, 2, '2026-09-01 14:00:00', '2026-09-01 15:30:00', 3.00, 'CONFIRMED'),
(3, 'RES-20260901-003', 7, 7, 15, 6, '2026-09-01 16:00:00', '2026-09-01 17:00:00', 40.00, 'CONFIRMED'),
(4, 'RES-20260831-089', 1, 1, 1, 1, '2026-08-31 09:00:00', '2026-08-31 10:15:00', 28.50, 'COMPLETED'),
(5, 'RES-20260831-090', 5, 5, 8, 3, '2026-08-31 13:00:00', '2026-08-31 14:30:00', 3.50, 'COMPLETED');

-- ---------------------------------------------------------------------
-- 7. CHARGING SESSIONS
-- ---------------------------------------------------------------------
-- Active Live Sessions
INSERT INTO charging_sessions (session_id, session_code, reservation_id, point_id, vehicle_id, user_id, tariff_id, start_time, end_time, duration_minutes, initial_soc_percent, final_soc_percent, total_energy_kwh, peak_power_kw, energy_cost, parking_fee, total_amount, status) VALUES
(1, 'SES-20260901-1001', NULL, 1, 1, 1, 1, '2026-09-01 08:30:00', NULL, 45, 25, 65, 16.250, 48.50, 121.88, 0.00, 121.88, 'CHARGING'),
(2, 'SES-20260901-1002', NULL, 5, 6, 6, 2, '2026-09-01 08:45:00', NULL, 30, 40, 72, 6.400, 7.20, 28.80, 0.00, 28.80, 'CHARGING'),
(3, 'SES-20260901-1003', NULL, 8, 5, 5, 1, '2026-09-01 08:50:00', NULL, 25, 15, 55, 1.850, 3.20, 13.88, 0.00, 13.88, 'CHARGING'),
(4, 'SES-20260901-1004', NULL, 11, 3, 3, 4, '2026-09-01 07:15:00', NULL, 120, 20, 85, 65.000, 75.00, 0.00, 0.00, 0.00, 'CHARGING'),

-- Historical Completed Sessions
(5, 'SES-20260831-0988', 4, 1, 1, 1, 1, '2026-08-31 09:05:00', '2026-08-31 10:15:00', 70, 30, 88, 26.400, 50.00, 198.00, 10.00, 208.00, 'COMPLETED'),
(6, 'SES-20260831-0989', NULL, 10, 7, 7, 1, '2026-08-31 11:00:00', '2026-08-31 12:10:00', 70, 20, 90, 48.200, 58.00, 361.50, 10.00, 371.50, 'COMPLETED'),
(7, 'SES-20260831-0990', 5, 8, 5, 5, 2, '2026-08-31 13:05:00', '2026-08-31 14:25:00', 80, 10, 95, 3.400, 3.30, 15.30, 0.00, 15.30, 'COMPLETED'),
(8, 'SES-20260830-0975', NULL, 2, 4, 4, 2, '2026-08-30 14:00:00', '2026-08-30 16:30:00', 150, 25, 85, 32.000, 21.00, 144.00, 20.00, 164.00, 'COMPLETED'),
(9, 'SES-20260830-0976', NULL, 11, 3, 3, 4, '2026-08-30 16:00:00', '2026-08-30 18:00:00', 120, 15, 90, 72.500, 78.00, 0.00, 0.00, 0.00, 'COMPLETED');

-- ---------------------------------------------------------------------
-- 8. ENERGY USAGE TELEMETRY SAMPLES
-- ---------------------------------------------------------------------
INSERT INTO energy_usage (session_id, reading_timestamp, instant_voltage_v, instant_current_a, instant_power_kw, cumulative_kwh, battery_temp_celsius) VALUES
(1, '2026-09-01 08:35:00', 398.50, 120.20, 47.90, 3.990, 29.5),
(1, '2026-09-01 08:50:00', 400.10, 121.50, 48.61, 11.850, 33.2),
(1, '2026-09-01 09:15:00', 402.00, 118.00, 47.43, 16.250, 35.8),
(2, '2026-09-01 09:00:00', 230.20, 31.00, 7.14, 3.570, 30.1),
(2, '2026-09-01 09:15:00', 229.80, 31.20, 7.17, 6.400, 32.0);

-- ---------------------------------------------------------------------
-- 9. BILLING & PAYMENTS
-- ---------------------------------------------------------------------
INSERT INTO billing_payments (payment_id, invoice_number, session_id, user_id, amount, payment_method, transaction_ref, payment_status, payment_time) VALUES
(1, 'INV-20260831-001', 5, 1, 208.00, 'CAMPUS_WALLET', 'TXN-CW-88192039', 'PAID', '2026-08-31 10:16:12'),
(2, 'INV-20260831-002', 6, 7, 371.50, 'UPI_QR', 'TXN-UPI-992018273', 'PAID', '2026-08-31 12:11:05'),
(3, 'INV-20260831-003', 7, 5, 15.30, 'SMART_ID_CARD', 'TXN-IDC-44102938', 'PAID', '2026-08-31 14:26:40'),
(4, 'INV-20260830-001', 8, 4, 164.00, 'CAMPUS_WALLET', 'TXN-CW-77192834', 'PAID', '2026-08-30 16:32:00'),
(5, 'INV-20260830-002', 9, 3, 0.00, 'WAIVED_FLEET', 'TXN-FLT-INTERNAL-009', 'PAID', '2026-08-30 18:01:00');

-- ---------------------------------------------------------------------
-- 10. REAL-TIME ACTIVITY LOGS
-- ---------------------------------------------------------------------
INSERT INTO activity_logs (log_id, event_type, description, point_code, user_code, vehicle_number, severity, logged_at) VALUES
(1, 'SESSION_STARTED', 'Charging session #SES-20260901-1001 started on CP-01', 'CP-01', 'USR-FAC-101', 'KA-01-EV-2024', 'SUCCESS', '2026-09-01 08:30:00'),
(2, 'SESSION_STARTED', 'Charging session #SES-20260901-1002 started on CP-05', 'CP-05', 'USR-STF-088', 'KA-01-VAN-005', 'SUCCESS', '2026-09-01 08:45:00'),
(3, 'RESERVATION_CREATED', 'Reservation confirmed for Prof. Meera on CP-03 (10:00-11:30)', 'CP-03', 'USR-FAC-305', 'KA-05-MG-4411', 'INFO', '2026-09-01 08:48:15'),
(4, 'SESSION_STARTED', 'Charging session #SES-20260901-1003 started on CP-08', 'CP-08', 'USR-STU-412', 'KA-04-OL-8123', 'SUCCESS', '2026-09-01 08:50:00'),
(5, 'MAINTENANCE_FLAG', 'CP-09 connector lock sensor flag - moved to maintenance mode', 'CP-09', 'SYSTEM', NULL, 'WARNING', '2026-09-01 08:55:00'),
(6, 'GRID_SOLAR_ACTIVE', 'Solar Rooftop generation online: supplying 45 kW to North Quad', 'STN-LIB-02', 'SYSTEM', NULL, 'INFO', '2026-09-01 09:00:00');
