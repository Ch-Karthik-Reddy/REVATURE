package com.revpay.test.model;

import com.revpay.model.PaymentMethod;
import org.junit.Test;
import java.sql.Date;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link PaymentMethod} model class.
 * <p>
 * Verifies object initialization and, most importantly, the security masking
 * logic in the {@code toString()} method.
 * </p>
 */
public class PaymentMethodTest {

    /**
     * Test the Security Masking Feature.
     * Ensures that sensitive card numbers are NOT printed in plain text.
     */
    @Test
    public void testToStringMasking() {
        String realCardNumber = "1234567812345678"; // 16 digits
        Date expiry = Date.valueOf("2025-12-31");
        
        PaymentMethod pm = new PaymentMethod(1, realCardNumber, "CREDIT", expiry);
        
        String displayString = pm.toString();
        
        // 1. Ensure the full number is NOT visible
        assertFalse("Security Fail: Full card number exposed!", displayString.contains(realCardNumber));
        
        // 2. Ensure the last 4 digits ARE visible (for user identification)
        assertTrue("User Experience Fail: Last 4 digits should be visible", displayString.contains("5678"));
        
        // 3. Ensure the masking format is correct
        assertTrue("Format Fail: Should contain masking characters", displayString.contains("****-****-****-"));
        
        System.out.println("✅ Payment Method Security Masking Test Passed");
    }

    /**
     * Test Constructor and Getters.
     */
    @Test
    public void testPaymentMethodData() {
        int userId = 101;
        String type = "DEBIT";
        
        PaymentMethod pm = new PaymentMethod(userId, "1111222233334444", type, Date.valueOf("2024-01-01"));
        
        assertEquals(userId, pm.getUserId());
        assertEquals(type, pm.getCardType());
        assertEquals("1111222233334444", pm.getCardNumber()); // Getter returns raw data (internal use)
    }
}