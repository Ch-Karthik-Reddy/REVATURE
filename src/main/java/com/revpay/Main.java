/**
 * --------------------------------------------------------------------
 * Main.java
 * --------------------------------------------------------------------
 *
 * Entry point of the RevPay – Secure Digital Banking System.
 *
 * Description:
 * This class acts as the central controller for the RevPay console-based
 * application. It manages user interaction, menu navigation, and routes
 * requests to appropriate service and DAO layers.
 *
 * Key Responsibilities:
 * - Application startup and shutdown handling
 * - User registration and authentication
 * - Role-based dashboard (Personal & Business users)
 * - Money transfers, deposits, and requests
 * - Card management and transaction history
 * - Business features such as invoices and loan management
 * - Secure logging using Log4j
 *
 * User Roles Supported:
 * - PERSONAL: Basic money transfer and wallet features
 * - BUSINESS: Invoices, loans, and advanced financial services
 *
 * Technologies Used:
 * - Java (Core + JDBC)
 * - MySQL
 * - Log4j 2
 * - DAO & Service Design Patterns
 *
 * Architecture:
 * - Presentation Layer  : Console UI (Main class)
 * - Service Layer       : Business logic handling
 * - DAO Layer           : Database access
 * - Utility Layer       : Security and helpers
 *
 * Logging:
 * - INFO  : Application lifecycle events
 * - ERROR : Runtime and processing issues
 * - FATAL : Critical application crashes
 *
 * Developed By:
 * Karthik
 *
 * Project:
 * RevPay – Secure Digital Banking System
 * --------------------------------------------------------------------
 */

package com.revpay;

