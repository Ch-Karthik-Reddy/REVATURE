package com.revpay.test.util;

import com.revpay.util.SecurityUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link SecurityUtil} class.
 * <p>
 * Verifies that the BCrypt implementation correctly hashes passwords
 * and validates credentials.
 * </p>
 */
public class SecurityUtilTest {

    /**
     * Test Hashing Logic.
     * The hash should never be null, and never equal the plain text.
     */
    @Test
    public void testHashGeneration() {
        String password = "mySecretPassword";
        String hash = SecurityUtil.hashPassword(password);

        assertNotNull("Hash should not be null", hash);
        assertNotEquals("Hash should scramble the password", password, hash);
        assertTrue("Hash should follow BCrypt format", hash.startsWith("$2a$"));
    }

    /**
     * Test Password Verification (Success Case).
     */
    @Test
    public void testVerifySuccess() {
        String password = "password123";
        String hash = SecurityUtil.hashPassword(password);

        boolean result = SecurityUtil.verifyPassword(password, hash);
        assertTrue("Correct password should verify successfully", result);
    }

    /**
     * Test Password Verification (Failure Case).
     */
    @Test
    public void testVerifyFailure() {
        String password = "password123";
        String hash = SecurityUtil.hashPassword(password);

        boolean result = SecurityUtil.verifyPassword("wrongPassword", hash);
        assertFalse("Wrong password should fail verification", result);
    }
}