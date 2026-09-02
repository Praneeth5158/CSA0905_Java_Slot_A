-- =====================================================================
-- SMART CAMPUS EV CHARGING MANAGEMENT SYSTEM
-- Relational Database Schema DDL (MySQL 8.0+)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS campus_ev_db;
USE campus_ev_db;

-- Drop tables in reverse foreign key order if re-initializing
DROP TABLE IF EXISTS billing_payments;
DROP TABLE IF EXISTS energy_usage;
DROP TABLE IF EXISTS charging_sessions;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS charging_points;
DROP TABLE IF EXISTS charging_stations;
DROP TABLE IF EXISTS ev_vehicles;
DROP TABLE IF EXISTS campus_users;
DROP TABLE IF EXISTS tariffs;
DROP TABLE IF EXISTS activity_logs;

-- ---------------------------------------------------------------------
-- 1. CAMPUS USERS
-- ---------------------------------------------------------------------
CREATE TABLE campus_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(80) NOT NULL,
    campus_role ENUM('STUDENT', 'FACULTY', 'CAMPUS_FLEET', 'FACILITY_STAFF', 'VISITOR') NOT NULL DEFAULT 'STUDENT',
    status ENUM('ACTIVE', 'SUSPENDED', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    rfid_card_uid VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 2. EV VEHICLES
-- ---------------------------------------------------------------------
CREATE TABLE ev_vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(25) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    vehicle_type ENUM('2-WHEELER_SCOOTER', '4-WHEELER_SEDAN', '4-WHEELER_SUV', 'CAMPUS_BUS_SHUTTLE', 'FACILITY_UTILITY_VAN') NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    battery_capacity_kwh DECIMAL(6,2) NOT NULL,
    max_charge_rate_kw DECIMAL(5,2) NOT NULL,
    connector_type ENUM('TYPE_2_AC', 'CCS_2_DC', 'CHADEMO', 'GB_T_DC', 'BHARAT_AC_001') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES campus_users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 3. CHARGING STATIONS (Campus Hubs)
-- ---------------------------------------------------------------------
CREATE TABLE charging_stations (
    station_id INT AUTO_INCREMENT PRIMARY KEY,
    station_code VARCHAR(20) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    campus_zone VARCHAR(80) NOT NULL,
    location_description VARCHAR(255),
    total_points INT NOT NULL DEFAULT 1,
    max_grid_capacity_kw DECIMAL(7,2) NOT NULL,
    operating_status ENUM('OPERATIONAL', 'DEGRADED', 'OFFLINE', 'MAINTENANCE') NOT NULL DEFAULT 'OPERATIONAL',
    solar_powered BOOLEAN NOT NULL DEFAULT FALSE,
    latitude DECIMAL(10,6),
    longitude DECIMAL(10,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 4. CHARGING POINTS (Dispenser Nodes)
-- ---------------------------------------------------------------------
CREATE TABLE charging_points (
    point_id INT AUTO_INCREMENT PRIMARY KEY,
    point_code VARCHAR(20) NOT NULL UNIQUE,
    station_id INT NOT NULL,
    point_number INT NOT NULL,
    connector_type ENUM('TYPE_2_AC', 'CCS_2_DC', 'CHADEMO', 'GB_T_DC', 'BHARAT_AC_001') NOT NULL,
    power_rating_kw DECIMAL(6,2) NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    is_fast_charger BOOLEAN NOT NULL DEFAULT FALSE,
    hardware_model VARCHAR(80),
    last_service_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (station_id) REFERENCES charging_stations(station_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE KEY uk_station_point (station_id, point_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 5. TARIFFS
-- ---------------------------------------------------------------------
CREATE TABLE tariffs (
    tariff_id INT AUTO_INCREMENT PRIMARY KEY,
    tariff_code VARCHAR(25) NOT NULL UNIQUE,
    tariff_name VARCHAR(100) NOT NULL,
    rate_per_kwh DECIMAL(6,2) NOT NULL,
    base_parking_fee_per_hour DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    peak_hour_multiplier DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    effective_from DATE NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'PENDING') NOT NULL DEFAULT 'ACTIVE',
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 6. RESERVATIONS
-- ---------------------------------------------------------------------
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_code VARCHAR(30) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    point_id INT NOT NULL,
    station_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    estimated_kwh DECIMAL(6,2) NOT NULL DEFAULT 10.00,
    status ENUM('CONFIRMED', 'CHECKED_IN', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES campus_users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES ev_vehicles(vehicle_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (point_id) REFERENCES charging_points(point_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (station_id) REFERENCES charging_stations(station_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 7. CHARGING SESSIONS
-- ---------------------------------------------------------------------
CREATE TABLE charging_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    session_code VARCHAR(30) NOT NULL UNIQUE,
    reservation_id INT NULL,
    point_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    user_id INT NOT NULL,
    tariff_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NULL,
    duration_minutes INT DEFAULT 0,
    initial_soc_percent INT DEFAULT 20,
    final_soc_percent INT DEFAULT 80,
    total_energy_kwh DECIMAL(7,3) NOT NULL DEFAULT 0.000,
    peak_power_kw DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    energy_cost DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    parking_fee DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    status ENUM('CHARGING', 'PAUSED', 'COMPLETED', 'STOPPED_USER', 'FAULT_STOPPED') NOT NULL DEFAULT 'CHARGING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (point_id) REFERENCES charging_points(point_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES ev_vehicles(vehicle_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES campus_users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (tariff_id) REFERENCES tariffs(tariff_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 8. ENERGY USAGE (Telemetry samples per session)
-- ---------------------------------------------------------------------
CREATE TABLE energy_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    reading_timestamp DATETIME NOT NULL,
    instant_voltage_v DECIMAL(6,2) NOT NULL,
    instant_current_a DECIMAL(6,2) NOT NULL,
    instant_power_kw DECIMAL(6,2) NOT NULL,
    cumulative_kwh DECIMAL(8,3) NOT NULL,
    battery_temp_celsius DECIMAL(4,1) DEFAULT 32.5,
    FOREIGN KEY (session_id) REFERENCES charging_sessions(session_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 9. BILLING & PAYMENTS
-- ---------------------------------------------------------------------
CREATE TABLE billing_payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(35) NOT NULL UNIQUE,
    session_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    amount DECIMAL(8,2) NOT NULL,
    payment_method ENUM('CAMPUS_WALLET', 'UPI_QR', 'SMART_ID_CARD', 'STUDENT_PORTAL', 'WAIVED_FLEET') NOT NULL DEFAULT 'CAMPUS_WALLET',
    transaction_ref VARCHAR(80) NOT NULL,
    payment_status ENUM('PAID', 'PENDING', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PAID',
    payment_time DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES charging_sessions(session_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES campus_users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 10. REAL-TIME ACTIVITY LOGS
-- ---------------------------------------------------------------------
CREATE TABLE activity_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    point_code VARCHAR(20),
    user_code VARCHAR(20),
    vehicle_number VARCHAR(25),
    severity ENUM('INFO', 'SUCCESS', 'WARNING', 'ALERT') NOT NULL DEFAULT 'INFO',
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Performance Indexes
-- ---------------------------------------------------------------------
CREATE INDEX idx_reservations_lookup ON reservations (point_id, start_time, end_time, status);
CREATE INDEX idx_sessions_active ON charging_sessions (status, point_id);
CREATE INDEX idx_points_station ON charging_points (station_id, status);
CREATE INDEX idx_logs_timestamp ON activity_logs (logged_at DESC);
