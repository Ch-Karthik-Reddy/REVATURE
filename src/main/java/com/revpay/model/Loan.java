package com.revpay.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing a Loan application in the RevPay system.
 * <p>
 * This class stores details about a loan request made by a Business user,
 * including the requested amount, the reason for the loan, and its current approval status.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class Loan {
    private int loanId;
    private int userId;
    private BigDecimal amount;
    private String reason;
    private LoanStatus status; // Enum: PENDING, APPROVED, REJECTED
    private Timestamp appliedAt;

    /**
     * Default constructor.
     */
    public Loan() {}

    /**
     * Parameterized constructor for creating a new Loan application.
     * <p>
     * The status is automatically initialized to {@link LoanStatus#PENDING}.
     * </p>
     *
     * @param userId The unique ID of the user applying for the loan.
     * @param amount The amount of money requested.
     * @param reason The reason for the loan request.
     */
    public Loan(int userId, BigDecimal amount, String reason) {
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.status = LoanStatus.PENDING; // Default status
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique Loan ID.
     * @return The loan ID.
     */
    public int getLoanId() { return loanId; }

    /**
     * Sets the unique Loan ID.
     * @param loanId The loan ID.
     */
    public void setLoanId(int loanId) { this.loanId = loanId; }

    /**
     * Gets the ID of the user who applied for the loan.
     * @return The user ID.
     */
    public int getUserId() { return userId; }

    /**
     * Sets the ID of the user applying for the loan.
     * @param userId The user ID.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the loan amount.
     * @return The amount as BigDecimal.
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Sets the loan amount.
     * @param amount The amount as BigDecimal.
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Gets the reason provided for the loan.
     * @return The reason string.
     */
    public String getReason() { return reason; }

    /**
     * Sets the reason for the loan.
     * @param reason The reason string.
     */
    public void setReason(String reason) { this.reason = reason; }

    /**
     * Gets the current status of the loan application.
     * @return The {@link LoanStatus} enum.
     */
    public LoanStatus getStatus() { return status; }

    /**
     * Sets the status of the loan application.
     * @param status The new status (e.g., APPROVED, REJECTED).
     */
    public void setStatus(LoanStatus status) { this.status = status; }

    /**
     * Gets the timestamp when the loan was applied for.
     * @return The application timestamp.
     */
    public Timestamp getAppliedAt() { return appliedAt; }

    /**
     * Sets the timestamp when the loan was applied for.
     * @param appliedAt The application timestamp.
     */
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }

    /**
     * Returns a string representation of the Loan.
     * @return A formatted string with Amount, Status, and Reason.
     */
    @Override
    public String toString() {
        return "Loan [Amount=$" + amount + ", Status=" + status + ", Reason=" + reason + "]";
    }
}