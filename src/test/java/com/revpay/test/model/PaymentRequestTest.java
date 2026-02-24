package com.revpay.test.model;

import com.revpay.model.PaymentRequest;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link PaymentRequest} model class.
 * <p>
 * Verifies that the payment request object is initialized with correct defaults
 * and data is stored accurately.
 * </p>
 */
public class PaymentRequestTest {

    /**
     * Test the parameterized constructor.
     * Verifies that 'status' defaults to "PENDING" automatically.
     */
    @Test
    public void testRequestConstructor() {
        int requester = 10;
        int payer = 20;
        BigDecimal amt = new BigDecimal("45.50");

        PaymentRequest req = new PaymentRequest(requester, payer, amt);

        assertEquals("Requester ID should match", requester, req.getRequesterId());
        assertEquals("Payer ID should match", payer, req.getPayerId());
        assertEquals("Amount should match", amt, req.getAmount());
        
        // Critical Logic Check
        assertEquals("Default status should be PENDING", "PENDING", req.getStatus());
    }

    /**
     * Test Setters and Getters.
     */
    @Test
    public void testSetters() {
        PaymentRequest req = new PaymentRequest();
        
        req.setRequestId(99);
        req.setStatus("ACCEPTED");
        req.setAmount(new BigDecimal("100.00"));

        assertEquals(99, req.getRequestId());
        assertEquals("ACCEPTED", req.getStatus());
        assertEquals(new BigDecimal("100.00"), req.getAmount());
    }

    /**
     * Test toString formatting.
     */
    @Test
    public void testToString() {
        PaymentRequest req = new PaymentRequest(1, 2, new BigDecimal("10.00"));
        req.setRequestId(5);
        
        String result = req.toString();
        
        assertTrue("Output should contain ID", result.contains("ID: 5"));
        assertTrue("Output should contain Amount", result.contains("$10.00"));
    }
}