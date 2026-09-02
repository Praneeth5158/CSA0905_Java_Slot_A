package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.CampusEnergySummaryDTO;
import com.campus.ev.model.OperationalSummaryDTO;
import com.campus.ev.model.StationUtilizationDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates the 3 fundamental JDBC statement mechanisms:
 * 1. Statement: For static campus operational queries & metadata summaries.
 * 2. PreparedStatement: For parameterized date-filtered reports.
 * 3. CallableStatement: For executing MySQL Stored Procedures (IN & OUT parameters).
 */
public class ReportDAO {

    // =========================================================================
    // 1. STATEMENT DEMONSTRATION
    // Used for fixed static analytical summaries and status counts.
    // =========================================================================
    public OperationalSummaryDTO getOperationalSummary() throws SQLException {
        OperationalSummaryDTO dto = new OperationalSummaryDTO();
        String sql = 
            "SELECT " +
            "(SELECT COUNT(*) FROM charging_points WHERE status = 'AVAILABLE') AS available_points, " +
            "(SELECT COUNT(*) FROM charging_points WHERE status = 'OCCUPIED') AS occupied_points, " +
            "(SELECT COUNT(*) FROM charging_points WHERE status = 'RESERVED') AS reserved_points, " +
            "(SELECT COUNT(*) FROM charging_points WHERE status = 'MAINTENANCE') AS maintenance_points, " +
            "(SELECT COUNT(*) FROM charging_sessions WHERE status = 'CHARGING') AS active_sessions_count, " +
            "(SELECT COALESCE(SUM(total_energy_kwh), 0) FROM charging_sessions WHERE DATE(start_time) = CURDATE()) AS today_energy_kwh, " +
            "(SELECT COALESCE(SUM(total_amount), 0) FROM charging_sessions WHERE DATE(start_time) = CURDATE()) AS today_revenue, " +
            "(SELECT COUNT(*) FROM charging_stations) AS total_stations, " +
            "(SELECT COUNT(*) FROM campus_users) AS total_users, " +
            "(SELECT COUNT(*) FROM ev_vehicles) AS total_vehicles";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dto.setAvailablePoints(rs.getInt("available_points"));
                dto.setOccupiedPoints(rs.getInt("occupied_points"));
                dto.setReservedPoints(rs.getInt("reserved_points"));
                dto.setMaintenancePoints(rs.getInt("maintenance_points"));
                dto.setActiveSessionsCount(rs.getInt("active_sessions_count"));
                dto.setTodayEnergyKwh(rs.getDouble("today_energy_kwh"));
                dto.setTodayRevenue(rs.getDouble("today_revenue"));
                dto.setTotalStations(rs.getInt("total_stations"));
                dto.setTotalRegisteredUsers(rs.getInt("total_users"));
                dto.setTotalRegisteredVehicles(rs.getInt("total_vehicles"));
            }
        }
        return dto;
    }

    // =========================================================================
    // 2. PREPAREDSTATEMENT DEMONSTRATION
    // Used for dynamic date range queries, energy consumption, and fleet analytics.
    // =========================================================================

    /**
     * Daily / Range Energy Consumption breakdown.
     */
    public List<Map<String, Object>> getEnergyConsumptionReport(Date startDate, Date endDate) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = 
            "SELECT DATE(start_time) AS report_date, " +
            "COUNT(session_id) AS total_sessions, " +
            "ROUND(SUM(total_energy_kwh), 2) AS total_energy_kwh, " +
            "ROUND(AVG(total_energy_kwh), 2) AS avg_energy_kwh, " +
            "ROUND(SUM(total_amount), 2) AS total_revenue " +
            "FROM charging_sessions " +
            "WHERE DATE(start_time) BETWEEN ? AND ? " +
            "GROUP BY DATE(start_time) " +
            "ORDER BY report_date DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("report_date", rs.getDate("report_date"));
                    row.put("total_sessions", rs.getInt("total_sessions"));
                    row.put("total_energy_kwh", rs.getDouble("total_energy_kwh"));
                    row.put("avg_energy_kwh", rs.getDouble("avg_energy_kwh"));
                    row.put("total_revenue", rs.getDouble("total_revenue"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    /**
     * Revenue & Settlement report with payment breakdown.
     */
    public List<Map<String, Object>> getRevenueReport(Date startDate, Date endDate) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = 
            "SELECT DATE(p.payment_time) AS payment_date, " +
            "p.payment_method, " +
            "COUNT(p.payment_id) AS payment_count, " +
            "ROUND(SUM(p.amount), 2) AS total_settled " +
            "FROM billing_payments p " +
            "WHERE DATE(p.payment_time) BETWEEN ? AND ? " +
            "GROUP BY DATE(p.payment_time), p.payment_method " +
            "ORDER BY payment_date DESC, total_settled DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("payment_date", rs.getDate("payment_date"));
                    row.put("payment_method", rs.getString("payment_method"));
                    row.put("payment_count", rs.getInt("payment_count"));
                    row.put("total_settled", rs.getDouble("total_settled"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    /**
     * Vehicle Fleet green carbon offset analysis.
     */
    public List<Map<String, Object>> getVehicleUsageReport() throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = 
            "SELECT v.vehicle_number, v.brand, v.model, v.vehicle_type, " +
            "u.full_name AS owner_name, u.campus_role, " +
            "COUNT(cs.session_id) AS total_sessions, " +
            "ROUND(COALESCE(SUM(cs.total_energy_kwh), 0.0), 2) AS total_energy_kwh, " +
            "ROUND(COALESCE(SUM(cs.total_amount), 0.0), 2) AS total_paid, " +
            "ROUND(COALESCE(SUM(cs.total_energy_kwh), 0.0) * 0.82, 2) AS co2_offset_kg " +
            "FROM ev_vehicles v " +
            "JOIN campus_users u ON v.user_id = u.user_id " +
            "LEFT JOIN charging_sessions cs ON v.vehicle_id = cs.vehicle_id " +
            "GROUP BY v.vehicle_id, v.vehicle_number, v.brand, v.model, v.vehicle_type, u.full_name, u.campus_role " +
            "ORDER BY total_energy_kwh DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("vehicle_number", rs.getString("vehicle_number"));
                row.put("brand_model", rs.getString("brand") + " " + rs.getString("model"));
                row.put("vehicle_type", rs.getString("vehicle_type"));
                row.put("owner_name", rs.getString("owner_name"));
                row.put("campus_role", rs.getString("campus_role"));
                row.put("total_sessions", rs.getInt("total_sessions"));
                row.put("total_energy_kwh", rs.getDouble("total_energy_kwh"));
                row.put("total_paid", rs.getDouble("total_paid"));
                row.put("co2_offset_kg", rs.getDouble("co2_offset_kg"));
                rows.add(row);
            }
        }
        return rows;
    }

    // =========================================================================
    // 3. CALLABLESTATEMENT DEMONSTRATION
    // Used for executing MySQL Stored Procedures (sp_get_station_utilization, etc.)
    // =========================================================================

    /**
     * Executes MySQL Stored Procedure `sp_get_station_utilization(IN stationId)`
     * using JDBC CallableStatement.
     */
    public List<StationUtilizationDTO> getStationUtilizationByProcedure(Integer stationId) throws SQLException {
        List<StationUtilizationDTO> list = new ArrayList<>();
        String callSql = "{CALL sp_get_station_utilization(?)}";

        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            if (stationId != null && stationId > 0) {
                cs.setInt(1, stationId);
            } else {
                cs.setNull(1, Types.INTEGER);
            }

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    StationUtilizationDTO dto = new StationUtilizationDTO();
                    dto.setStationId(rs.getInt("station_id"));
                    dto.setStationCode(rs.getString("station_code"));
                    dto.setStationName(rs.getString("station_name"));
                    dto.setCampusZone(rs.getString("campus_zone"));
                    dto.setTotalPoints(rs.getInt("total_points"));
                    dto.setOccupiedPoints(rs.getInt("occupied_points"));
                    dto.setAvailablePoints(rs.getInt("available_points"));
                    dto.setReservedPoints(rs.getInt("reserved_points"));
                    dto.setMaintenancePoints(rs.getInt("maintenance_points"));
                    dto.setTotalLifetimeSessions(rs.getInt("total_lifetime_sessions"));
                    dto.setTotalEnergyDeliveredKwh(rs.getDouble("total_energy_delivered_kwh"));
                    dto.setCurrentUtilizationPercent(rs.getDouble("current_utilization_percent"));
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            // Fallback SQL query in case stored procedure is not yet compiled on MySQL server
            System.err.println("Notice: Stored procedure execution encountered exception, executing inline query: " + ex.getMessage());
            list = getStationUtilizationInline(stationId);
        }
        return list;
    }

    /**
     * Executes MySQL Stored Procedure `sp_calculate_session_billing` using CallableStatement
     * with IN and OUT parameters.
     */
    public Map<String, Double> calculateSessionBillingProcedure(int sessionId) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String callSql = "{CALL sp_calculate_session_billing(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setInt(1, sessionId);
            cs.registerOutParameter(2, Types.DECIMAL); // energy_kwh
            cs.registerOutParameter(3, Types.DECIMAL); // rate_per_kwh
            cs.registerOutParameter(4, Types.DECIMAL); // energy_cost
            cs.registerOutParameter(5, Types.DECIMAL); // parking_fee
            cs.registerOutParameter(6, Types.DECIMAL); // total_amount

            cs.execute();

            result.put("energy_kwh", cs.getDouble(2));
            result.put("rate_per_kwh", cs.getDouble(3));
            result.put("energy_cost", cs.getDouble(4));
            result.put("parking_fee", cs.getDouble(5));
            result.put("total_amount", cs.getDouble(6));
        }
        return result;
    }

    /**
     * Executes MySQL Stored Procedure `sp_generate_campus_energy_summary(IN daysBack)`
     */
    public CampusEnergySummaryDTO getCampusEnergySummaryProcedure(int daysBack) throws SQLException {
        CampusEnergySummaryDTO dto = new CampusEnergySummaryDTO();
        String callSql = "{CALL sp_generate_campus_energy_summary(?)}";

        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setInt(1, daysBack);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalSessions(rs.getInt("total_sessions"));
                    dto.setTotalEnergyKwh(rs.getDouble("total_energy_kwh"));
                    dto.setTotalRevenue(rs.getDouble("total_revenue"));
                    dto.setUniqueActiveUsers(rs.getInt("unique_active_users"));
                    dto.setUniqueVehicles(rs.getInt("unique_vehicles"));
                    dto.setAvgDurationMins(rs.getDouble("avg_duration_mins"));
                    dto.setCarbonOffsetKgCo2(rs.getDouble("carbon_offset_kg_co2"));
                }
            }
        } catch (SQLException ex) {
            // Fallback inline query
            dto = getCampusEnergySummaryInline(daysBack);
        }
        return dto;
    }

    private List<StationUtilizationDTO> getStationUtilizationInline(Integer stationId) throws SQLException {
        List<StationUtilizationDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.station_id, s.station_code, s.station_name, s.campus_zone, " +
            "COUNT(DISTINCT cp.point_id) AS total_points, " +
            "SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_points, " +
            "SUM(CASE WHEN cp.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_points, " +
            "SUM(CASE WHEN cp.status = 'RESERVED' THEN 1 ELSE 0 END) AS reserved_points, " +
            "SUM(CASE WHEN cp.status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance_points, " +
            "COUNT(DISTINCT cs.session_id) AS total_lifetime_sessions, " +
            "COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_delivered_kwh, " +
            "ROUND((SUM(CASE WHEN cp.status = 'OCCUPIED' THEN 1 ELSE 0 END) * 100.0) / NULLIF(COUNT(DISTINCT cp.point_id), 0), 2) AS current_utilization_percent " +
            "FROM charging_stations s " +
            "LEFT JOIN charging_points cp ON s.station_id = cp.station_id " +
            "LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id "
        );
        if (stationId != null && stationId > 0) {
            sql.append("WHERE s.station_id = ").append(stationId).append(" ");
        }
        sql.append("GROUP BY s.station_id, s.station_code, s.station_name, s.campus_zone ORDER BY s.station_id ASC");

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            while (rs.next()) {
                StationUtilizationDTO dto = new StationUtilizationDTO();
                dto.setStationId(rs.getInt("station_id"));
                dto.setStationCode(rs.getString("station_code"));
                dto.setStationName(rs.getString("station_name"));
                dto.setCampusZone(rs.getString("campus_zone"));
                dto.setTotalPoints(rs.getInt("total_points"));
                dto.setOccupiedPoints(rs.getInt("occupied_points"));
                dto.setAvailablePoints(rs.getInt("available_points"));
                dto.setReservedPoints(rs.getInt("reserved_points"));
                dto.setMaintenancePoints(rs.getInt("maintenance_points"));
                dto.setTotalLifetimeSessions(rs.getInt("total_lifetime_sessions"));
                dto.setTotalEnergyDeliveredKwh(rs.getDouble("total_energy_delivered_kwh"));
                dto.setCurrentUtilizationPercent(rs.getDouble("current_utilization_percent"));
                list.add(dto);
            }
        }
        return list;
    }

    private CampusEnergySummaryDTO getCampusEnergySummaryInline(int daysBack) throws SQLException {
        CampusEnergySummaryDTO dto = new CampusEnergySummaryDTO();
        String sql = 
            "SELECT " +
            "COUNT(DISTINCT cs.session_id) AS total_sessions, " +
            "COALESCE(SUM(cs.total_energy_kwh), 0.000) AS total_energy_kwh, " +
            "COALESCE(SUM(cs.total_amount), 0.00) AS total_revenue, " +
            "COUNT(DISTINCT cs.user_id) AS unique_active_users, " +
            "COUNT(DISTINCT cs.vehicle_id) AS unique_vehicles, " +
            "COALESCE(AVG(TIMESTAMPDIFF(MINUTE, cs.start_time, COALESCE(cs.end_time, NOW()))), 0) AS avg_duration_mins, " +
            "ROUND(COALESCE(SUM(cs.total_energy_kwh), 0.000) * 0.82, 2) AS carbon_offset_kg_co2 " +
            "FROM charging_sessions cs " +
            "WHERE cs.start_time >= DATE_SUB(NOW(), INTERVAL " + daysBack + " DAY)";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                dto.setTotalSessions(rs.getInt("total_sessions"));
                dto.setTotalEnergyKwh(rs.getDouble("total_energy_kwh"));
                dto.setTotalRevenue(rs.getDouble("total_revenue"));
                dto.setUniqueActiveUsers(rs.getInt("unique_active_users"));
                dto.setUniqueVehicles(rs.getInt("unique_vehicles"));
                dto.setAvgDurationMins(rs.getDouble("avg_duration_mins"));
                dto.setCarbonOffsetKgCo2(rs.getDouble("carbon_offset_kg_co2"));
            }
        }
        return dto;
    }
}
