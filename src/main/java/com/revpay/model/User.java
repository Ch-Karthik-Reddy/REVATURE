package com.revpay.model;

/**
 * Model class representing a registered User in the RevPay system.
 * <p>
 * This class stores the core identity information, including authentication credentials
 * (hashed), contact details, and the account role (Personal vs Business).
 * </p>
 *
 * @author RevPay Dev Team
 * @version 1.0
 */
public class User {
    private int userId;
    private String email;
    private String phoneNumber;
    private String passwordHash; // Stored as BCrypt hash, never plain text
    private String transactionPin;
    private String fullName;
    private Role role; // PERSONAL or BUSINESS

    /**
     * Default constructor.
     * Required for various frameworks and database mapping tools.
     */
    public User() {}

    /**
     * Parameterized constructor for registering a new user.
     *
     * @param email          The user's email address (unique identifier).
     * @param phoneNumber    The user's contact number.
     * @param passwordHash   The securely hashed password.
     * @param transactionPin The 4-digit PIN for authorizing transactions.
     * @param fullName       The user's legal full name.
     * @param role           The account type ({@link Role#PERSONAL} or {@link Role#BUSINESS}).
     */
    public User(String email, String phoneNumber, String passwordHash, String transactionPin, String fullName, Role role) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.transactionPin = transactionPin;
        this.fullName = fullName;
        this.role = role;
    }

    // --- Getters and Setters ---

    /**
     * Gets the unique User ID.
     * @return The user ID.
     */
    public int getUserId() { return userId; }

    /**
     * Sets the unique User ID.
     * @param userId The user ID.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the user's email.
     * @return The email address.
     */
    public String getEmail() { return email; }

    /**
     * Sets the user's email.
     * @param email The email address.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the user's phone number.
     * @return The phone number.
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Sets the user's phone number.
     * @param phoneNumber The phone number.
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * Gets the hashed password.
     * @return The password hash string.
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets the hashed password.
     * @param passwordHash The password hash string.
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Gets the transaction PIN.
     * @return The PIN string.
     */
    public String getTransactionPin() { return transactionPin; }

    /**
     * Sets the transaction PIN.
     * @param transactionPin The PIN string.
     */
    public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }

    /**
     * Gets the user's full name.
     * @return The full name.
     */
    public String getFullName() { return fullName; }

    /**
     * Sets the user's full name.
     * @param fullName The full name.
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Gets the user's role.
     * @return The {@link Role} enum.
     */
    public Role getRole() { return role; }

    /**
     * Sets the user's role.
     * @param role The {@link Role} enum.
     */
    public void setRole(Role role) { this.role = role; }

    /**
     * Returns a string representation of the User.
     * <p>
     * <b>Security Note:</b> Passwords and PINs are intentionally omitted
     * from this output to prevent logging sensitive data.
     * </p>
     *
     * @return A safe string representation.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + userId +
                ", email='" + email + '\'' +
                ", name='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}