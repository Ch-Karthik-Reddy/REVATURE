package com.revpay.test.model;

import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link User} model class.
 * <p>
 * Verifies object creation, data access, and security precautions in toString().
 * </p>
 */
public class UserTest {

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testUserConstructor() {
        String email = "alice@example.com";
        String phone = "1234567890";
        String passHash = "hashed_secret_123";
        String pin = "1234";
        String name = "Alice Smith";
        Role role = Role.PERSONAL;

        User user = new User(email, phone, passHash, pin, name, role);

        assertEquals("Email match", email, user.getEmail());
        assertEquals("Phone match", phone, user.getPhoneNumber());
        assertEquals("Password Hash match", passHash, user.getPasswordHash());
        assertEquals("PIN match", pin, user.getTransactionPin());
        assertEquals("Name match", name, user.getFullName());
        assertEquals("Role match", role, user.getRole());
    }

    /**
     * Test Setters.
     */
    @Test
    public void testSetters() {
        User user = new User();
        user.setUserId(5);
        user.setFullName("Bob");
        user.setRole(Role.BUSINESS);

        assertEquals(5, user.getUserId());
        assertEquals("Bob", user.getFullName());
        assertEquals(Role.BUSINESS, user.getRole());
    }

    /**
     * Test toString Security.
     * Ensures that sensitive fields (Password, PIN) are NOT included in the logs.
     */
    @Test
    public void testToStringSecurity() {
        User user = new User("test@email.com", "111", "SUPER_SECRET_PASSWORD", "9999", "Test User", Role.PERSONAL);
        
        String output = user.toString();
        
        // 1. Check that safe info IS present
        assertTrue("Output should contain email", output.contains("test@email.com"));
        
        // 2. Check that sensitive info is ABSENT
        assertFalse("SECURITY FAIL: Password leaked in toString!", output.contains("SUPER_SECRET_PASSWORD"));
        assertFalse("SECURITY FAIL: PIN leaked in toString!", output.contains("9999"));
    }
}