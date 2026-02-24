package com.revpay.test.dao; // Ensure package is correct

import com.revpay.dao.InvoiceDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Invoice;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.InvoiceDAO}.
 * <p>
 * This test creates a temporary Business User to ensure Foreign Key constraints
 * are satisfied, tests the invoice lifecycle, and cleans up afterwards.
 * </p>
 */
public class InvoiceDAOTest {

    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private UserDAO userDAO = new UserDAO();
    private User testBusinessUser;
    private int businessId;

    /**
     * Setup: Create a temporary Business User before the test runs.
     * This ensures we have a valid Foreign Key for the invoice.
     */
    @Before
    public void setUp() {
        // 1. Create a unique test user
        String email = "test_biz_" + System.currentTimeMillis() + "@revpay.com";
        testBusinessUser = new User(email, "9999999999", "hashedpass", "0000", "Test Biz", Role.BUSINESS);

        // 2. Register user in DB
        userDAO.registerUser(testBusinessUser);

        // 3. Retrieve the generated ID
        User savedUser = userDAO.getUserByEmail(email);
        if (savedUser != null) {
            this.businessId = savedUser.getUserId();
        } else {
            fail("Failed to set up test environment: User creation failed.");
        }
    }

    /**
     * Teardown: Delete the temporary user after the test finishes.
     * This cleans up the User AND the Invoices (cascade delete).
     */
    @After
    public void tearDown() {
        if (businessId > 0) {
            userDAO.deleteUser(businessId);
        }
    }

    /**
     * Test Scenario: Create an invoice, verify it exists, and then mark it as paid.
     */
    @Test
    public void testInvoiceLifecycle() {
        // 1. Prepare Test Data using the REAL Business ID
        String customerEmail = "customer_" + System.currentTimeMillis() + "@example.com";
        BigDecimal amount = new BigDecimal("99.99");
        String description = "Test Service Fee";

        Invoice newInvoice = new Invoice(businessId, customerEmail, amount, description);

        // 2. Test Creation
        boolean created = invoiceDAO.createInvoice(newInvoice);
        assertTrue("Invoice should be created successfully", created);

        // 3. Test Retrieval (For Customer)
        List<Invoice> pendingInvoices = invoiceDAO.getInvoicesForCustomer(customerEmail);
        assertFalse("Customer should have pending invoices", pendingInvoices.isEmpty());

        // Verify details of the most recent invoice
        Invoice retrieved = pendingInvoices.get(pendingInvoices.size() - 1);
        assertEquals("Amount should match", amount, retrieved.getAmount());
        assertEquals("Description should match", description, retrieved.getDescription());
        assertEquals("Status should be PENDING", "PENDING", retrieved.getStatus());

        // 4. Test Marking as Paid
        boolean markedPaid = invoiceDAO.markAsPaid(retrieved.getInvoiceId());
        assertTrue("Invoice should be marked as paid successfully", markedPaid);

        // 5. Verify Status Update
        Invoice paidInvoice = invoiceDAO.getInvoiceById(retrieved.getInvoiceId());
        assertNotNull("Invoice should exist", paidInvoice);
        assertEquals("Status should now be PAID", "PAID", paidInvoice.getStatus());

        System.out.println("✅ Invoice Lifecycle Test Passed");
    }
}