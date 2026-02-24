package com.revpay.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing an Invoice in the RevPay system.
 * <p>
 * An invoice is created by a Business user and sent to a Customer email.
 * It tracks the amount owed, the description of services, and the payment status.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class Invoice {
    private int invoiceId;
    private int businessId;
    private String customerEmail;
    private BigDecimal amount;
    private String description;
    private String status; // PENDING, PAID, CANCELLED
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public Invoice() {}

    /**
     * Parameterized constructor for creating a new Invoice.
     * <p>
     * The status is automatically set to "PENDING".
     * </p>
     *
     * @param businessId    The ID of the business issuing the invoice.
     * @param customerEmail The email address of the customer being billed.
     * @param amount        The total amount to be paid.
     * @param description   A brief description of the item or service.
     */
    public Invoice(int businessId, String customerEmail, BigDecimal amount, String description) {
        this.businessId = businessId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.description = description;
        this.status = "PENDING";
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique Invoice ID.
     * @return The invoice ID.
     */
    public int getInvoiceId() { return invoiceId; }

    /**
     * Sets the unique Invoice ID.
     * @param invoiceId The invoice ID.
     */
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    /**
     * Gets the Business ID associated with this invoice.
     * @return The business user ID.
     */
    public int getBusinessId() { return businessId; }

    /**
     * Sets the Business ID.
     * @param businessId The business user ID.
     */
    public void setBusinessId(int businessId) { this.businessId = businessId; }

    /**
     * Gets the customer's email address.
     * @return The customer email.
     */
    public String getCustomerEmail() { return customerEmail; }

    /**
     * Sets the customer's email address.
     * @param customerEmail The customer email.
     */
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    /**
     * Gets the invoice amount.
     * @return The amount as BigDecimal.
     */
    public BigDecimal getAmount() { return amount; }

    /**
     * Sets the invoice amount.
     * @param amount The amount as BigDecimal.
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /**
     * Gets the description of the invoice.
     * @return The description string.
     */
    public String getDescription() { return description; }

    /**
     * Sets the description of the invoice.
     * @param description The description string.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the current status of the invoice (e.g., PENDING, PAID).
     * @return The status string.
     */
    public String getStatus() { return status; }

    /**
     * Sets the status of the invoice.
     * @param status The new status.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Gets the timestamp when the invoice was created.
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() { return createdAt; }

    /**
     * Sets the timestamp when the invoice was created.
     * @param createdAt The creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Returns a string representation of the Invoice.
     * @return A formatted string with ID, Email, Amount, and Status.
     */
    @Override
    public String toString() {
        return "Invoice #" + invoiceId + " | To: " + customerEmail + " | $" + amount + " | Status: " + status;
    }
}