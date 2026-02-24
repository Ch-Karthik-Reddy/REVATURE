package com.revpay.test.model;

import com.revpay.model.LoanStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link LoanStatus} enumeration.
 * <p>
 * Verifies that the defined constants exist and function correctly.
 * Critical for ensuring database string mapping remains consistent.
 * </p>
 */
public class LoanStatusTest {

    /**
     * Test that the Enum contains exactly the expected number of states.
     */
    @Test
    public void testEnumCount() {
        // We expect exactly 4 states: PENDING, APPROVED, REJECTED, PAID
        assertEquals("Enum should have 4 states", 4, LoanStatus.values().length);
    }

    /**
     * Test that valueOf returns the correct Enum constant.
     * This verifies spelling matches what the database expects.
     */
    @Test
    public void testEnumValues() {
        assertEquals(LoanStatus.PENDING, LoanStatus.valueOf("PENDING"));
        assertEquals(LoanStatus.APPROVED, LoanStatus.valueOf("APPROVED"));
        assertEquals(LoanStatus.REJECTED, LoanStatus.valueOf("REJECTED"));
        assertEquals(LoanStatus.PAID, LoanStatus.valueOf("PAID"));
    }
}