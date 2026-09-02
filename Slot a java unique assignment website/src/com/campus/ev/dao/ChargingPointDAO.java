package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.ChargingPoint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingPointDAO {

    public List<ChargingPoint> getAllPointsWithDetails() throws SQLException {
        List<ChargingPoint> list = new ArrayList<>();
        String sql = 
            "SELECT cp.*, s.station_name, s.station_code, s.campus_zone, " +
            "cs.session_id AS active_session_id, cs.session_code AS active_session_code, " +
            "cs.start_time AS active_session_start_time, cs.total_energy_kwh AS active_energy_kwh, " +
            "cs.total_amount AS active_cost, v.vehicle_number AS active_vehicle_number, " +
            "u.full_name AS active_user_name " +
            "FROM charging_points cp " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id AND cs.status = 'CHARGING' " +
            "LEFT JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "LEFT JOIN campus_users u ON cs.user_id = u.user_id " +
            "ORDER BY s.station_id ASC, cp.point_number ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPointWithDetails(rs));
            }
        }
        return list;
    }

    public List<ChargingPoint> getPointsByStation(int stationId) throws SQLException {
        List<ChargingPoint> list = new ArrayList<>();
        String sql = "SELECT cp.*, s.station_name, s.station_code, s.campus_zone " +
                     "FROM charging_points cp " +
                     "JOIN charging_stations s ON cp.station_id = s.station_id " +
                     "WHERE cp.station_id = ? ORDER BY cp.point_number ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPoint(rs));
                }
            }
        }
        return list;
    }

    public ChargingPoint getPointById(int pointId) throws SQLException {
        String sql = 
            "SELECT cp.*, s.station_name, s.station_code, s.campus_zone, " +
            "cs.session_id AS active_session_id, cs.session_code AS active_session_code, " +
            "cs.start_time AS active_session_start_time, cs.total_energy_kwh AS active_energy_kwh, " +
            "cs.total_amount AS active_cost, v.vehicle_number AS active_vehicle_number, " +
            "u.full_name AS active_user_name " +
            "FROM charging_points cp " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "LEFT JOIN charging_sessions cs ON cp.point_id = cs.point_id AND cs.status = 'CHARGING' " +
            "LEFT JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "LEFT JOIN campus_users u ON cs.user_id = u.user_id " +
            "WHERE cp.point_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pointId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointWithDetails(rs);
                }
            }
        }
        return null;
    }

    public boolean isPointCodeExists(String pointCode, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM charging_points WHERE point_code = ? AND point_id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pointCode.trim());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insertPoint(ChargingPoint cp) throws SQLException {
        String sql = "INSERT INTO charging_points (point_code, station_id, point_number, " +
                     "connector_type, power_rating_kw, status, is_fast_charger, hardware_model, last_service_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cp.getPointCode().trim());
            ps.setInt(2, cp.getStationId());
            ps.setInt(3, cp.getPointNumber());
            ps.setString(4, cp.getConnectorType());
            ps.setDouble(5, cp.getPowerRatingKw());
            ps.setString(6, cp.getStatus() != null ? cp.getStatus() : "AVAILABLE");
            ps.setBoolean(7, cp.isFastCharger());
            ps.setString(8, cp.getHardwareModel());
            ps.setDate(9, cp.getLastServiceDate());

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cp.setPointId(generatedKeys.getInt(1));
                    return cp.getPointId();
                }
            }
        }
        return 0;
    }

    public boolean updatePoint(ChargingPoint cp) throws SQLException {
        String sql = "UPDATE charging_points SET point_code = ?, station_id = ?, point_number = ?, " +
                     "connector_type = ?, power_rating_kw = ?, status = ?, is_fast_charger = ?, " +
                     "hardware_model = ?, last_service_date = ? WHERE point_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cp.getPointCode().trim());
            ps.setInt(2, cp.getStationId());
            ps.setInt(3, cp.getPointNumber());
            ps.setString(4, cp.getConnectorType());
            ps.setDouble(5, cp.getPowerRatingKw());
            ps.setString(6, cp.getStatus());
            ps.setBoolean(7, cp.isFastCharger());
            ps.setString(8, cp.getHardwareModel());
            ps.setDate(9, cp.getLastServiceDate());
            ps.setInt(10, cp.getPointId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePointStatus(int pointId, String newStatus) throws SQLException {
        String sql = "UPDATE charging_points SET status = ? WHERE point_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, pointId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePoint(int pointId) throws SQLException {
        String sql = "DELETE FROM charging_points WHERE point_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pointId);
            return ps.executeUpdate() > 0;
        }
    }

    private ChargingPoint mapResultSetToPoint(ResultSet rs) throws SQLException {
        ChargingPoint cp = new ChargingPoint();
        cp.setPointId(rs.getInt("point_id"));
        cp.setPointCode(rs.getString("point_code"));
        cp.setStationId(rs.getInt("station_id"));
        cp.setPointNumber(rs.getInt("point_number"));
        cp.setConnectorType(rs.getString("connector_type"));
        cp.setPowerRatingKw(rs.getDouble("power_rating_kw"));
        cp.setStatus(rs.getString("status"));
        cp.setFastCharger(rs.getBoolean("is_fast_charger"));
        cp.setHardwareModel(rs.getString("hardware_model"));
        cp.setLastServiceDate(rs.getDate("last_service_date"));
        cp.setCreatedAt(rs.getTimestamp("created_at"));
        
        try {
            cp.setStationName(rs.getString("station_name"));
            cp.setStationCode(rs.getString("station_code"));
            cp.setCampusZone(rs.getString("campus_zone"));
        } catch (SQLException ignored) {}
        return cp;
    }

    private ChargingPoint mapResultSetToPointWithDetails(ResultSet rs) throws SQLException {
        ChargingPoint cp = mapResultSetToPoint(rs);
        int activeSess = rs.getInt("active_session_id");
        if (!rs.wasNull() && activeSess > 0) {
            cp.setActiveSessionId(activeSess);
            cp.setActiveSessionCode(rs.getString("active_session_code"));
            cp.setActiveSessionStartTime(rs.getTimestamp("active_session_start_time"));
            cp.setActiveEnergyKwh(rs.getDouble("active_energy_kwh"));
            cp.setActiveCost(rs.getDouble("active_cost"));
            cp.setActiveVehicleNumber(rs.getString("active_vehicle_number"));
            cp.setActiveUserName(rs.getString("active_user_name"));
        }
        return cp;
    }
}
