package com.revpay.test.dao;

import com.revpay.dao.RequestDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.PaymentRequest;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.RequestDAO}.
 * <p>
 * Verifies the complete lifecycle of a Payment Request:
 * Creation -> Retrieval -> Status Update.
 * </p>
 */
public class RequestDAOTest {

    private RequestDAO requestDAO = new RequestDAO();
    private UserDAO userDAO = new UserDAO();
    
    private int requesterId;
    private int payerId;

    /**
     * Setup: Create two temporary users for the transaction.
     */
    @Before
    public void setUp() {
        // Create Requester
        String reqEmail = "req_" + System.currentTimeMillis() + "@revpay.com";
        userDAO.registerUser(new User(reqEmail, "111", "pass", "0000", "Requester", Role.PERSONAL));
        requesterId = userDAO.getUserByEmail(reqEmail).getUserId();

        // Create Payer
        String payEmail = "pay_" + System.currentTimeMillis() + "@revpay.com";
        userDAO.registerUser(new User(payEmail, "222", "pass", "0000", "Payer", Role.PERSONAL));
        payerId = userDAO.getUserByEmail(payEmail).getUserId();
    }

    /**
     * Teardown: Delete both users.
     */
    @After
    public void tearDown() {
        if (requesterId > 0) userDAO.deleteUser(requesterId);
        if (payerId > 0) userDAO.deleteUser(payerId);
    }

    /**
     * Test Scenario: Send a request, check if the payer sees it, and then accept it.
     */
    @Test
    public void testRequestLifecycle() {
        // 1. Create Request
        BigDecimal amount = new BigDecimal("50.00");
        PaymentRequest req = new PaymentRequest(requesterId, payerId, amount);
        
        boolean created = requestDAO.createRequest(req);
        assertTrue("Request should be created successfully", created);

        // 2. Retrieve Incoming Requests for Payer
        List<PaymentRequest> incoming = requestDAO.getIncomingRequests(payerId);
        assertFalse("Payer should have incoming requests", incoming.isEmpty());
        
        PaymentRequest retrieved = incoming.get(0);
        assertEquals("Amount matches", amount, retrieved.getAmount());
        assertEquals("Requester matches", requesterId, retrieved.getRequesterId());
        assertEquals("Status is PENDING", "PENDING", retrieved.getStatus());

        // 3. Update Status (Simulate Payment)
        boolean updated = requestDAO.updateStatus(retrieved.getRequestId(), "ACCEPTED");
        assertTrue("Status update should succeed", updated);

        // 4. Verify Update
        PaymentRequest finalReq = requestDAO.getRequestById(retrieved.getRequestId());
        assertEquals("Status is ACCEPTED", "ACCEPTED", finalReq.getStatus());

        System.out.println("✅ Payment Request Lifecycle Test Passed");
    }
}