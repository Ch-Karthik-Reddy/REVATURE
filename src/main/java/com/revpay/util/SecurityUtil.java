package com.revpay.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for handling Security and Encryption.
 * <p>
 * This class uses the <b>BCrypt</b> algorithm to securely hash and verify user passwords.
 * BCrypt is an industry-standard function that incorporates "salting" and "key stretching"
 * to protect against rainbow table attacks and brute force attempts.
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class SecurityUtil {

    /**
     * Hashes a plain-text password using BCrypt.
     * <p>
     * This method generates a secure, random salt and combines it with the password
     * to create a non-reversible hash string.
     * </p>
     *
     * @param plainPassword The raw password entered by the user.
     * @return A secure BCrypt hash string (e.g., "$2a$12$...").
     */
    public static String hashPassword(String plainPassword) {
        // Cost factor 12 is a good balance between security and performance (takes ~200-300ms)
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }


    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword  The raw password entered during login.
     * @param hashedPassword The hash stored in the database.
     * @return {@code true} if the password matches the hash, {@code false} otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || plainPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}