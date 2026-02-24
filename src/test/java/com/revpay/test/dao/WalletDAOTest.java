package com.revpay.test.dao;

import com.revpay.dao.UserDAO;
import com.revpay.dao.WalletDAO;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.WalletDAO}.
 * <p>
 * Verifies wallet creation, balance retrieval, and manual balance updates.
 * </p>
 */
public class WalletDAOTest {

    private WalletDAO walletDAO = new WalletDAO();
    private UserDAO userDAO = new UserDAO();
    private int tempUserId;

    /**
     * Setup: Create a user so we can attach a wallet to them.
     */
    @Before
    public void setUp() {
        String email = "wallet_test_" + System.currentTimeMillis() + "@revpay.com";
        User user = new User(email, "9998887777", "pass", "1234", "Wallet Tester", Role.PERSONAL);
        
        userDAO.registerUser(user);
        
        User saved = userDAO.getUserByEmail(email);
        if (saved != null) {
            this.tempUserId = saved.getUserId();
        } else {
            fail("Setup Failed: Could not create temporary user.");
        }
    }

    /**
     * Teardown: Remove the user (database cascade will remove the wallet).
     */
    @After
    public void tearDown() {
        if (tempUserId > 0) {
            userDAO.deleteUser(tempUserId);
        }
    }

    /**
     * Test Scenario: Create wallet, check zero balance, update balance, verify new balance.
     */
    @Test
    public void testWalletLifecycle() {
        // 1. Create Wallet
        walletDAO.createWallet(tempUserId);
        
        // 2. Check Initial Balance (Should be 0.00)
        BigDecimal initialBalance = walletDAO.getBalance(tempUserId);
        assertEquals("Initial balance should be zero", 
                     0, initialBalance.compareTo(BigDecimal.ZERO));

        // 3. Update Balance (Simulate deposit logic done elsewhere)
        BigDecimal newAmount = new BigDecimal("500.00");
        boolean updated = walletDAO.updateBalance(tempUserId, newAmount);
        assertTrue("Balance update should succeed", updated);

        // 4. Verify New Balance
        BigDecimal currentBalance = walletDAO.getBalance(tempUserId);
        assertEquals("Balance should match the updated amount", 
                     newAmount, currentBalance);

        System.out.println("✅ Wallet Lifecycle Test Passed");
    }
}