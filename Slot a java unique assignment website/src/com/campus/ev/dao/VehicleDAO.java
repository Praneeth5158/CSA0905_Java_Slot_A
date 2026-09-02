package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.*, u.full_name AS owner_name, u.user_code AS owner_code " +
                     "FROM ev_vehicles v " +
                     "JOIN campus_users u ON v.user_id = u.user_id " +
                     "ORDER BY v.vehicle_id DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> searchVehicles(String query, String typeFilter, String connectorFilter) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT v.*, u.full_name AS owner_name, u.user_code AS owner_code " +
            "FROM ev_vehicles v " +
            "JOIN campus_users u ON v.user_id = u.user_id WHERE 1=1 "
        );

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (v.vehicle_number LIKE ? OR v.brand LIKE ? OR v.model LIKE ? OR u.full_name LIKE ?) ");
        }
        if (typeFilter != null && !typeFilter.equalsIgnoreCase("ALL") && !typeFilter.isEmpty()) {
            sql.append("AND v.vehicle_type = ? ");
        }
        if (connectorFilter != null && !connectorFilter.equalsIgnoreCase("ALL") && !connectorFilter.isEmpty()) {
            sql.append("AND v.connector_type = ? ");
        }
        sql.append("ORDER BY v.vehicle_id DESC");

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (query != null && !query.trim().isEmpty()) {
                String pattern = "%" + query.trim() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (typeFilter != null && !typeFilter.equalsIgnoreCase("ALL") && !typeFilter.isEmpty()) {
                ps.setString(idx++, typeFilter);
            }
            if (connectorFilter != null && !connectorFilter.equalsIgnoreCase("ALL") && !connectorFilter.isEmpty()) {
                ps.setString(idx++, connectorFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        }
        return vehicles;
    }

    public Vehicle getVehicleById(int vehicleId) throws SQLException {
        String sql = "SELECT v.*, u.full_name AS owner_name, u.user_code AS owner_code " +
                     "FROM ev_vehicles v " +
                     "JOIN campus_users u ON v.user_id = u.user_id " +
                     "WHERE v.vehicle_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        return null;
    }

    public boolean isVehicleNumberExists(String vehicleNumber, int excludeVehicleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ev_vehicles WHERE vehicle_number = ? AND vehicle_id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicleNumber.trim());
            ps.setInt(2, excludeVehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO ev_vehicles (vehicle_number, user_id, vehicle_type, brand, model, " +
                     "battery_capacity_kwh, max_charge_rate_kw, connector_type, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicle.getVehicleNumber().trim());
            ps.setInt(2, vehicle.getUserId());
            ps.setString(3, vehicle.getVehicleType());
            ps.setString(4, vehicle.getBrand().trim());
            ps.setString(5, vehicle.getModel().trim());
            ps.setDouble(6, vehicle.getBatteryCapacityKwh());
            ps.setDouble(7, vehicle.getMaxChargeRateKw());
            ps.setString(8, vehicle.getConnectorType());
            ps.setString(9, vehicle.getStatus() != null ? vehicle.getStatus() : "ACTIVE");

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setVehicleId(generatedKeys.getInt(1));
                    return vehicle.getVehicleId();
                }
            }
        }
        return 0;
    }

    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE ev_vehicles SET vehicle_number = ?, user_id = ?, vehicle_type = ?, brand = ?, " +
                     "model = ?, battery_capacity_kwh = ?, max_charge_rate_kw = ?, connector_type = ?, " +
                     "status = ? WHERE vehicle_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleNumber().trim());
            ps.setInt(2, vehicle.getUserId());
            ps.setString(3, vehicle.getVehicleType());
            ps.setString(4, vehicle.getBrand().trim());
            ps.setString(5, vehicle.getModel().trim());
            ps.setDouble(6, vehicle.getBatteryCapacityKwh());
            ps.setDouble(7, vehicle.getMaxChargeRateKw());
            ps.setString(8, vehicle.getConnectorType());
            ps.setString(9, vehicle.getStatus());
            ps.setInt(10, vehicle.getVehicleId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM ev_vehicles WHERE vehicle_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setVehicleNumber(rs.getString("vehicle_number"));
        v.setUserId(rs.getInt("user_id"));
        v.setVehicleType(rs.getString("vehicle_type"));
        v.setBrand(rs.getString("brand"));
        v.setModel(rs.getString("model"));
        v.setBatteryCapacityKwh(rs.getDouble("battery_capacity_kwh"));
        v.setMaxChargeRateKw(rs.getDouble("max_charge_rate_kw"));
        v.setConnectorType(rs.getString("connector_type"));
        v.setStatus(rs.getString("status"));
        v.setRegisteredAt(rs.getTimestamp("registered_at"));
        v.setOwnerName(rs.getString("owner_name"));
        v.setOwnerCode(rs.getString("owner_code"));
        return v;
    }
}
