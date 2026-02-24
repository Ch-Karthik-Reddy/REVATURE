package com.revpay.test.model;

import com.revpay.model.TransactionStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TransactionStatus} enumeration.
 * <p>
 * Verifies that the transaction states are correctly defined.
 * </p>
 */
public class TransactionStatusTest {

    /**
     * Test that the Enum contains exactly the expected number of states.
     */
    @Test
    public void testEnumCount() {
        // We expect 3 states: SUCCESS, FAILED, PENDING
        assertEquals("Enum should have 3 states", 3, TransactionStatus.values().length);
    }

    /**
     * Test that valueOf returns the correct Enum constant.
     * This safeguards against accidental renaming which could break DB mapping.
     */
    @Test
    public void testEnumValues() {
        assertEquals(TransactionStatus.SUCCESS, TransactionStatus.valueOf("SUCCESS"));
        assertEquals(TransactionStatus.FAILED, TransactionStatus.valueOf("FAILED"));
        assertEquals(TransactionStatus.PENDING, TransactionStatus.valueOf("PENDING"));
    }
}