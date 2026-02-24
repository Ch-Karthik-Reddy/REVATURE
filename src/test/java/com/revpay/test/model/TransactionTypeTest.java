package com.revpay.test.model;

import com.revpay.model.TransactionType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TransactionType} enumeration.
 * <p>
 * Verifies that the transaction categories are correctly defined.
 * </p>
 */
public class TransactionTypeTest {

    /**
     * Test that the Enum contains exactly the expected number of types.
     */
    @Test
    public void testEnumCount() {
        // We expect 4 types: TRANSFER, DEPOSIT, WITHDRAWAL, PAYMENT
        assertEquals("Enum should have 4 types", 4, TransactionType.values().length);
    }

    /**
     * Test that valueOf returns the correct Enum constant.
     */
    @Test
    public void testEnumValues() {
        assertEquals(TransactionType.TRANSFER, TransactionType.valueOf("TRANSFER"));
        assertEquals(TransactionType.DEPOSIT, TransactionType.valueOf("DEPOSIT"));
        assertEquals(TransactionType.WITHDRAWAL, TransactionType.valueOf("WITHDRAWAL"));
        assertEquals(TransactionType.PAYMENT, TransactionType.valueOf("PAYMENT"));
    }
}