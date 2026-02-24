package com.revpay.service;

import com.revpay.dao.TransactionDAO;
import com.revpay.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for handling Financial Business Logic.
 * <p>
 * This layer sits between the Controller (Menu) and the DAO (Database).
 * It enforces business rules such as:
 * <ul>
 * <li>Transfers must be positive amounts.</li>
 * <li>Users cannot transfer money to themselves.</li>
 * <li>Receiver emails must exist in the system.</li>
 * </ul>
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class TransactionService {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    private TransactionDAO transactionDAO = new TransactionDAO();
    private UserService userService = new UserService();

    /**
     * Processes a money transfer using the receiver's email address.
     * <p>
     * This method resolves the email to a User ID and delegates to the ID-based
     * transfer method.
     * </p>
     *
     * @param senderId      The ID of the user sending money.
     * @param receiverEmail The email address of the recipient.
     * @param amount        The amount to transfer.
     * @return {@code true} if successful, {@code false} if email not found or
     *         validation fails.
     */
    public boolean processTransfer(int senderId, String receiverEmail, BigDecimal amount) {
        // 1. Get Receiver ID from Email
        int receiverId = userService.getUserIdByEmail(receiverEmail);

        if (receiverId == -1) {
            logger.warn("Transfer Failed: Receiver email not found (" + receiverEmail + ")");
            System.out.println("❌ Receiver email not found.");
            return false;
        }

        // 2. Delegate to the main ID-based method
        return processTransfer(senderId, receiverId, amount);
    }

    /**
     * Processes a money transfer using User IDs.
     * <p>
     * Enforces validation rules (Positive amount, No self-transfer) before
     * contacting the DAO.
     * </p>
     *
     * @param senderId   The ID of the user sending money.
     * @param receiverId The ID of the user receiving money.
     * @param amount     The amount to transfer.
     * @return {@code true} if successful, {@code false} if validation or DB
     *         operation fails.
     */

    public boolean processTransfer(int senderId, int receiverId, BigDecimal amount) {
        // 1. Validation: Positive Amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid Transfer: User " + senderId + " attempted to send non-positive amount: " + amount);
            System.out.println("❌ Amount must be greater than 0.");
            return false;
        }

        // 2. Validation: Prevent sending to self
        if (senderId == receiverId) {
            logger.warn("Invalid Transfer: User " + senderId + " attempted to send money to themselves.");
            System.out.println("❌ You cannot send money to yourself.");
            return false;
        }

        // 3. Execute Transaction via DAO
        logger.info("Initiating Transfer: " + senderId + " -> " + receiverId + " ($" + amount + ")");
        return transactionDAO.transferMoney(senderId, receiverId, amount);
    }

    /**
     * Processes a deposit into a user's wallet.
     *
     * @param userId The ID of the user.
     * @param amount The amount to deposit (must be positive).
     * @return {@code true} if successful.
     */
    public boolean processDeposit(int userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid Deposit: User " + userId + " attempted to deposit non-positive amount: " + amount);
            System.out.println("❌ Deposit amount must be positive.");
            return false;
        }
        return transactionDAO.depositMoney(userId, amount);
    }

    /**
     * Retrieves the transaction history for a user.
     *
     * @param userId The ID of the user.
     * @return List of past transactions.
     */
    public List<Transaction> getHistory(int userId) {
        return transactionDAO.getTransactionHistory(userId);
    }
}