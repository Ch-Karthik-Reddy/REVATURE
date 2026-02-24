package com.revpay.test.model;

import com.revpay.model.Role;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Role} enumeration.
 * <p>
 * Verifies that the user roles are strictly defined as PERSONAL and BUSINESS.
 * </p>
 */
public class RoleTest {

    /**
     * Test that the Enum contains exactly the expected number of roles.
     * If a developer adds 'ADMIN' later, they must update this test.
     */
    @Test
    public void testEnumCount() {
        assertEquals("Enum should have exactly 2 roles", 2, Role.values().length);
    }

    /**
     * Test that valueOf returns the correct Enum constant.
     * This verifies spelling matches what the database expects.
     */
    @Test
    public void testEnumValues() {
        assertEquals(Role.PERSONAL, Role.valueOf("PERSONAL"));
        assertEquals(Role.BUSINESS, Role.valueOf("BUSINESS"));
    }
}