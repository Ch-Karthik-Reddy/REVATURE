package com.revpay.model;

/**
 * Enumeration representing the type of User account.
 * <p>
 * This distinction is critical for access control:
 * <ul>
 * <li><b>PERSONAL:</b> Standard users who can send/receive money and pay bills.</li>
 * <li><b>BUSINESS:</b> Commercial users who can issue invoices and apply for loans.</li>
 * </ul>
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public enum Role {

    /**
     * Represents a standard individual user.
     * Can perform P2P transfers and pay requests.
     */
    PERSONAL,

    /**
     * Represents a business entity.
     * Has additional privileges like creating invoices and requesting loans.
     */
    BUSINESS
}