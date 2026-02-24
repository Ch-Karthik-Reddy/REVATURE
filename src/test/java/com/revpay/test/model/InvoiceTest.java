package com.revpay.test.model;

import com.revpay.model.Invoice;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Invoice} model class.
 * <p>
 * Verifies that the Invoice object correctly stores and retrieves data,
 * and that the constructor initializes default values correctly.
 * </p>
 */
public class InvoiceTest {

    /**
     * Test the parameterized constructor.
     * Verifies that fields are set correctly and 'status' defaults to "PENDING".
     */
    @Test
    public void testInvoiceConstructor() {
        int businessId = 101;
        String email = "client@example.com";
        BigDecimal amount = new BigDecimal("250.00");
        String desc = "Web Development";

        Invoice invoice = new Invoice(businessId, email, amount, desc);

        assertEquals("Business ID should match", businessId, invoice.getBusinessId());
        assertEquals("Customer email should match", email, invoice.getCustomerEmail());
        assertEquals("Amount should match", amount, invoice.getAmount());
        assertEquals("Description should match", desc, invoice.getDescription());
        
        // Critical Logic Check: Default status
        assertEquals("Default status should be PENDING", "PENDING", invoice.getStatus());
    }

    /**
     * Test Getters and Setters.
     * Verifies that data can be modified after object creation.
     */
    @Test
    public void testSettersAndGetters() {
        Invoice invoice = new Invoice();
        
        invoice.setInvoiceId(1);
        invoice.setStatus("PAID");
        invoice.setAmount(new BigDecimal("100.00"));

        assertEquals(1, invoice.getInvoiceId());
        assertEquals("PAID", invoice.getStatus());
        assertEquals(new BigDecimal("100.00"), invoice.getAmount());
    }

    /**
     * Test toString method.
     * Ensures the string representation contains key identifiers.
     */
    @Test
    public void testToString() {
        Invoice invoice = new Invoice(1, "test@test.com", new BigDecimal("50.00"), "Test");
        invoice.setInvoiceId(99);
        
        String result = invoice.toString();
        
        assertTrue("toString should contain Invoice ID", result.contains("#99"));
        assertTrue("toString should contain Amount", result.contains("$50.00"));
        assertTrue("toString should contain Status", result.contains("PENDING"));
    }
}