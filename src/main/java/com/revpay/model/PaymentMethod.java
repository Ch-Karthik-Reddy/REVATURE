package com.revpay.model;

import java.sql.Date;

/**
 * Model class representing a user's Payment Method (e.g., Credit or Debit
 * card).
 * <p>
 * This class stores card details securely.
 * <b>Security Note:</b> The {@link #toString()} method automatically masks the
 * card number to prevent sensitive data from being displayed in logs or UI.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class PaymentMethod {
    private int methodId;
    private int userId;
    private String cardNumber; // Stored encrypted in DB, but raw/masked here depending on context
    private String cardType; // CREDIT or DEBIT
    private Date expiryDate;

    /**
     * Default constructor.
     */
    public PaymentMethod() {
    }

    /**
     * Parameterized constructor for adding a new card.
     *
     * @param userId     The ID of the user owning the card.
     * @param cardNumber The full 16-digit card number.
     * @param cardType   The type of card (e.g., "CREDIT", "DEBIT").
     * @param expiryDate The expiration date of the card.
     */
    public PaymentMethod(int userId, String cardNumber, String cardType, Date expiryDate) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expiryDate = expiryDate;
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique ID of this payment method.
     * 
     * @return The method ID.
     */
    public int getMethodId() {
        return methodId;
    }

    /**
     * Sets the unique ID of this payment method.
     * 
     * @param methodId The method ID.
     */
    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    /**
     * Gets the user ID associated with this card.
     * 
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this card.
     * 
     * @param userId The user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the card number.
     * 
     * @return The card number string.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number.
     * 
     * @param cardNumber The card number string.
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the type of the card.
     * 
     * @return The card type (e.g., CREDIT, DEBIT).
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the type of the card.
     * 
     * @param cardType The card type.
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the expiry date of the card.
     * 
     * @return The expiry date.
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the expiry date of the card.
     * 
     * @param expiryDate The expiry date.
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Returns a string representation of the Payment Method.
     * <p>
     * <b>Security Feature:</b> This method masks the first 12 digits of the card
     * number
     * (e.g., {@code ****-****-****-1234}) to ensure PCI-DSS compliance in logs.
     * </p>
     *
     * @return A secure string representation of the card.
     */
    @Override
    public String toString() {
        // Mask the card number for display (e.g., ************1234)
        String masked = (cardNumber != null && cardNumber.length() > 4)
                ? "****-****-****-" + cardNumber.substring(cardNumber.length() - 4)
                : "****";
        return "[" + cardType + "] " + masked + " (Exp: " + expiryDate + ")";
    }
}