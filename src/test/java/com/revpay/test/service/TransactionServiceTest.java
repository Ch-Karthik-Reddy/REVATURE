package com.revpay.test.service;

import com.revpay.dao.UserDAO;
import com.revpay.dao.WalletDAO;
import com.revpay.model.Role;
import com.revpay.model.User;
import com.revpay.service.TransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link TransactionService}.
 * <p>
 * Verifies that business rules (negative amounts, self-transfers) are enforcing correctly
 * before the data hits the database.
 * </p>
 */
public class TransactionServiceTest {

    private TransactionService transactionService = new TransactionService();
    private UserDAO userDAO = new UserDAO();
    private WalletDAO walletDAO = new WalletDAO();

    private int senderId;
    private int receiverId;

    /**
     * Setup: Create two users and fund the sender's wallet.
     */
    @Before
    public void setUp() {
        // 1. Create Sender
        String senderEmail = "service_sender_" + System.currentTimeMillis() + "@test.com";
        User sender = new User(senderEmail, "111", "hash", "0000", "Service Sender", Role.PERSONAL);
        userDAO.registerUser(sender);
        senderId = userDAO.getUserByEmail(senderEmail).getUserId();
        
        // Fund Sender ($1000)
        walletDAO.createWallet(senderId);
        walletDAO.updateBalance(senderId, new BigDecimal("1000.00"));

        // 2. Create Receiver
        String receiverEmail = "service_receiver_" + System.currentTimeMillis() + "@test.com";
        User receiver = new User(receiverEmail, "222", "hash", "0000", "Service Receiver", Role.PERSONAL);
        userDAO.registerUser(receiver);
        receiverId = userDAO.getUserByEmail(receiverEmail).getUserId();
        walletDAO.createWallet(receiverId);
    }

    /**
     * Teardown: Cleanup users.
     */
    @After
    public void tearDown() {
        if (senderId > 0) userDAO.deleteUser(senderId);
        if (receiverId > 0) userDAO.deleteUser(receiverId);
    }

    /**
     * Test: Should FAIL when amount is negative.
     */
    @Test
    public void testNegativeTransfer() {
        BigDecimal negativeAmount = new BigDecimal("-50.00");
        boolean result = transactionService.processTransfer(senderId, receiverId, negativeAmount);
        
        assertFalse("Service should block negative transfers", result);
    }

    /**
     * Test: Should FAIL when amount is Zero.
     */
    @Test
    public void testZeroTransfer() {
        boolean result = transactionService.processTransfer(senderId, receiverId, BigDecimal.ZERO);
        
        assertFalse("Service should block zero transfers", result);
    }

    /**
     * Test: Should FAIL when sending to Self.
     */
    @Test
    public void testSelfTransfer() {
        BigDecimal amount = new BigDecimal("10.00");
        boolean result = transactionService.processTransfer(senderId, senderId, amount);
        
        assertFalse("Service should block self-transfers", result);
    }

    /**
     * Test: Should PASS when inputs are valid.
     */
    @Test
    public void testValidTransfer() {
        BigDecimal amount = new BigDecimal("100.00");
        boolean result = transactionService.processTransfer(senderId, receiverId, amount);
        
        assertTrue("Valid transfer should succeed", result);
    }
}