package com.revpay.model;

import java.math.BigDecimal;

/**
 * Model class representing a User's Digital Wallet.
 * <p>
 * This class stores the current balance available to a user.
 * Every registered user has exactly one Wallet.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class Wallet {
    private int walletId;
    private int userId;
    private BigDecimal balance;

    /**
     * Default constructor.
     */
    public Wallet() {}

    /**
     * Parameterized constructor for creating a new Wallet.
     *
     * @param userId  The ID of the user owning this wallet.
     * @param balance The initial balance (usually 0.00).
     */
    public Wallet(int userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique Wallet ID.
     * @return The wallet ID.
     */
    public int getWalletId() { return walletId; }

    /**
     * Sets the unique Wallet ID.
     * @param walletId The wallet ID.
     */
    public void setWalletId(int walletId) { this.walletId = walletId; }

    /**
     * Gets the User ID associated with this wallet.
     * @return The user ID.
     */
    public int getUserId() { return userId; }

    /**
     * Sets the User ID associated with this wallet.
     * @param userId The user ID.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the current balance.
     * @return The balance as BigDecimal.
     */
    public BigDecimal getBalance() { return balance; }

    /**
     * Sets the current balance.
     * @param balance The balance as BigDecimal.
     */
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    /**
     * Returns a string representation of the Wallet.
     * @return A formatted string with User ID and Balance.
     */
    @Override
    public String toString() {
        return "Wallet [User=" + userId + ", Balance=$" + balance + "]";
    }
}