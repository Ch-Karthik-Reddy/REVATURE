package com.revpay.test.model;

import com.revpay.model.Wallet;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Wallet} model class.
 * <p>
 * Verifies that the Wallet object correctly manages balance data.
 * </p>
 */
public class WalletTest {

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testWalletConstructor() {
        int userId = 500;
        BigDecimal initial = new BigDecimal("0.00");

        Wallet wallet = new Wallet(userId, initial);

        assertEquals("User ID match", userId, wallet.getUserId());
        assertEquals("Balance match", initial, wallet.getBalance());
    }

    /**
     * Test Setters.
     */
    @Test
    public void testSetters() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(1);
        wallet.setUserId(2);
        wallet.setBalance(new BigDecimal("99.99"));

        assertEquals(1, wallet.getWalletId());
        assertEquals(2, wallet.getUserId());
        assertEquals(new BigDecimal("99.99"), wallet.getBalance());
    }
    
    /**
     * Test toString format.
     */
    @Test
    public void testToString() {
        Wallet w = new Wallet(10, new BigDecimal("500.00"));
        String output = w.toString();
        
        assertTrue("Output should contain Balance", output.contains("$500.00"));
        assertTrue("Output should contain User ID", output.contains("User=10"));
    }
}