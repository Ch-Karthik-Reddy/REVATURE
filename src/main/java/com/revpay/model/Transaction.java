package com.revpay.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing a financial transaction.
 * <p>
 * This class serves as an immutable record of a money movement event,
 * including details like the sender, receiver, amount, and the timestamp
 * of when it occurred.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class Transaction {
    private int transactionId;
    private int senderId;
    private int receiverId;
    private BigDecimal amount;
    private TransactionType type;     // Enum: TRANSFER, DEPOSIT, WITHDRAWAL
    private TransactionStatus status; // Enum: SUCCESS, FAILED, PENDING
    private Timestamp timestamp;

    /**
     * Default constructor.
     */
    public Transaction() {}

    /**
     * Parameterized constructor for creating a new Transaction record.
     *
     * @param senderId   The ID of the user sending money.
     * @param receiverId The ID of the user receiving money.
     * @param amount     The amount transferred.
     * @param type       The type of transaction (e.g., TRANSFER).
     * @param status     The final status of the transaction (e.g., SUCCESS).
     */
    public Transaction(int senderId, int receiverId, BigDecimal amount, TransactionType type, TransactionStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique Transaction ID.
     * @return The transaction ID.
     */
    public int getTransactionId() { return transactionId; }

    /**
     * Sets the unique Transaction ID.
     * @param transactionId The transaction ID.
     */
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    /**
     * Gets the Sender's User ID.
     * @return The sender ID.
     */
    public int getSenderId() { return senderId; }

    /**
     * Sets the Sender's User ID.
     * @param senderId The sender ID.
     */
    public void setSenderId(int senderId) { this.senderId = senderId; }

    /**
     * Gets the Receiver's User ID.
     * @return The receiver ID.
     */
    public int getReceiverId() { return receiverId; }

    /**
     * Sets the Receiver's User ID.
     * @param receiverId The receiver ID.
     */
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    /**
     * Gets the transaction amount.
     * @return The amount.
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Sets the transaction amount.
     * @param amount The amount.
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Gets the type of transaction.
     * @return The {@link TransactionType}.
     */
    public TransactionType getType() { return type; }

    /**
     * Sets the type of transaction.
     * @param type The {@link TransactionType}.
     */
    public void setType(TransactionType type) { this.type = type; }

    /**
     * Gets the status of the transaction.
     * @return The {@link TransactionStatus}.
     */
    public TransactionStatus getStatus() { return status; }

    /**
     * Sets the status of the transaction.
     * @param status The {@link TransactionStatus}.
     */
    public void setStatus(TransactionStatus status) { this.status = status; }

    /**
     * Gets the timestamp of when the transaction occurred.
     * @return The timestamp.
     */
    public Timestamp getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of the transaction.
     * @param timestamp The timestamp.
     */
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    /**
     * Returns a string representation of the Transaction.
     * @return A formatted string with ID, Sender, Receiver, Amount, and Status.
     */
    @Override
    public String toString() {
        return "Transaction [ID=" + transactionId + ", From=" + senderId + ", To=" + receiverId + ", Amount=" + amount + ", Status=" + status + "]";
    }
}