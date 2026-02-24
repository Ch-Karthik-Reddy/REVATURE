package com.revpay.dao;

import com.revpay.config.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for managing User Wallets.
 * <p>
 * This class handles the low-level database operations for creating wallets,
 * retrieving current balances, and updating funds.
 * <br>
 * <b>Note:</b> Direct usage of {@code updateBalance} should be done carefully to avoid
 * race conditions. Prefer using {@link TransactionDAO} for money movements.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class WalletDAO {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(WalletDAO.class);

    /**
     * Creates a new wallet for a newly registered user.
     * <p>
     * Initializes the balance to $0.00.
     * </p>
     *
     * @param userId The unique ID of the user.
     */
    public void createWallet(int userId) {
        String sql = "INSERT INTO wallets (user_id, balance) VALUES (?, 0.00)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            logger.info("✅ Wallet created for User ID: " + userId);

        } catch (SQLException e) {
            logger.error("❌ Failed to create wallet for User ID: " + userId, e);
        }
    }

    /**
     * Retrieves the current balance for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return The current balance as a {@link BigDecimal}. Returns {@link BigDecimal#ZERO} if not found or on error.
     */
    public BigDecimal getBalance(int userId) {
        String sql = "SELECT balance FROM wallets WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            logger.error("❌ Error fetching balance for User ID: " + userId, e);
        }
        return BigDecimal.ZERO; // Default safe fallback
    }

    /**
     * Updates the wallet balance directly.
     * <p>
     * <b>Warning:</b> This sets the absolute balance. Ensure calculation correctness before calling.
     * </p>
     *
     * @param userId     The unique ID of the user.
     * @param newBalance The new total balance to set.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public boolean updateBalance(int userId, BigDecimal newBalance) {
        String sql = "UPDATE wallets SET balance = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                logger.debug("💰 Balance updated for User ID " + userId + ": $" + newBalance);
                return true;
            }
        } catch (SQLException e) {
            logger.error("❌ Failed to update balance for User ID: " + userId, e);
        }
        return false;
    }
}