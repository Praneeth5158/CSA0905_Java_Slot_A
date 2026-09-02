package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.EnergyUsage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnergyUsageDAO {

    public int insertReading(EnergyUsage usage) throws SQLException {
        String sql = "INSERT INTO energy_usage (session_id, reading_timestamp, instant_voltage_v, " +
                     "instant_current_a, instant_power_kw, cumulative_kwh, battery_temp_celsius) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usage.getSessionId());
            ps.setTimestamp(2, usage.getReadingTimestamp());
            ps.setDouble(3, usage.getInstantVoltageV());
            ps.setDouble(4, usage.getInstantCurrentA());
            ps.setDouble(5, usage.getInstantPowerKw());
            ps.setDouble(6, usage.getCumulativeKwh());
            ps.setDouble(7, usage.getBatteryTempCelsius());

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    usage.setUsageId(gk.getInt(1));
                    return usage.getUsageId();
                }
            }
        }
        return 0;
    }

    public List<EnergyUsage> getTelemetryBySession(int sessionId) throws SQLException {
        List<EnergyUsage> list = new ArrayList<>();
        String sql = "SELECT * FROM energy_usage WHERE session_id = ? ORDER BY reading_timestamp ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnergyUsage eu = new EnergyUsage();
                    eu.setUsageId(rs.getInt("usage_id"));
                    eu.setSessionId(rs.getInt("session_id"));
                    eu.setReadingTimestamp(rs.getTimestamp("reading_timestamp"));
                    eu.setInstantVoltageV(rs.getDouble("instant_voltage_v"));
                    eu.setInstantCurrentA(rs.getDouble("instant_current_a"));
                    eu.setInstantPowerKw(rs.getDouble("instant_power_kw"));
                    eu.setCumulativeKwh(rs.getDouble("cumulative_kwh"));
                    eu.setBatteryTempCelsius(rs.getDouble("battery_temp_celsius"));
                    list.add(eu);
                }
            }
        }
        return list;
    }
}
