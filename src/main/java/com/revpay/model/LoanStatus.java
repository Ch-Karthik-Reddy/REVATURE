package com.revpay.model;

/**
 * Enumeration representing the lifecycle states of a Loan.
 * <p>
 * This is used to track the progress of a loan application from submission to
 * repayment.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public enum LoanStatus {
    /**
     * The loan application has been submitted but not yet reviewed by the system or
     * admin.
     */
    PENDING,

    /**
     * The loan has been reviewed and approved. Funds are ready to be disbursed.
     */
    APPROVED,

    /**
     * The loan application was denied (e.g., due to low credit score or policy).
     */
    REJECTED,

    /**
     * The loan amount has been fully repaid by the user.
     */
    PAID
}