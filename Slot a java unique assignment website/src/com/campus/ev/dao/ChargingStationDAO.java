package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.ChargingStation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingStationDAO {

    public List<ChargingStation> getAllStations() throws SQLException {
        List<ChargingStation> list = new ArrayList<>();
        String sql = "SELECT * FROM charging_stations ORDER BY station_id ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStation(rs));
            }
        }
        return list;
    }

    public ChargingStation getStationById(int stationId) throws SQLException {
        String sql = "SELECT * FROM charging_stations WHERE station_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStation(rs);
                }
            }
        }
        return null;
    }

    public boolean isStationCodeExists(String code, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM charging_stations WHERE station_code = ? AND station_id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insertStation(ChargingStation stn) throws SQLException {
        String sql = "INSERT INTO charging_stations (station_code, station_name, campus_zone, " +
                     "location_description, total_points, max_grid_capacity_kw, operating_status, " +
                     "solar_powered, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, stn.getStationCode().trim());
            ps.setString(2, stn.getStationName().trim());
            ps.setString(3, stn.getCampusZone().trim());
            ps.setString(4, stn.getLocationDescription());
            ps.setInt(5, stn.getTotalPoints());
            ps.setDouble(6, stn.getMaxGridCapacityKw());
            ps.setString(7, stn.getOperatingStatus());
            ps.setBoolean(8, stn.isSolarPowered());
            ps.setDouble(9, stn.getLatitude());
            ps.setDouble(10, stn.getLongitude());

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stn.setStationId(generatedKeys.getInt(1));
                    return stn.getStationId();
                }
            }
        }
        return 0;
    }

    public boolean updateStation(ChargingStation stn) throws SQLException {
        String sql = "UPDATE charging_stations SET station_code = ?, station_name = ?, campus_zone = ?, " +
                     "location_description = ?, total_points = ?, max_grid_capacity_kw = ?, " +
                     "operating_status = ?, solar_powered = ?, latitude = ?, longitude = ? WHERE station_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stn.getStationCode().trim());
            ps.setString(2, stn.getStationName().trim());
            ps.setString(3, stn.getCampusZone().trim());
            ps.setString(4, stn.getLocationDescription());
            ps.setInt(5, stn.getTotalPoints());
            ps.setDouble(6, stn.getMaxGridCapacityKw());
            ps.setString(7, stn.getOperatingStatus());
            ps.setBoolean(8, stn.isSolarPowered());
            ps.setDouble(9, stn.getLatitude());
            ps.setDouble(10, stn.getLongitude());
            ps.setInt(11, stn.getStationId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStation(int stationId) throws SQLException {
        String sql = "DELETE FROM charging_stations WHERE station_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            return ps.executeUpdate() > 0;
        }
    }

    private ChargingStation mapResultSetToStation(ResultSet rs) throws SQLException {
        ChargingStation s = new ChargingStation();
        s.setStationId(rs.getInt("station_id"));
        s.setStationCode(rs.getString("station_code"));
        s.setStationName(rs.getString("station_name"));
        s.setCampusZone(rs.getString("campus_zone"));
        s.setLocationDescription(rs.getString("location_description"));
        s.setTotalPoints(rs.getInt("total_points"));
        s.setMaxGridCapacityKw(rs.getDouble("max_grid_capacity_kw"));
        s.setOperatingStatus(rs.getString("operating_status"));
        s.setSolarPowered(rs.getBoolean("solar_powered"));
        s.setLatitude(rs.getDouble("latitude"));
        s.setLongitude(rs.getDouble("longitude"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
