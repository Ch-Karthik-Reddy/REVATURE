package com.revpay.model;

import java.math.BigDecimal;

/**
 * Model class representing a request for funds between users.
 * <p>
 * A {@code PaymentRequest} is created when a user asks another user for money.
 * It tracks who asked (requester), who owes (payer), the amount, and the current
 * status of the request (e.g., PENDING, ACCEPTED).
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class PaymentRequest {
    private int requestId;
    private int requesterId;
    private int payerId;
    private BigDecimal amount;
    private String status; // PENDING, ACCEPTED, DECLINED

    /**
     * Default constructor.
     */
    public PaymentRequest() {}

    /**
     * Parameterized constructor for creating a new Payment Request.
     * <p>
     * The status is automatically initialized to "PENDING".
     * </p>
     *
     * @param requesterId The ID of the user asking for money.
     * @param payerId     The ID of the user who needs to pay.
     * @param amount      The amount requested.
     */
    public PaymentRequest(int requesterId, int payerId, BigDecimal amount) {
        this.requesterId = requesterId;
        this.payerId = payerId;
        this.amount = amount;
        this.status = "PENDING";
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique Request ID.
     * @return The request ID.
     */
    public int getRequestId() { return requestId; }

    /**
     * Sets the unique Request ID.
     * @param requestId The request ID.
     */
    public void setRequestId(int requestId) { this.requestId = requestId; }

    /**
     * Gets the ID of the user requesting the money.
     * @return The requester ID.
     */
    public int getRequesterId() { return requesterId; }

    /**
     * Sets the ID of the user requesting the money.
     * @param requesterId The requester ID.
     */
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    /**
     * Gets the ID of the user expected to pay.
     * @return The payer ID.
     */
    public int getPayerId() { return payerId; }

    /**
     * Sets the ID of the user expected to pay.
     * @param payerId The payer ID.
     */
    public void setPayerId(int payerId) { this.payerId = payerId; }

    /**
     * Gets the amount requested.
     * @return The amount as BigDecimal.
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Sets the amount requested.
     * @param amount The amount as BigDecimal.
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Gets the status of the request.
     * @return The status string (PENDING, ACCEPTED, DECLINED).
     */
    public String getStatus() { return status; }

    /**
     * Sets the status of the request.
     * @param status The new status.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns a string representation of the Request.
     * @return A formatted string for display.
     */
    @Override
    public String toString() {
        return "ID: " + requestId + " | Amount: $" + amount + " | Status: " + status;
    }
}