package com.campus.ev.dao;

import com.campus.ev.db.ConnectionManager;
import com.campus.ev.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = 
            "SELECT p.*, u.full_name AS user_name, u.user_code, cs.session_code " +
            "FROM billing_payments p " +
            "JOIN campus_users u ON p.user_id = u.user_id " +
            "JOIN charging_sessions cs ON p.session_id = cs.session_id " +
            "ORDER BY p.payment_time DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Payment pay = new Payment();
                pay.setPaymentId(rs.getInt("payment_id"));
                pay.setInvoiceNumber(rs.getString("invoice_number"));
                pay.setSessionId(rs.getInt("session_id"));
                pay.setUserId(rs.getInt("user_id"));
                pay.setAmount(rs.getDouble("amount"));
                pay.setPaymentMethod(rs.getString("payment_method"));
                pay.setTransactionRef(rs.getString("transaction_ref"));
                pay.setPaymentStatus(rs.getString("payment_status"));
                pay.setPaymentTime(rs.getTimestamp("payment_time"));
                pay.setUserName(rs.getString("user_name"));
                pay.setUserCode(rs.getString("user_code"));
                pay.setSessionCode(rs.getString("session_code"));
                list.add(pay);
            }
        }
        return list;
    }
}