import com.revpay.dao.InvoiceDAO;
import com.revpay.dao.LoanDAO;
import com.revpay.dao.PaymentMethodDAO;
import com.revpay.dao.RequestDAO;
import com.revpay.model.*;
import com.revpay.service.*;
import com.revpay.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(Main.class);

    // --- SERVICES & DAO'S ---
    private static UserService userService = new UserService();
    private static TransactionService transactionService = new TransactionService();
    private static LoanDAO loanDAO = new LoanDAO();
    private static RequestDAO requestDAO = new RequestDAO();
    private static PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();
    private static InvoiceDAO invoiceDAO = new InvoiceDAO();

    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        logger.info("  RevPay Application Started");
        System.out.println("\n=========================================");
        System.out.println("       Welcome to RevPay application     ");
        System.out.println("=========================================");

        try {
            while (true) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        handleRegister();
                        break;
                    case "2":
                        handleLogin();
                        break;
                    case "3":
                        logger.info(" Application Stopped by User");
                        System.out.println("Goodbye! Thank you for using RevPay.");
                        return; // Exit the app
                    default:
                        System.out.println(" Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            logger.fatal("  Critical Application Crash", e);
            System.out.println(" An unexpected error occurred. Please check logs.");
        }
    }

    private static void handleLogin() {
        System.out.println("\n---  LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userService.login(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println(" Login Successful! Welcome, " + user.getFullName());
            showUserDashboard();
        }
    }

    private static void handleRegister() {
        System.out.println("\n--- REGISTER ---");
        System.out.println("Select Account Type:\n1. Personal\n2. Business");
        System.out.print("Choice: ");
        String roleChoice = scanner.nextLine();
        Role role = roleChoice.equals("2") ? Role.BUSINESS : Role.PERSONAL;

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Password: ");
        String rawPassword = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter 4-digit PIN: ");
        String pin = scanner.nextLine();

        String hashedPassword = SecurityUtil.hashPassword(rawPassword);
        User newUser = new User(email, phone, hashedPassword, pin, name, role);

        if (userService.registerUser(newUser)) {
            System.out.println(" Registered Successfully! Please Login.");
        } else {
            System.out.println(" Registration Failed. Email might already exist.");
        }
    }

    private static void showUserDashboard() {
        while (currentUser != null) {
            System.out.println("\n===  " + currentUser.getFullName() + "'s Dashboard ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Add Money (Deposit)");
            System.out.println("3. Send Money");
            System.out.println("4. Request Money");
            System.out.println("5. Pending Requests & Invoices");
            System.out.println("6. Manage Cards");
            System.out.println("7. Transaction History");

            if (currentUser.getRole() == Role.BUSINESS) {
                System.out.println("--- Business Services ---");
                System.out.println("8. Create Invoice");
                System.out.println("9. Apply for Loan");
                System.out.println("10. View Loans");
                System.out.println("11. Delete Account");
                System.out.println("12. Logout");
            } else {
                System.out.println("-------------------------");
                System.out.println("8. Delete Account");
                System.out.println("9. Logout");
            }

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            try {
                processDashboardChoice(choice);
            } catch (Exception e) {
                logger.error("Error processing dashboard choice", e);
                System.out.println(" An error occurred processing your request.");
            }
        }
    }

    private static void processDashboardChoice(String choice) {
        switch (choice) {
            case "1":
                checkBalance();
                break;
            case "2":
                handleAddMoney();
                break;
            case "3":
                handleSendMoney();
                break;
            case "4":
                handleRequestMoney();
                break;
            case "5":
                handlePendingPayments();
                break;
            case "6":
                handleManageCards();
                break;
            case "7":
                handleViewHistory();
                break;

            // Business vs Personal Routing
            case "8":
                if (currentUser.getRole() == Role.BUSINESS)
                    handleCreateInvoice();
                else
                    handleDeleteAccount();
                break;
            case "9":
                if (currentUser.getRole() == Role.BUSINESS)
                    handleApplyLoan();
                else
                    logout();
                break;
            case "10":
                if (currentUser.getRole() == Role.BUSINESS)
                    handleViewLoans();
                else
                    System.out.println(" Invalid option.");
                break;
            case "11":
                if (currentUser.getRole() == Role.BUSINESS)
                    handleDeleteAccount();
                else
                    System.out.println(" Invalid option.");
                break;
            case "12":
                if (currentUser.getRole() == Role.BUSINESS)
                    logout();
                else
                    System.out.println(" Invalid option.");
                break;
            default:
                System.out.println(" Invalid option.");
        }
    }

    private static void checkBalance() {
        System.out.println("\n💰 Balance: ₹" + userService.getBalance(currentUser.getUserId()));
    }

    private static void handleAddMoney() {
        System.out.println("\n--- ➕ ADD MONEY ---");
        List<PaymentMethod> cards = paymentMethodDAO.getMethodsByUserId(currentUser.getUserId());
        if (cards.isEmpty()) {
            System.out.println("⚠ No saved cards. Go to 'Manage Cards' first.");
            return;
        }

        System.out.println("Select Payment Method:");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i));
        }
        System.out.print("Choice: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= cards.size()) {
                System.out.println(" Invalid selection.");
                return;
            }

            System.out.print("Enter Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.println("Charging card ending in " + cards.get(idx).getCardNumber().substring(12) + "...");
            if (transactionService.processDeposit(currentUser.getUserId(), amount)) {
                System.out.println(" Deposit Successful!");
            } else {
                System.out.println(" Deposit Failed.");
            }
        } catch (Exception e) {
            System.out.println(" Invalid input.");
        }
    }

    private static void handleSendMoney() {
        System.out.println("\n---  SEND MONEY ---");
        System.out.print("To Email: ");
        String email = scanner.nextLine();
        System.out.print("Amount: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            if (transactionService.processTransfer(currentUser.getUserId(), email, amount)) {
                System.out.println(" Sent Successfully!");
            } else {
                System.out.println(" Transfer Failed (Check Balance or Email).");
            }
        } catch (Exception e) {
            System.out.println(" Invalid amount format.");
        }
    }

    private static void handleRequestMoney() {
        System.out.println("\n---  REQUEST MONEY ---");
        System.out.print("Ask Email: ");
        String email = scanner.nextLine();

        int payerId = userService.getUserIdByEmail(email);
        if (payerId == -1 || payerId == currentUser.getUserId()) {
            System.out.println(" User not found or invalid.");
            return;
        }

        System.out.print("Amount: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            PaymentRequest req = new PaymentRequest(currentUser.getUserId(), payerId, amount);
            if (requestDAO.createRequest(req)) {
                System.out.println(" Request Sent!");
            } else {
                System.out.println(" Error sending request.");
            }
        } catch (Exception e) {
            System.out.println(" Invalid input.");
        }
    }

    private static void handlePendingPayments() {
        System.out.println("\n---  PENDING PAYMENTS ---");

        List<PaymentRequest> requests = requestDAO.getIncomingRequests(currentUser.getUserId());
        System.out.println("[Money Requests]");
        if (requests.isEmpty())
            System.out.println("  (None)");
        else
            for (PaymentRequest r : requests)
                System.out.println("  ID: " + r.getRequestId() + " | Amount: ₹" + r.getAmount());

        List<Invoice> invoices = invoiceDAO.getInvoicesForCustomer(currentUser.getEmail());
        System.out.println("\n[Invoices]");
        if (invoices.isEmpty())
            System.out.println("  (None)");
        else
            for (Invoice i : invoices)
                System.out.println("  INV#" + i.getInvoiceId() + " | " + i.getDescription() + " | ₹" + i.getAmount());

        System.out.println("\nTo Pay: Type 'P R [ID]' (Request) or 'P I [ID]' (Invoice). Type '0' to Back.");
        System.out.print("> ");
        String input = scanner.nextLine().toUpperCase();

        try {
            if (input.startsWith("P R ")) {
                int id = Integer.parseInt(input.substring(4));
                PaymentRequest r = requestDAO.getRequestById(id);
                if (r != null && transactionService.processTransfer(currentUser.getUserId(), r.getRequesterId(),
                        r.getAmount())) {
                    requestDAO.updateStatus(id, "ACCEPTED");
                    System.out.println(" Request Paid!");
                } else
                    System.out.println(" Payment Failed.");

            } else if (input.startsWith("P I ")) {
                int id = Integer.parseInt(input.substring(4));
                Invoice inv = invoiceDAO.getInvoiceById(id);
                if (inv != null && transactionService.processTransfer(currentUser.getUserId(), inv.getBusinessId(),
                        inv.getAmount())) {
                    invoiceDAO.markAsPaid(id);
                    System.out.println(" Invoice Paid!");
                } else
                    System.out.println(" Payment Failed.");
            }
        } catch (Exception e) {
            System.out.println(" Invalid Command.");
        }
    }

    private static void handleManageCards() {
        System.out.println("\n--- 💳 CARDS ---");
        System.out.println("1. View Cards");
        System.out.println("2. Add Card");
        System.out.print("Choice: ");

        if ("2".equals(scanner.nextLine())) {
            System.out.print("Card Number (16 digits): ");
            String num = scanner.nextLine();
            System.out.print("Type (CREDIT/DEBIT): ");
            String type = scanner.nextLine().toUpperCase();
            System.out.print("Expiry (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            try {
                PaymentMethod pm = new PaymentMethod(currentUser.getUserId(), num, type, Date.valueOf(date));
                paymentMethodDAO.addPaymentMethod(pm);
                System.out.println(" Card Added.");
            } catch (Exception e) {
                System.out.println(" Invalid Date format.");
            }
        } else {
            List<PaymentMethod> list = paymentMethodDAO.getMethodsByUserId(currentUser.getUserId());
            if (list.isEmpty())
                System.out.println("No cards found.");
            else
                list.forEach(System.out::println);
        }
    }

    private static void handleViewHistory() {
        System.out.println("\n---  HISTORY ---");
        List<Transaction> list = transactionService.getHistory(currentUser.getUserId());
        if (list.isEmpty())
            System.out.println("No transactions found.");
        else
            list.forEach(t -> {
                String sign = (t.getSenderId() == currentUser.getUserId()) ? "-" : "+";
                System.out.println(t.getType() + " | " + sign + "₹" + t.getAmount() + " | " + t.getTimestamp());
            });
    }

    private static void handleCreateInvoice() {
        System.out.println("\n---  NEW INVOICE ---");
        System.out.print("Customer Email: ");
        String email = scanner.nextLine();

        if (userService.getUserIdByEmail(email) == -1) {
            System.out.println(" Customer not found.");
            return;
        }

        System.out.print("Amount: ");
        try {
            BigDecimal amt = new BigDecimal(scanner.nextLine());
            System.out.print("Description: ");
            String desc = scanner.nextLine();

            if (invoiceDAO.createInvoice(new Invoice(currentUser.getUserId(), email, amt, desc))) {
                System.out.println(" Invoice Sent!");
            } else {
                System.out.println(" Failed to create invoice.");
            }
        } catch (Exception e) {
            System.out.println(" Invalid Amount.");
        }
    }

    private static void handleApplyLoan() {
        System.out.println("\n---  APPLY LOAN ---");
        System.out.print("Amount: ");
        try {
            BigDecimal amt = new BigDecimal(scanner.nextLine());
            System.out.print("Reason: ");
            String reason = scanner.nextLine();
            loanDAO.applyForLoan(new Loan(currentUser.getUserId(), amt, reason));
            System.out.println(" Application Submitted.");
        } catch (Exception e) {
            System.out.println(" Invalid Input.");
        }
    }

    private static void handleViewLoans() {
        System.out.println("\n---  LOANS ---");
        List<Loan> loans = loanDAO.getLoansByUserId(currentUser.getUserId());
        if (loans.isEmpty())
            System.out.println("No loans found.");
        else
            loans.forEach(System.out::println);
    }

    // ==========================================
    // SYSTEM
    // ==========================================

    private static void handleDeleteAccount() {
        System.out.println("\n⚠ DELETE ACCOUNT ⚠");
        System.out.print("Are you sure? Type 'yes' to confirm: ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            userService.deleteAccount(currentUser.getUserId());
            currentUser = null;
            System.out.println(" Account Deleted.");
        }
    }

    private static void logout() {
        System.out.println("Logging out...");
        currentUser = null;
    }
}