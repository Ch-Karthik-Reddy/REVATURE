package com.revpay.test.service;

import com.revpay.model.Role;
import com.revpay.model.User;
import com.revpay.service.UserService;
import com.revpay.util.SecurityUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link UserService}.
 * <p>
 * Verifies the Authentication flow and the "Register -> Wallet Creation" chain.
 * </p>
 */
public class UserServiceTest {

    private UserService userService = new UserService();
    private int testUserId;
    private static final String TEST_EMAIL = "auth_test_" + System.currentTimeMillis() + "@revpay.com";
    private static final String TEST_PASS = "securePass123";

    /**
     * Setup: Ensure we start with a clean state (though unique emails handle most conflicts).
     */
    @Before
    public void setUp() {
        // Setup logic handled inside tests to simulate flow
    }

    /**
     * Teardown: Delete the user created during testing.
     */
    @After
    public void tearDown() {
        if (testUserId > 0) {
            userService.deleteAccount(testUserId);
        }
    }

    /**
     * Test Scenario: Register a user, verify wallet exists, then login.
     */
    @Test
    public void testRegistrationAndLoginFlow() {
        // 1. Prepare User Data (Hash the password before sending to service, 
        //    or let Controller handle hashing. Assuming Service receives hashed obj for now based on your User model)
        //    *Correction*: Usually Service/Controller hashes. Here we simulate Controller passing a hashed object.
        String hashedPassword = SecurityUtil.hashPassword(TEST_PASS);
        
        User newUser = new User(TEST_EMAIL, "555-0199", hashedPassword, "1234", "Auth Tester", Role.PERSONAL);

        // 2. Test Registration
        boolean registered = userService.registerUser(newUser);
        assertTrue("Registration should succeed", registered);

        // 3. Retrieve ID for cleanup
        testUserId = userService.getUserIdByEmail(TEST_EMAIL);
        assertTrue("User ID should be valid", testUserId > 0);

        // 4. Verify Wallet Creation (Auto-created by service)
        BigDecimal balance = userService.getBalance(testUserId);
        assertNotNull("Balance should not be null", balance);
        assertEquals("Initial balance should be zero", 0, balance.compareTo(BigDecimal.ZERO));

        // 5. Test Login (Success)
        // Note: Login takes RAW password and compares with DB HASH
        User loggedInUser = userService.login(TEST_EMAIL, TEST_PASS);
        assertNotNull("Login should succeed with correct password", loggedInUser);
        assertEquals("Login should return correct user", TEST_EMAIL, loggedInUser.getEmail());

        // 6. Test Login (Failure)
        User failedUser = userService.login(TEST_EMAIL, "wrongPassword");
        assertNull("Login should fail with wrong password", failedUser);

        System.out.println("  User Service Flow Test Passed");
    }
}