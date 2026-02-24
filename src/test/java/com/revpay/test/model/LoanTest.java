package com.revpay.test.model;

import com.revpay.model.Loan;
import com.revpay.model.LoanStatus;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Loan} model class.
 * <p>
 * Verifies object creation, default values, and data integrity.
 * </p>
 */
public class LoanTest {

    /**
     * Test the parameterized constructor.
     * Ensures that when a user applies for a loan, the status is PENDING by default.
     */
    @Test
    public void testLoanConstructorDefaults() {
        int userId = 50;
        BigDecimal amount = new BigDecimal("10000.00");
        String reason = "Business Expansion";

        Loan loan = new Loan(userId, amount, reason);

        assertEquals("User ID should match", userId, loan.getUserId());
        assertEquals("Amount should match", amount, loan.getAmount());
        assertEquals("Reason should match", reason, loan.getReason());
        
        // Critical Logic Check
        assertEquals("Default status should be PENDING", LoanStatus.PENDING, loan.getStatus());
    }

    /**
     * Test Setters and Getters.
     */
    @Test
    public void testSettersAndGetters() {
        Loan loan = new Loan();
        
        loan.setLoanId(101);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setAmount(new BigDecimal("500.00"));

        assertEquals(101, loan.getLoanId());
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertEquals(new BigDecimal("500.00"), loan.getAmount());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        Loan loan = new Loan(1, new BigDecimal("100.00"), "Emergency");
        String result = loan.toString();
        
        assertTrue("toString should contain Amount", result.contains("$100.00"));
        assertTrue("toString should contain Reason", result.contains("Emergency"));
    }
}