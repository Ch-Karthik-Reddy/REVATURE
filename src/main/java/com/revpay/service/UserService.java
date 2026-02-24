package com.revpay.service;

import com.revpay.dao.UserDAO;
import com.revpay.dao.WalletDAO;
import com.revpay.model.User;
import com.revpay.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * Service class for managing User Accounts.
 * <p>
 * This class handles high-level user operations such as Authentication (Login),
 * Registration (including Wallet creation), and Account Deletion.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class UserService {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private UserDAO userDAO = new UserDAO();
    private WalletDAO walletDAO = new WalletDAO();

    /**
     * Authenticates a user based on email and password.
     *
     * @param email    The user's email address.
     * @param password The raw password (to be hashed and verified).
     * @return The {@link User} object if authentication succeeds, {@code null} otherwise.
     */
    public User login(String email, String password) {
        // 1. Get the user from the DB
        User user = userDAO.getUserByEmail(email);

        // 2. Check if user exists
        if (user == null) {
            logger.warn("Login Failed: Account not found for email " + email);
            System.out.println(" Account not found.");
            return null;
        }

        // 3. Verify the password using SecurityUtil (BCrypt)
        if (SecurityUtil.verifyPassword(password, user.getPasswordHash())) {
            logger.info(" User Logged In: " + email);
            return user;
        } else {
            logger.warn("Login Failed: Invalid password for email " + email);
            System.out.println(" Wrong password.");
            return null;
        }
    }

    /**
     * Registers a new user and automatically creates their Digital Wallet.
     *
     * @param user The {@link User} object containing registration details.
     * @return {@code true} if registration and wallet creation are successful.
     */
    public boolean registerUser(User user) {
        // 1. Register the User in the DB
        boolean isRegistered = userDAO.registerUser(user);

        // 2. If successful, find their new ID and create a Wallet
        if (isRegistered) {
            User savedUser = userDAO.getUserByEmail(user.getEmail());
            if (savedUser != null) {
                walletDAO.createWallet(savedUser.getUserId());
                logger.info(" Registration Complete: Wallet created for " + user.getEmail());
            } else {
                logger.error(" Registration Error: Could not fetch new user ID for wallet creation.");
                return false;
            }
        }
        return isRegistered;
    }

    /**
     * Retrieves the wallet balance for a specific user.
     *
     * @param userId The unique User ID.
     * @return The current balance.
     */
    public BigDecimal getBalance(int userId) {
        return walletDAO.getBalance(userId);
    }

    /**
     * Deletes a user account and all associated data.
     *
     * @param userId The unique User ID.
     * @return {@code true} if deletion was successful.
     */
    public boolean deleteAccount(int userId) {
        logger.info("🗑️ Deleting account for User ID: " + userId);
        return userDAO.deleteUser(userId);
    }

    /**
     * Helper method to resolve an Email Address to a User ID.
     *
     * @param email The email to search for.
     * @return The User ID if found, or -1 if not found.
     */
    public int getUserIdByEmail(String email) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            return user.getUserId();
        }
        logger.warn("User lookup failed: " + email);
        return -1;
    }
}