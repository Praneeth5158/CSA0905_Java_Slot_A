package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.Tariff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TariffDAO {

    public List<Tariff> getAllTariffs() throws SQLException {
        List<Tariff> list = new ArrayList<>();
        String sql = "SELECT * FROM tariffs ORDER BY tariff_id ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTariff(rs));
            }
        }
        return list;
    }

    public Tariff getActiveTariff() throws SQLException {
        String sql = "SELECT * FROM tariffs WHERE status = 'ACTIVE' ORDER BY tariff_id ASC LIMIT 1";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToTariff(rs);
            }
        }
        return null;
    }

    public Tariff getTariffById(int tariffId) throws SQLException {
        String sql = "SELECT * FROM tariffs WHERE tariff_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tariffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTariff(rs);
                }
            }
        }
        return null;
    }

    public int insertTariff(Tariff t) throws SQLException {
        String sql = "INSERT INTO tariffs (tariff_code, tariff_name, rate_per_kwh, base_parking_fee_per_hour, " +
                     "peak_hour_multiplier, effective_from, status, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTariffCode().trim());
            ps.setString(2, t.getTariffName().trim());
            ps.setDouble(3, t.getRatePerKwh());
            ps.setDouble(4, t.getBaseParkingFeePerHour());
            ps.setDouble(5, t.getPeakHourMultiplier());
            ps.setDate(6, t.getEffectiveFrom());
            ps.setString(7, t.getStatus() != null ? t.getStatus() : "ACTIVE");
            ps.setString(8, t.getDescription());

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    t.setTariffId(gk.getInt(1));
                    return t.getTariffId();
                }
            }
        }
        return 0;
    }

    public boolean updateTariff(Tariff t) throws SQLException {
        String sql = "UPDATE tariffs SET tariff_code = ?, tariff_name = ?, rate_per_kwh = ?, " +
                     "base_parking_fee_per_hour = ?, peak_hour_multiplier = ?, effective_from = ?, " +
                     "status = ?, description = ? WHERE tariff_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTariffCode().trim());
            ps.setString(2, t.getTariffName().trim());
            ps.setDouble(3, t.getRatePerKwh());
            ps.setDouble(4, t.getBaseParkingFeePerHour());
            ps.setDouble(5, t.getPeakHourMultiplier());
            ps.setDate(6, t.getEffectiveFrom());
            ps.setString(7, t.getStatus());
            ps.setString(8, t.getDescription());
            ps.setInt(9, t.getTariffId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTariff(int tariffId) throws SQLException {
        String sql = "DELETE FROM tariffs WHERE tariff_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tariffId);
            return ps.executeUpdate() > 0;
        }
    }

    private Tariff mapResultSetToTariff(ResultSet rs) throws SQLException {
        Tariff t = new Tariff();
        t.setTariffId(rs.getInt("tariff_id"));
        t.setTariffCode(rs.getString("tariff_code"));
        t.setTariffName(rs.getString("tariff_name"));
        t.setRatePerKwh(rs.getDouble("rate_per_kwh"));
        t.setBaseParkingFeePerHour(rs.getDouble("base_parking_fee_per_hour"));
        t.setPeakHourMultiplier(rs.getDouble("peak_hour_multiplier"));
        t.setEffectiveFrom(rs.getDate("effective_from"));
        t.setStatus(rs.getString("status"));
        t.setDescription(rs.getString("description"));
        return t;
    }
}
