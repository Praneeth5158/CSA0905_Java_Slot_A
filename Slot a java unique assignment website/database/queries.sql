-- =====================================================================
-- SMART CAMPUS EV CHARGING MANAGEMENT SYSTEM
-- Reference SQL Queries demonstrating Statement, PreparedStatement & CRUD
-- =====================================================================

USE campus_ev_db;

-- 1. Operational Overview Query (Statement)
SELECT 
    (SELECT COUNT(*) FROM charging_points WHERE status = 'AVAILABLE') AS available_points,
    (SELECT COUNT(*) FROM charging_points WHERE status = 'OCCUPIED') AS occupied_points,
    (SELECT COUNT(*) FROM charging_points WHERE status = 'RESERVED') AS reserved_points,
    (SELECT COUNT(*) FROM charging_points WHERE status = 'MAINTENANCE') AS maintenance_points,
    (SELECT COUNT(*) FROM charging_sessions WHERE status = 'CHARGING') AS active_sessions_count,
    (SELECT COALESCE(SUM(total_energy_kwh), 0) FROM charging_sessions WHERE DATE(start_time) = CURDATE()) AS today_energy_kwh,
    (SELECT COALESCE(SUM(total_amount), 0) FROM charging_sessions WHERE DATE(start_time) = CURDATE()) AS today_revenue;

-- 2. Charging Grid Interactive Map Data (PreparedStatement)
SELECT 
    cp.point_id,
    cp.point_code,
    cp.station_id,
    s.station_code,
    s.station_name,
    s.campus_zone,
    cp.point_number,
    cp.connector_type,
    cp.power_rating_kw,
    cp.status,
    cp.is_fast_charger,
    cs.session_id,
    cs.session_code,
    v.vehicle_number,
    u.full_name AS current_user_name,
    cs.start_time AS session_start_time,
    cs.total_energy_kwh,
    cs.total_amount
FROM charging_points cp
JOIN charging_stations s ON cp.station_id = s.station_id
LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id AND cs.status = 'CHARGING'
LEFT JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id
LEFT JOIN campus_users u ON cs.user_id = u.user_id
ORDER BY s.station_id, cp.point_number;

-- 3. Reservation Conflict Detection Query (PreparedStatement)
-- Checks if any confirmed/checked-in reservation overlaps the requested window [p_start, p_end]
SELECT COUNT(*) 
FROM reservations 
WHERE point_id = ? 
  AND status IN ('CONFIRMED', 'CHECKED_IN')
  AND (
      (start_time < ? AND end_time > ?) OR
      (start_time >= ? AND start_time < ?)
  );

-- 4. Vehicle Fleet Green Analysis Report
SELECT 
    v.vehicle_number,
    v.brand,
    v.model,
    v.vehicle_type,
    u.full_name AS owner_name,
    u.campus_role,
    COUNT(cs.session_id) AS total_sessions,
    COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_kwh,
    COALESCE(SUM(cs.total_amount), 0.00) AS total_paid_amount,
    ROUND(COALESCE(SUM(cs.total_energy_kwh), 0.000) * 0.82, 2) AS co2_offset_kg
FROM ev_vehicles v
JOIN campus_users u ON v.user_id = u.user_id
LEFT JOIN charging_sessions cs ON v.vehicle_id = cs.vehicle_id
GROUP BY v.vehicle_id, v.vehicle_number, v.brand, v.model, v.vehicle_type, u.full_name, u.campus_role
ORDER BY total_energy_kwh DESC;

-- 5. Revenue & Settlement Daily Aggregation
SELECT 
    DATE(cs.start_time) AS session_date,
    COUNT(cs.session_id) AS total_sessions,
    ROUND(SUM(cs.total_energy_kwh), 2) AS total_energy_delivered,
    ROUND(SUM(cs.energy_cost), 2) AS energy_revenue,
    ROUND(SUM(cs.parking_fee), 2) AS parking_revenue,
    ROUND(SUM(cs.total_amount), 2) AS gross_revenue
FROM charging_sessions cs
GROUP BY DATE(cs.start_time)
ORDER BY session_date DESC;
