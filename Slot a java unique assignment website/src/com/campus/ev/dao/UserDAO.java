package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM campus_users ORDER BY user_id DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public List<User> searchUsers(String query, String roleFilter) throws SQLException {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM campus_users WHERE 1=1 ");
        
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (full_name LIKE ? OR user_code LIKE ? OR email LIKE ? OR department LIKE ?) ");
        }
        if (roleFilter != null && !roleFilter.equalsIgnoreCase("ALL") && !roleFilter.isEmpty()) {
            sql.append("AND campus_role = ? ");
        }
        sql.append("ORDER BY user_id DESC");

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
            if (roleFilter != null && !roleFilter.equalsIgnoreCase("ALL") && !roleFilter.isEmpty()) {
                ps.setString(idx++, roleFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM campus_users WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public boolean isUserCodeExists(String userCode, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM campus_users WHERE user_code = ? AND user_id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userCode.trim());
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isEmailExists(String email, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM campus_users WHERE email = ? AND user_id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int insertUser(User user) throws SQLException {
        String sql = "INSERT INTO campus_users (user_code, full_name, email, phone, department, campus_role, status, rfid_card_uid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUserCode().trim());
            ps.setString(2, user.getFullName().trim());
            ps.setString(3, user.getEmail().trim());
            ps.setString(4, user.getPhone().trim());
            ps.setString(5, user.getDepartment().trim());
            ps.setString(6, user.getCampusRole());
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.setString(8, user.getRfidCardUid() != null && !user.getRfidCardUid().trim().isEmpty() ? user.getRfidCardUid().trim() : null);

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                    return user.getUserId();
                }
            }
        }
        return 0;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE campus_users SET user_code = ?, full_name = ?, email = ?, phone = ?, " +
                     "department = ?, campus_role = ?, status = ?, rfid_card_uid = ? WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserCode().trim());
            ps.setString(2, user.getFullName().trim());
            ps.setString(3, user.getEmail().trim());
            ps.setString(4, user.getPhone().trim());
            ps.setString(5, user.getDepartment().trim());
            ps.setString(6, user.getCampusRole());
            ps.setString(7, user.getStatus());
            ps.setString(8, user.getRfidCardUid() != null && !user.getRfidCardUid().trim().isEmpty() ? user.getRfidCardUid().trim() : null);
            ps.setInt(9, user.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM campus_users WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUserCode(rs.getString("user_code"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setDepartment(rs.getString("department"));
        u.setCampusRole(rs.getString("campus_role"));
        u.setStatus(rs.getString("status"));
        u.setRfidCardUid(rs.getString("rfid_card_uid"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
