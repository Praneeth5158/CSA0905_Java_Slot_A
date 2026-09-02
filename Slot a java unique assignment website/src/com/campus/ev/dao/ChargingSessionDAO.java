package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.ChargingSession;
import com.campus.ev.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingSessionDAO {

    public List<ChargingSession> getAllSessions() throws SQLException {
        List<ChargingSession> list = new ArrayList<>();
        String sql = 
            "SELECT cs.*, cp.point_code, s.station_name, s.campus_zone, " +
            "v.vehicle_number, CONCAT(v.brand, ' ', v.model) AS vehicle_model, " +
            "u.full_name AS user_name, u.user_code, t.tariff_name, t.rate_per_kwh AS tariff_rate " +
            "FROM charging_sessions cs " +
            "JOIN charging_points cp ON cs.point_id = cp.point_id " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "JOIN campus_users u ON cs.user_id = u.user_id " +
            "JOIN tariffs t ON cs.tariff_id = t.tariff_id " +
            "ORDER BY cs.start_time DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSession(rs));
            }
        }
        return list;
    }

    public List<ChargingSession> getActiveSessions() throws SQLException {
        List<ChargingSession> list = new ArrayList<>();
        String sql = 
            "SELECT cs.*, cp.point_code, s.station_name, s.campus_zone, " +
            "v.vehicle_number, CONCAT(v.brand, ' ', v.model) AS vehicle_model, " +
            "u.full_name AS user_name, u.user_code, t.tariff_name, t.rate_per_kwh AS tariff_rate " +
            "FROM charging_sessions cs " +
            "JOIN charging_points cp ON cs.point_id = cp.point_id " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "JOIN campus_users u ON cs.user_id = u.user_id " +
            "JOIN tariffs t ON cs.tariff_id = t.tariff_id " +
            "WHERE cs.status = 'CHARGING' " +
            "ORDER BY cs.start_time DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSession(rs));
            }
        }
        return list;
    }

    public ChargingSession getSessionById(int sessionId) throws SQLException {
        String sql = 
            "SELECT cs.*, cp.point_code, s.station_name, s.campus_zone, " +
            "v.vehicle_number, CONCAT(v.brand, ' ', v.model) AS vehicle_model, " +
            "u.full_name AS user_name, u.user_code, t.tariff_name, t.rate_per_kwh AS tariff_rate " +
            "FROM charging_sessions cs " +
            "JOIN charging_points cp ON cs.point_id = cp.point_id " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "JOIN campus_users u ON cs.user_id = u.user_id " +
            "JOIN tariffs t ON cs.tariff_id = t.tariff_id " +
            "WHERE cs.session_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSession(rs);
                }
            }
        }
        return null;
    }

    public ChargingSession getActiveSessionByPoint(int pointId) throws SQLException {
        String sql = 
            "SELECT cs.*, cp.point_code, s.station_name, s.campus_zone, " +
            "v.vehicle_number, CONCAT(v.brand, ' ', v.model) AS vehicle_model, " +
            "u.full_name AS user_name, u.user_code, t.tariff_name, t.rate_per_kwh AS tariff_rate " +
            "FROM charging_sessions cs " +
            "JOIN charging_points cp ON cs.point_id = cp.point_id " +
            "JOIN charging_stations s ON cp.station_id = s.station_id " +
            "JOIN ev_vehicles v ON cs.vehicle_id = v.vehicle_id " +
            "JOIN campus_users u ON cs.user_id = u.user_id " +
            "JOIN tariffs t ON cs.tariff_id = t.tariff_id " +
            "WHERE cs.point_id = ? AND cs.status = 'CHARGING' LIMIT 1";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pointId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSession(rs);
                }
            }
        }
        return null;
    }

    /**
     * ACID Transactional Start Session:
     * 1. Inserts new charging_sessions record
     * 2. Updates charging_points status to 'OCCUPIED'
     * 3. Logs activity in activity_logs
     * Commits atomically or rolls back on failure.
     */
    public int startSessionTransaction(ChargingSession session, String pointCode, String userCode, String vehicleNumber) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Session
            String insertSql = "INSERT INTO charging_sessions (session_code, reservation_id, point_id, vehicle_id, " +
                               "user_id, tariff_id, start_time, initial_soc_percent, final_soc_percent, " +
                               "total_energy_kwh, peak_power_kw, energy_cost, parking_fee, total_amount, status) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedSessionId = 0;
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, session.getSessionCode());
                if (session.getReservationId() != null && session.getReservationId() > 0) {
                    ps.setInt(2, session.getReservationId());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setInt(3, session.getPointId());
                ps.setInt(4, session.getVehicleId());
                ps.setInt(5, session.getUserId());
                ps.setInt(6, session.getTariffId());
                ps.setTimestamp(7, session.getStartTime());
                ps.setInt(8, session.getInitialSocPercent());
                ps.setInt(9, session.getFinalSocPercent());
                ps.setDouble(10, session.getTotalEnergyKwh());
                ps.setDouble(11, session.getPeakPowerKw());
                ps.setDouble(12, session.getEnergyCost());
                ps.setDouble(13, session.getParkingFee());
                ps.setDouble(14, session.getTotalAmount());
                ps.setString(15, "CHARGING");

                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        generatedSessionId = gk.getInt(1);
                        session.setSessionId(generatedSessionId);
                    }
                }
            }

            // 2. Update Charging Point status to OCCUPIED
            String updateCpSql = "UPDATE charging_points SET status = 'OCCUPIED' WHERE point_id = ?";
            try (PreparedStatement psCp = conn.prepareStatement(updateCpSql)) {
                psCp.setInt(1, session.getPointId());
                psCp.executeUpdate();
            }

            // 3. Update reservation if present
            if (session.getReservationId() != null && session.getReservationId() > 0) {
                String updateResSql = "UPDATE reservations SET status = 'CHECKED_IN' WHERE reservation_id = ?";
                try (PreparedStatement psRes = conn.prepareStatement(updateResSql)) {
                    psRes.setInt(1, session.getReservationId());
                    psRes.executeUpdate();
                }
            }

            // 4. Log Activity
            String logSql = "INSERT INTO activity_logs (event_type, description, point_code, user_code, vehicle_number, severity) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                psLog.setString(1, "SESSION_STARTED");
                psLog.setString(2, "Charging session #" + session.getSessionCode() + " initiated on " + pointCode);
                psLog.setString(3, pointCode);
                psLog.setString(4, userCode);
                psLog.setString(5, vehicleNumber);
                psLog.setString(6, "SUCCESS");
                psLog.executeUpdate();
            }

            conn.commit();
            return generatedSessionId;
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * ACID Transactional Stop Session:
     * 1. Updates charging_sessions (end_time, duration, final kWh, cost, total, status = 'COMPLETED')
     * 2. Frees charging_point to 'AVAILABLE'
     * 3. Generates billing_payments record
     * 4. Updates reservation if any to 'COMPLETED'
     * 5. Logs activity
     */
    public boolean stopSessionTransaction(ChargingSession session, Payment payment, String pointCode, String userCode, String vehicleNumber) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Update Session
            String updateSessionSql = 
                "UPDATE charging_sessions SET end_time = ?, duration_minutes = ?, final_soc_percent = ?, " +
                "total_energy_kwh = ?, peak_power_kw = ?, energy_cost = ?, parking_fee = ?, " +
                "total_amount = ?, status = ? WHERE session_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSessionSql)) {
                ps.setTimestamp(1, session.getEndTime());
                ps.setInt(2, session.getDurationMinutes());
                ps.setInt(3, session.getFinalSocPercent());
                ps.setDouble(4, session.getTotalEnergyKwh());
                ps.setDouble(5, session.getPeakPowerKw());
                ps.setDouble(6, session.getEnergyCost());
                ps.setDouble(7, session.getParkingFee());
                ps.setDouble(8, session.getTotalAmount());
                ps.setString(9, "COMPLETED");
                ps.setInt(10, session.getSessionId());
                ps.executeUpdate();
            }

            // 2. Free Point to AVAILABLE
            String updateCpSql = "UPDATE charging_points SET status = 'AVAILABLE' WHERE point_id = ?";
            try (PreparedStatement psCp = conn.prepareStatement(updateCpSql)) {
                psCp.setInt(1, session.getPointId());
                psCp.executeUpdate();
            }

            // 3. Create Payment Record
            String insertPaymentSql = 
                "INSERT INTO billing_payments (invoice_number, session_id, user_id, amount, payment_method, transaction_ref, payment_status, payment_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psPay = conn.prepareStatement(insertPaymentSql, Statement.RETURN_GENERATED_KEYS)) {
                psPay.setString(1, payment.getInvoiceNumber());
                psPay.setInt(2, session.getSessionId());
                psPay.setInt(3, session.getUserId());
                psPay.setDouble(4, payment.getAmount());
                psPay.setString(5, payment.getPaymentMethod());
                psPay.setString(6, payment.getTransactionRef());
                psPay.setString(7, payment.getPaymentStatus());
                psPay.setTimestamp(8, payment.getPaymentTime());
                psPay.executeUpdate();
                try (ResultSet gk = psPay.getGeneratedKeys()) {
                    if (gk.next()) {
                        payment.setPaymentId(gk.getInt(1));
                    }
                }
            }

            // 4. Update Reservation if any
            if (session.getReservationId() != null && session.getReservationId() > 0) {
                String updateResSql = "UPDATE reservations SET status = 'COMPLETED' WHERE reservation_id = ?";
                try (PreparedStatement psRes = conn.prepareStatement(updateResSql)) {
                    psRes.setInt(1, session.getReservationId());
                    psRes.executeUpdate();
                }
            }

            // 5. Activity Log
            String logSql = "INSERT INTO activity_logs (event_type, description, point_code, user_code, vehicle_number, severity) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                psLog.setString(1, "SESSION_COMPLETED");
                psLog.setString(2, "Session #" + session.getSessionCode() + " stopped. Energy: " + 
                               String.format("%.2f", session.getTotalEnergyKwh()) + " kWh, Total: ₹" + 
                               String.format("%.2f", session.getTotalAmount()));
                psLog.setString(3, pointCode);
                psLog.setString(4, userCode);
                psLog.setString(5, vehicleNumber);
                psLog.setString(6, "SUCCESS");
                psLog.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean updateLiveSessionProgress(int sessionId, double cumulativeKwh, double energyCost, double totalAmount) throws SQLException {
        String sql = "UPDATE charging_sessions SET total_energy_kwh = ?, energy_cost = ?, total_amount = ? WHERE session_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, cumulativeKwh);
            ps.setDouble(2, energyCost);
            ps.setDouble(3, totalAmount);
            ps.setInt(4, sessionId);
            return ps.executeUpdate() > 0;
        }
    }

    private ChargingSession mapResultSetToSession(ResultSet rs) throws SQLException {
        ChargingSession cs = new ChargingSession();
        cs.setSessionId(rs.getInt("session_id"));
        cs.setSessionCode(rs.getString("session_code"));
        int resId = rs.getInt("reservation_id");
        if (!rs.wasNull()) cs.setReservationId(resId);
        cs.setPointId(rs.getInt("point_id"));
        cs.setVehicleId(rs.getInt("vehicle_id"));
        cs.setUserId(rs.getInt("user_id"));
        cs.setTariffId(rs.getInt("tariff_id"));
        cs.setStartTime(rs.getTimestamp("start_time"));
        cs.setEndTime(rs.getTimestamp("end_time"));
        cs.setDurationMinutes(rs.getInt("duration_minutes"));
        cs.setInitialSocPercent(rs.getInt("initial_soc_percent"));
        cs.setFinalSocPercent(rs.getInt("final_soc_percent"));
        cs.setTotalEnergyKwh(rs.getDouble("total_energy_kwh"));
        cs.setPeakPowerKw(rs.getDouble("peak_power_kw"));
        cs.setEnergyCost(rs.getDouble("energy_cost"));
        cs.setParkingFee(rs.getDouble("parking_fee"));
        cs.setTotalAmount(rs.getDouble("total_amount"));
        cs.setStatus(rs.getString("status"));
        cs.setCreatedAt(rs.getTimestamp("created_at"));

        try {
            cs.setPointCode(rs.getString("point_code"));
            cs.setStationName(rs.getString("station_name"));
            cs.setCampusZone(rs.getString("campus_zone"));
            cs.setVehicleNumber(rs.getString("vehicle_number"));
            cs.setVehicleModel(rs.getString("vehicle_model"));
            cs.setUserName(rs.getString("user_name"));
            cs.setUserCode(rs.getString("user_code"));
            cs.setTariffName(rs.getString("tariff_name"));
            cs.setTariffRate(rs.getDouble("tariff_rate"));
        } catch (SQLException ignored) {}

        return cs;
    }
}
