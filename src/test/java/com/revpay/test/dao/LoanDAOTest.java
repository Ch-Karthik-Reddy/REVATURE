package com.revpay.test.dao;

import com.revpay.dao.LoanDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Loan;
import com.revpay.model.LoanStatus;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.LoanDAO}.
 * <p>
 * Tests the creation and retrieval of Loan applications within the database.
 * Uses a temporary user to satisfy Foreign Key constraints.
 * </p>
 */
public class LoanDAOTest {

    private LoanDAO loanDAO = new LoanDAO();
    private UserDAO userDAO = new UserDAO();
    private int tempUserId;

    /**
     * Setup: Create a temporary Business User before the test runs.
     * This allows us to link loans to a real User ID.
     */
    @Before
    public void setUp() {
        // 1. Create unique user data
        String email = "loan_test_" + System.currentTimeMillis() + "@revpay.com";
        User tempUser = new User(email, "5550005555", "hashedpass", "1234", "Loan Tester", Role.BUSINESS);

        // 2. Register user
        userDAO.registerUser(tempUser);

        // 3. Get the ID for use in tests
        User saved = userDAO.getUserByEmail(email);
        if (saved != null) {
            this.tempUserId = saved.getUserId();
        } else {
            fail("Test Setup Failed: Could not create temporary user.");
        }
    }

    /**
     * Teardown: Delete the temporary user and their loans after the test.
     */
    @After
    public void tearDown() {
        if (tempUserId > 0) {
            userDAO.deleteUser(tempUserId);
        }
    }

    /**
     * Test Scenario: Apply for a loan and verify it appears in the user's history.
     */
    @Test
    public void testApplyAndRetrieveLoan() {
        // 1. Prepare Loan Data
        BigDecimal amount = new BigDecimal("15000.00");
        String reason = "Startup Capital";
        Loan newLoan = new Loan(tempUserId, amount, reason);

        // 2. Test Application Submission
        boolean applied = loanDAO.applyForLoan(newLoan);
        assertTrue("Loan application should be successful", applied);

        // 3. Test Retrieval
        List<Loan> loans = loanDAO.getLoansByUserId(tempUserId);
        assertFalse("Loan list should not be empty", loans.isEmpty());

        // 4. Verify Data Integrity
        Loan retrievedLoan = loans.get(0);
        assertEquals("User ID should match", tempUserId, retrievedLoan.getUserId());
        assertEquals("Amount should match", amount, retrievedLoan.getAmount());
        assertEquals("Reason should match", reason, retrievedLoan.getReason());
        assertEquals("Default status should be PENDING", LoanStatus.PENDING, retrievedLoan.getStatus());

        System.out.println("✅ Loan Application Test Passed");
    }
}