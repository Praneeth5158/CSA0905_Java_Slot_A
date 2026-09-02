package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = 
            "SELECT r.*, u.full_name AS user_name, u.user_code, v.vehicle_number, " +
            "CONCAT(v.brand, ' ', v.model) AS vehicle_model, cp.point_code, " +
            "s.station_name, s.campus_zone " +
            "FROM reservations r " +
            "JOIN campus_users u ON r.user_id = u.user_id " +
            "JOIN ev_vehicles v ON r.vehicle_id = v.vehicle_id " +
            "JOIN charging_points cp ON r.point_id = cp.point_id " +
            "JOIN charging_stations s ON r.station_id = s.station_id " +
            "ORDER BY r.start_time DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        }
        return list;
    }

    public List<Reservation> getReservationsByPointAndDate(int pointId, java.util.Date date) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = 
            "SELECT r.*, u.full_name AS user_name, u.user_code, v.vehicle_number, " +
            "CONCAT(v.brand, ' ', v.model) AS vehicle_model, cp.point_code, " +
            "s.station_name, s.campus_zone " +
            "FROM reservations r " +
            "JOIN campus_users u ON r.user_id = u.user_id " +
            "JOIN ev_vehicles v ON r.vehicle_id = v.vehicle_id " +
            "JOIN charging_points cp ON r.point_id = cp.point_id " +
            "JOIN charging_stations s ON r.station_id = s.station_id " +
            "WHERE r.point_id = ? AND DATE(r.start_time) = DATE(?) " +
            "AND r.status IN ('CONFIRMED', 'CHECKED_IN') " +
            "ORDER BY r.start_time ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pointId);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if there are any conflicting reservations for the same charging point in the requested time interval.
     */
    public boolean hasConflictingReservation(int pointId, Timestamp start, Timestamp end, int excludeReservationId) throws SQLException {
        String sql = 
            "SELECT COUNT(*) FROM reservations " +
            "WHERE point_id = ? AND reservation_id != ? " +
            "AND status IN ('CONFIRMED', 'CHECKED_IN') " +
            "AND ((start_time < ? AND end_time > ?) OR (start_time >= ? AND start_time < ?))";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pointId);
            ps.setInt(2, excludeReservationId);
            ps.setTimestamp(3, end);
            ps.setTimestamp(4, start);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, end);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the vehicle itself is already reserved in that time window.
     */
    public boolean isVehicleBusy(int vehicleId, Timestamp start, Timestamp end, int excludeReservationId) throws SQLException {
        String sql = 
            "SELECT COUNT(*) FROM reservations " +
            "WHERE vehicle_id = ? AND reservation_id != ? " +
            "AND status IN ('CONFIRMED', 'CHECKED_IN') " +
            "AND ((start_time < ? AND end_time > ?) OR (start_time >= ? AND start_time < ?))";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setInt(2, excludeReservationId);
            ps.setTimestamp(3, end);
            ps.setTimestamp(4, start);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, end);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insertReservation(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservations (reservation_code, user_id, vehicle_id, point_id, " +
                     "station_id, start_time, end_time, estimated_kwh, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getReservationCode());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getVehicleId());
            ps.setInt(4, r.getPointId());
            ps.setInt(5, r.getStationId());
            ps.setTimestamp(6, r.getStartTime());
            ps.setTimestamp(7, r.getEndTime());
            ps.setDouble(8, r.getEstimatedKwh());
            ps.setString(9, r.getStatus() != null ? r.getStatus() : "CONFIRMED");

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    r.setReservationId(generatedKeys.getInt(1));
                    return r.getReservationId();
                }
            }
        }
        return 0;
    }

    public boolean updateReservationStatus(int reservationId, String status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cancelReservation(int reservationId) throws SQLException {
        return updateReservationStatus(reservationId, "CANCELLED");
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationCode(rs.getString("reservation_code"));
        r.setUserId(rs.getInt("user_id"));
        r.setVehicleId(rs.getInt("vehicle_id"));
        r.setPointId(rs.getInt("point_id"));
        r.setStationId(rs.getInt("station_id"));
        r.setStartTime(rs.getTimestamp("start_time"));
        r.setEndTime(rs.getTimestamp("end_time"));
        r.setEstimatedKwh(rs.getDouble("estimated_kwh"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUserName(rs.getString("user_name"));
        r.setUserCode(rs.getString("user_code"));
        r.setVehicleNumber(rs.getString("vehicle_number"));
        r.setVehicleModel(rs.getString("vehicle_model"));
        r.setPointCode(rs.getString("point_code"));
        r.setStationName(rs.getString("station_name"));
        r.setCampusZone(rs.getString("campus_zone"));
        return r;
    }
}
