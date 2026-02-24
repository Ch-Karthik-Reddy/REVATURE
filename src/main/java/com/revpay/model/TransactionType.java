package com.revpay.model;

/**
 * Enumeration representing the category of a financial transaction.
 * <p>
 * This helps classify records for statement generation and reporting:
 * <ul>
 * <li><b>TRANSFER:</b> Person-to-Person (P2P) money movement.</li>
 * <li><b>DEPOSIT:</b> Adding funds to the wallet.</li>
 * <li><b>WITHDRAWAL:</b> Removing funds from the wallet.</li>
 * <li><b>PAYMENT:</b> Paying an invoice or bill.</li>
 * </ul>
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public enum TransactionType {

    /**
     * Money sent from one user to another (P2P).
     */
    TRANSFER,

    /**
     * Money added to the user's wallet (Self-funding).
     */
    DEPOSIT,

    /**
     * Money removed from the wallet (Cash out).
     */
    WITHDRAWAL,

    /**
     * Payment made for a specific invoice or request.
     */
    PAYMENT
}