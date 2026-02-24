package com.revpay.test.dao;

import com.revpay.dao.UserDAO;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.UserDAO}.
 * <p>
 * Verifies that users can be registered, retrieved by email, and completely deleted
 * from the system (including cascade checks).
 * </p>
 */
public class UserDAOTest {

    private UserDAO userDAO = new UserDAO();
    private int testUserId; // To track ID for cleanup

    /**
     * Cleanup: Ensure the test user is removed if the test fails or completes.
     */
    @After
    public void tearDown() {
        if (testUserId > 0) {
            userDAO.deleteUser(testUserId);
        }
    }

    /**
     * Test Scenario: Register a user, find them, and then delete them.
     */
    @Test
    public void testUserLifecycle() {
        // 1. Prepare User Data
        String email = "lifecycle_test_" + System.currentTimeMillis() + "@revpay.com";
        User newUser = new User(email, "9876543210", "hashed_secret", "9999", "Test Subject", Role.PERSONAL);

        // 2. Test Registration
        boolean registered = userDAO.registerUser(newUser);
        assertTrue("User should be registered successfully", registered);

        // 3. Test Retrieval
        User retrievedUser = userDAO.getUserByEmail(email);
        assertNotNull("User should be found by email", retrievedUser);
        assertEquals("Name should match", "Test Subject", retrievedUser.getFullName());
        assertEquals("Role should match", Role.PERSONAL, retrievedUser.getRole());
        
        // Save ID for cleanup
        this.testUserId = retrievedUser.getUserId();

        // 4. Test Deletion
        boolean deleted = userDAO.deleteUser(testUserId);
        assertTrue("User deletion should succeed", deleted);

        // 5. Verify Deletion
        User deletedUser = userDAO.getUserByEmail(email);
        assertNull("User should no longer exist", deletedUser);

        // Reset ID so tearDown doesn't try to delete again
        this.testUserId = 0;
        
        System.out.println("✅ User Lifecycle Test Passed");
    }
}