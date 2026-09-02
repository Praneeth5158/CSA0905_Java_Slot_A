-- =====================================================================
-- SMART CAMPUS EV CHARGING MANAGEMENT SYSTEM
-- MySQL Stored Procedures for JDBC CallableStatement
-- =====================================================================

USE campus_ev_db;

DELIMITER $$

-- ---------------------------------------------------------------------
-- 1. Station Utilization Stored Procedure
-- Invoked via CallableStatement in Analytics & Reporting
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_get_station_utilization $$

CREATE PROCEDURE sp_get_station_utilization(IN p_station_id INT)
BEGIN
    IF p_station_id IS NOT NULL AND p_station_id > 0 THEN
        SELECT 
            s.station_id,
            s.station_code,
            s.station_name,
            s.campus_zone,
            COUNT(DISTINCT cp.point_id) AS total_points,
            SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_points,
            SUM(CASE WHEN cp.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_points,
            SUM(CASE WHEN cp.status = 'RESERVED' THEN 1 ELSE 0 END) AS reserved_points,
            SUM(CASE WHEN cp.status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_points,
            COUNT(DISTINCT cs.session_id) AS total_lifetime_sessions,
            COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_delivered_kwh,
            ROUND(
                (SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) * 100.0) / 
                NULLIF(COUNT(DISTINCT cp.point_id), 0), 
                2
            ) AS current_utilization_percent
        FROM charging_stations s
        LEFT JOIN charging_points cp ON s.station_id = cp.station_id
        LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id
        WHERE s.station_id = p_station_id
        GROUP BY s.station_id, s.station_code, s.station_name, s.campus_zone;
    ELSE
        SELECT 
            s.station_id,
            s.station_code,
            s.station_name,
            s.campus_zone,
            COUNT(DISTINCT cp.point_id) AS total_points,
            SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_points,
            SUM(CASE WHEN cp.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_points,
            SUM(CASE WHEN cp.status = 'RESERVED' THEN 1 ELSE 0 END) AS reserved_points,
            SUM(CASE WHEN cp.status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_points,
            COUNT(DISTINCT cs.session_id) AS total_lifetime_sessions,
            COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_delivered_kwh,
            ROUND(
                (SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) * 100.0) / 
                NULLIF(COUNT(DISTINCT cp.point_id), 0), 
                2
            ) AS current_utilization_percent
        FROM charging_stations s
        LEFT JOIN charging_points cp ON s.station_id = cp.station_id
        LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id
        GROUP BY s.station_id, s.station_code, s.station_name, s.campus_zone
        ORDER BY s.station_id ASC;
    END IF;
END $$

-- ---------------------------------------------------------------------
-- 2. Session Billing Calculation Stored Procedure (IN & OUT Parameters)
-- Demonstrates CallableStatement parameter binding and retrieval
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_calculate_session_billing $$

CREATE PROCEDURE sp_calculate_session_billing(
    IN p_session_id INT,
    OUT p_energy_kwh DECIMAL(7,3),
    OUT p_rate_per_kwh DECIMAL(6,2),
    OUT p_energy_cost DECIMAL(8,2),
    OUT p_parking_fee DECIMAL(8,2),
    OUT p_total_amount DECIMAL(8,2)
)
BEGIN
    DECLARE v_duration INT DEFAULT 0;
    DECLARE v_base_parking DECIMAL(6,2) DEFAULT 0.00;
    DECLARE v_multiplier DECIMAL(4,2) DEFAULT 1.00;

    SELECT 
        COALESCE(cs.total_energy_kwh, 0.000),
        COALESCE(t.rate_per_kwh, 7.50),
        COALESCE(t.base_parking_fee_per_hour, 0.00),
        COALESCE(t.peak_hour_multiplier, 1.00),
        COALESCE(TIMESTAMPDIFF(MINUTE, cs.start_time, COALESCE(cs.end_time, NOW())), 0)
    INTO 
        p_energy_kwh, 
        p_rate_per_kwh, 
        v_base_parking, 
        v_multiplier, 
        v_duration
    FROM charging_sessions cs
    JOIN tariffs t ON cs.tariff_id = t.tariff_id
    WHERE cs.session_id = p_session_id;

    -- Compute cost
    SET p_energy_cost = ROUND(p_energy_kwh * p_rate_per_kwh * v_multiplier, 2);
    
    IF v_duration > 60 THEN
        SET p_parking_fee = ROUND(((v_duration - 60) / 60.0) * v_base_parking, 2);
    ELSE
        SET p_parking_fee = 0.00;
    END IF;

    SET p_total_amount = p_energy_cost + p_parking_fee;
END $$

-- ---------------------------------------------------------------------
-- 3. Campus Energy Summary Stored Procedure
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_generate_campus_energy_summary $$

CREATE PROCEDURE sp_generate_campus_energy_summary(IN p_days_back INT)
BEGIN
    DECLARE v_cutoff DATETIME;
    SET v_cutoff = DATE_SUB(NOW(), INTERVAL COALESCE(p_days_back, 30) DAY);

    SELECT 
        COUNT(DISTINCT cs.session_id) AS total_sessions,
        COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_kwh,
        COALESCE(SUM(cs.total_amount), 0.00) AS total_revenue,
        COUNT(DISTINCT cs.user_id) AS unique_active_users,
        COUNT(DISTINCT cs.vehicle_id) AS unique_vehicles,
        COALESCE(AVG(TIMESTAMPDIFF(MINUTE, cs.start_time, COALESCE(cs.end_time, NOW()))), 0) AS avg_duration_mins,
        -- Carbon Offset Calculation: approx 0.82 kg CO2 avoided per kWh of EV vs ICE vehicle
        ROUND(COALESCE(SUM(cs.total_energy_kwh), 0.000) * 0.82, 2) AS carbon_offset_kg_co2
    FROM charging_sessions cs
    WHERE cs.start_time >= v_cutoff;
END $$

DELIMITER ;
