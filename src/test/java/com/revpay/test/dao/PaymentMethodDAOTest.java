package com.revpay.test.dao;

import com.revpay.dao.PaymentMethodDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.PaymentMethod;
import com.revpay.model.Role;
import com.revpay.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link com.revpay.dao.PaymentMethodDAO}.
 * <p>
 * Verifies that Credit/Debit cards can be linked to a User account
 * and retrieved successfully.
 * </p>
 */
public class PaymentMethodDAOTest {

    private PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();
    private UserDAO userDAO = new UserDAO();
    private int tempUserId;

    /**
     * Setup: Create a temporary User.
     * Database constraint requires a valid user_id to store a card.
     */
    @Before
    public void setUp() {
        String email = "card_test_" + System.currentTimeMillis() + "@revpay.com";
        User user = new User(email, "1112223333", "hashedpass", "1234", "Card Tester", Role.PERSONAL);
        
        userDAO.registerUser(user);
        
        User saved = userDAO.getUserByEmail(email);
        if (saved != null) {
            this.tempUserId = saved.getUserId();
        } else {
            fail("Setup Failed: Could not create temporary user.");
        }
    }

    /**
     * Teardown: Delete the user (and cascading cards).
     */
    @After
    public void tearDown() {
        if (tempUserId > 0) {
            userDAO.deleteUser(tempUserId);
        }
    }

    /**
     * Test Scenario: Add a card and verify it appears in the user's wallet.
     */
    @Test
    public void testAddAndRetrieveCard() {
        // 1. Prepare Card Data
        String cardNumber = "1234567812345678";
        String cardType = "CREDIT";
        Date expiryDate = Date.valueOf("2028-12-31");
        
        PaymentMethod pm = new PaymentMethod(tempUserId, cardNumber, cardType, expiryDate);

        // 2. Test Adding Card
        boolean added = paymentMethodDAO.addPaymentMethod(pm);
        assertTrue("Card should be added successfully", added);

        // 3. Test Retrieval
        List<PaymentMethod> methods = paymentMethodDAO.getMethodsByUserId(tempUserId);
        assertFalse("User should have at least one payment method", methods.isEmpty());

        // 4. Verify Data
        PaymentMethod retrieved = methods.get(0);
        assertEquals("Card number should match", cardNumber, retrieved.getCardNumber());
        assertEquals("Card type should match", cardType, retrieved.getCardType());
        // Note: Dates might compare differently depending on TimeZone settings, converting to string is safer
        assertEquals("Expiry date should match", expiryDate.toString(), retrieved.getExpiryDate().toString());

        System.out.println("✅ Payment Method Test Passed");
    }
}