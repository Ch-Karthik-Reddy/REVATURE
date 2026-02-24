package com.revpay.test.dao;

import com.revpay.config.DatabaseConnection;
import com.revpay.dao.TransactionDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Role;
import com.revpay.model.User;
import com.revpay.model.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.TransactionDAO}.
 * <p>
 * Simulates real-world banking scenarios involving Deposits and P2P Transfers.
 * </p>
 */
public class TransactionDAOTest {

    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserDAO userDAO = new UserDAO();
    
    private int senderId;
    private int receiverId;

    /**
     * Setup: Create two users and initialize their wallets in the DB.
     */
    @Before
    public void setUp() {
        // 1. Create Sender
        String senderEmail = "sender_" + System.currentTimeMillis() + "@revpay.com";
        User sender = new User(senderEmail, "111", "pass", "0000", "Sender", Role.PERSONAL);
        userDAO.registerUser(sender);
        senderId = userDAO.getUserByEmail(senderEmail).getUserId();
        createWalletForUser(senderId); // Manually create wallet for test

        // 2. Create Receiver
        String receiverEmail = "receiver_" + System.currentTimeMillis() + "@revpay.com";
        User receiver = new User(receiverEmail, "222", "pass", "0000", "Receiver", Role.PERSONAL);
        userDAO.registerUser(receiver);
        receiverId = userDAO.getUserByEmail(receiverEmail).getUserId();
        createWalletForUser(receiverId);
    }

    /**
     * Helper: Manually create a wallet row since UserDAO.registerUser might not do it directly
     * depending on the implementation version.
     */
    private void createWalletForUser(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO wallets (user_id, balance) VALUES (?, 0.00)")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Teardown: Delete users (cascades to wallets and transactions).
     */
    @After
    public void tearDown() {
        if (senderId > 0) userDAO.deleteUser(senderId);
        if (receiverId > 0) userDAO.deleteUser(receiverId);
    }

    /**
     * Test Scenario: Deposit money, then transfer it to another user.
     */
    @Test
    public void testDepositAndTransfer() {
        // 1. Test Deposit
        BigDecimal depositAmount = new BigDecimal("100.00");
        boolean depositSuccess = transactionDAO.depositMoney(senderId, depositAmount);
        assertTrue("Deposit should be successful", depositSuccess);

        // 2. Test Transfer (Sender -> Receiver)
        BigDecimal transferAmount = new BigDecimal("40.00");
        boolean transferSuccess = transactionDAO.transferMoney(senderId, receiverId, transferAmount);
        assertTrue("Transfer should be successful", transferSuccess);

        // 3. Verify History for Sender (Should have Deposit AND Transfer)
        List<Transaction> senderHistory = transactionDAO.getTransactionHistory(senderId);
        assertFalse("Sender history should not be empty", senderHistory.isEmpty());
        // We expect at least 2 transactions (1 Deposit, 1 Transfer)
        assertTrue("Sender should have multiple transactions", senderHistory.size() >= 2);

        // 4. Verify Receiver History
        List<Transaction> receiverHistory = transactionDAO.getTransactionHistory(receiverId);
        assertFalse("Receiver history should not be empty", receiverHistory.isEmpty());
        assertEquals("Receiver should see the transfer amount", transferAmount, receiverHistory.get(0).getAmount());

        // 5. Test Insufficient Funds (Try to send 1000 when balance is ~60)
        boolean failTransfer = transactionDAO.transferMoney(senderId, receiverId, new BigDecimal("1000.00"));
        assertFalse("Transfer should fail due to low balance", failTransfer);

        System.out.println("✅ Transaction Logic Test Passed");
    }
}