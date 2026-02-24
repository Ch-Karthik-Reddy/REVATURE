package com.revpay.dao;

import com.revpay.config.DatabaseConnection;
import com.revpay.model.User;
import com.revpay.model.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(UserDAO.class);


    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (email, phone_number, password_hash, transaction_pin, full_name, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getTransactionPin());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getRole().name());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                logger.info("  New User Registered: " + user.getEmail());
                return true;
            }

        } catch (SQLException e) {
            // 🔍 CHECK: Is this a "Duplicate Entry" error? (SQLState 23000 is for integrity violations)
            if ("23000".equals(e.getSQLState())) {
                // Log a clean warning WITHOUT the stack trace
                logger.warn(" ️ Registration attempt failed: Email '" + user.getEmail() + "' already exists.");
            } else {
                // If it's some other weird error (like DB connection lost), print the full log
                logger.error("  Unexpected DB Error for " + user.getEmail(), e);
            }
        }
        return false;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setTransactionPin(rs.getString("transaction_pin"));
                user.setFullName(rs.getString("full_name"));
                // Safely parse Role
                try {
                    user.setRole(Role.valueOf(rs.getString("role")));
                } catch (Exception e) {
                    logger.warn("Invalid Role found for user " + email);
                    user.setRole(Role.PERSONAL); // Default fallback
                }
                return user;
            }
        } catch (SQLException e) {
            logger.error("  Error fetching user by email: " + email, e);
        }
        return null;
    }

    public boolean deleteUser(int userId) {
        logger.warn("⚠️ Attempting to delete User ID: " + userId);

        String[] sqlSteps = {
                "DELETE FROM payment_methods WHERE user_id = ?",
                "DELETE FROM payment_requests WHERE requester_id = ? OR payer_id = ?",
                "DELETE FROM invoices WHERE business_id = ?",
                "DELETE FROM transactions WHERE sender_id = ? OR receiver_id = ?",
                "DELETE FROM loans WHERE user_id = ?",
                "DELETE FROM business_profiles WHERE user_id = ?",
                "DELETE FROM wallets WHERE user_id = ?",
                "DELETE FROM users WHERE user_id = ?"
        };

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            for (String sql : sqlSteps) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    if (sql.contains("OR payer_id") || sql.contains("OR receiver_id")) {
                        stmt.setInt(2, userId);
                    }
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            logger.info("✅ User ID " + userId + " Deleted Successfully.");
            return true;

        } catch (SQLException e) {
            logger.error("❌ Error deleting user " + userId + ". Rolling back...", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { logger.error("Rollback failed", ex); }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { logger.error("Error closing connection", e); }
            }
        }
    }
}