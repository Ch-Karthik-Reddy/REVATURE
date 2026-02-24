package com.revpay.dao;

import com.revpay.config.DatabaseConnection;
import com.revpay.model.Transaction;
import com.revpay.model.TransactionStatus;
import com.revpay.model.TransactionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(TransactionDAO.class);

    public boolean transferMoney(int senderId, int receiverId, BigDecimal amount) {
        Connection conn = null;
        PreparedStatement withdrawStmt = null;
        PreparedStatement depositStmt = null;
        PreparedStatement logStmt = null;

        String withdrawSQL = "UPDATE wallets SET balance = balance - ? WHERE user_id = ? AND balance >= ?";
        String depositSQL = "UPDATE wallets SET balance = balance + ? WHERE user_id = ?";
        String logSQL = "INSERT INTO transactions (sender_id, receiver_id, amount, transaction_type, status) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 🛑 Start Transaction

            // 1. Withdraw from Sender
            withdrawStmt = conn.prepareStatement(withdrawSQL);
            withdrawStmt.setBigDecimal(1, amount);
            withdrawStmt.setInt(2, senderId);
            withdrawStmt.setBigDecimal(3, amount); // Ensure balance >= amount
            int rowsAffected1 = withdrawStmt.executeUpdate();

            if (rowsAffected1 == 0) {
                // 🛑 USER FRIENDLY FIX: Don't throw exception. Just log warning and exit.
                logger.warn(" ️ Transfer Failed: Insufficient funds for User ID " + senderId);
                conn.rollback(); // Undo any locks
                return false; // Return false nicely
            }

            // 2. Deposit to Receiver
            depositStmt = conn.prepareStatement(depositSQL);
            depositStmt.setBigDecimal(1, amount);
            depositStmt.setInt(2, receiverId);
            int rowsAffected2 = depositStmt.executeUpdate();

            if (rowsAffected2 == 0) {
                logger.warn(" ️ Transfer Failed: Invalid receiver ID " + receiverId);
                conn.rollback();
                return false;
            }

            // 3. Log the Transaction
            logStmt = conn.prepareStatement(logSQL);
            logStmt.setInt(1, senderId);
            logStmt.setInt(2, receiverId);
            logStmt.setBigDecimal(3, amount);
            logStmt.setString(4, TransactionType.TRANSFER.name());
            logStmt.setString(5, TransactionStatus.SUCCESS.name());
            logStmt.executeUpdate();

            conn.commit();
            logger.info("  Transfer Successful: $" + amount + " from ID " + senderId + " to ID " + receiverId);
            return true;

        } catch (SQLException e) {
            // ❌ Only REAL errors (like DB crash) land here now
            if (conn != null) {
                try {
                    logger.error("  System Error. Rolling back...", e);
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Critical: Rollback failed!", ex);
                }
            }
            return false;
        } finally {
            closeResources(withdrawStmt, depositStmt, logStmt, conn);
        }
    }

    public List<Transaction> getTransactionHistory(int userId) {
        List<Transaction> history = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE sender_id = ? OR receiver_id = ? ORDER BY txn_timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setSenderId(rs.getInt("sender_id"));
                t.setReceiverId(rs.getInt("receiver_id"));
                t.setAmount(rs.getBigDecimal("amount"));
                t.setType(TransactionType.valueOf(rs.getString("transaction_type")));
                t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
                t.setTimestamp(rs.getTimestamp("txn_timestamp"));

                history.add(t);
            }
        } catch (SQLException e) {
            logger.error("  Error fetching transaction history for User ID " + userId, e);
        }
        return history;
    }

    public boolean depositMoney(int userId, BigDecimal amount) {
        Connection conn = null;
        PreparedStatement depositStmt = null;
        PreparedStatement logStmt = null;

        String depositSQL = "UPDATE wallets SET balance = balance + ? WHERE user_id = ?";
        String logSQL = "INSERT INTO transactions (sender_id, receiver_id, amount, transaction_type, status) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Add Money to Wallet
            depositStmt = conn.prepareStatement(depositSQL);
            depositStmt.setBigDecimal(1, amount);
            depositStmt.setInt(2, userId);
            int rows = depositStmt.executeUpdate();

            if (rows == 0) {
                logger.warn("Deposit Failed: Wallet not found for User ID " + userId);
                throw new SQLException("Wallet not found.");
            }

            // 2. Log it (Sender is self, Receiver is self for Deposit)
            logStmt = conn.prepareStatement(logSQL);
            logStmt.setInt(1, userId);
            logStmt.setInt(2, userId);
            logStmt.setBigDecimal(3, amount);
            logStmt.setString(4, TransactionType.DEPOSIT.name());
            logStmt.setString(5, TransactionStatus.SUCCESS.name());
            logStmt.executeUpdate();

            conn.commit(); // Save changes
            logger.info("  Deposit Successful: $" + amount + " for User ID " + userId);
            return true;

        } catch (SQLException e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            logger.error("  Deposit Error for User ID " + userId, e);
            return false;
        } finally {
            closeResources(depositStmt, logStmt, null, conn);
        }
    }

    // Helper to close resources safely
    private void closeResources(PreparedStatement s1, PreparedStatement s2, PreparedStatement s3, Connection conn) {
        try {
            if (s1 != null)
                s1.close();
            if (s2 != null)
                s2.close();
            if (s3 != null)
                s3.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("Error closing resources", e);
        }
    }
}