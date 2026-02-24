package com.revpay.dao;

import com.revpay.config.DatabaseConnection;
import com.revpay.model.PaymentRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RequestDAO {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(RequestDAO.class);


    public boolean createRequest(PaymentRequest req) {
        String sql = "INSERT INTO payment_requests (requester_id, payer_id, amount, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, req.getRequesterId());
            stmt.setInt(2, req.getPayerId());
            stmt.setBigDecimal(3, req.getAmount());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("  Payment Request Created: User " + req.getRequesterId() + " asked User " + req.getPayerId() + " for $" + req.getAmount());
                return true;
            }
        } catch (SQLException e) {
            logger.error("  Failed to create payment request from User " + req.getRequesterId() + " to " + req.getPayerId(), e);
        }
        return false;
    }


    public List<PaymentRequest> getIncomingRequests(int userId) {
        List<PaymentRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_requests WHERE payer_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PaymentRequest r = new PaymentRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setRequesterId(rs.getInt("requester_id"));
                r.setPayerId(rs.getInt("payer_id"));
                r.setAmount(rs.getBigDecimal("amount"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (SQLException e) {
            logger.error("  Error fetching incoming requests for User ID " + userId, e);
        }
        return list;
    }


    public boolean updateStatus(int requestId, String status) {
        String sql = "UPDATE payment_requests SET status = ? WHERE request_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                logger.info("  Request ID " + requestId + " updated to status: " + status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("  Failed to update status for Request ID " + requestId, e);
        }
        return false;
    }


    public PaymentRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM payment_requests WHERE request_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PaymentRequest r = new PaymentRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setRequesterId(rs.getInt("requester_id"));
                r.setPayerId(rs.getInt("payer_id"));
                r.setAmount(rs.getBigDecimal("amount"));
                r.setStatus(rs.getString("status")); // Ensure status is also retrieved
                return r;
            }
        } catch (SQLException e) {
            logger.error("  Error fetching Request ID " + requestId, e);
        }
        return null;
    }
}