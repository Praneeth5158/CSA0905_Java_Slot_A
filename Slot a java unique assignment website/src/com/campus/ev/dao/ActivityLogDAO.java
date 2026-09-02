package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    public List<ActivityLog> getRecentLogs(int limit) throws SQLException {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY logged_at DESC LIMIT ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityLog log = new ActivityLog();
                    log.setLogId(rs.getInt("log_id"));
                    log.setEventType(rs.getString("event_type"));
                    log.setDescription(rs.getString("description"));
                    log.setPointCode(rs.getString("point_code"));
                    log.setUserCode(rs.getString("user_code"));
                    log.setVehicleNumber(rs.getString("vehicle_number"));
                    log.setSeverity(rs.getString("severity"));
                    log.setLoggedAt(rs.getTimestamp("logged_at"));
                    list.add(log);
                }
            }
        }
        return list;
    }

    public int insertLog(String eventType, String desc, String pointCode, String userCode, String vehicleNum, String severity) {
        String sql = "INSERT INTO activity_logs (event_type, description, point_code, user_code, vehicle_number, severity) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType);
            ps.setString(2, desc);
            ps.setString(3, pointCode);
            ps.setString(4, userCode);
            ps.setString(5, vehicleNum);
            ps.setString(6, severity != null ? severity : "INFO");
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert activity log: " + e.getMessage());
            return 0;
        }
    }
}
