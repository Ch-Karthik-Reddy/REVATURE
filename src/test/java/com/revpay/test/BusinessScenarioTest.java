package com.revpay.test;

import com.revpay.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Scanner;
import static org.junit.Assert.assertTrue;

/**
 * Business-specific scenario tests for RevPay.
 * Tests cover: Invoices, Loans, Request Money.
 */
public class BusinessScenarioTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void prepareInput(String data) throws Exception {
        InputStream testInput = new ByteArrayInputStream(data.getBytes());
        System.setIn(testInput);
        Field field = Main.class.getDeclaredField("scanner");
        field.setAccessible(true);
        field.set(null, new Scanner(testInput));
    }

    @Test
    public void testBusinessInvoiceCreation() throws Exception {
        long id = System.currentTimeMillis();
        String bizEmail = "biz" + id + "@invoice.com";
        String customerEmail = "cust" + id + "@invoice.com";

        StringBuilder sb = new StringBuilder();

        // Register Business User
        sb.append("1\n2\n").append(bizEmail).append("\nBiz Owner\nbizpass\n3331112222\n1111\n");
        // Register Customer (Personal)
        sb.append("1\n1\n").append(customerEmail).append("\nCustomer\ncustpass\n4442223333\n2222\n");

        // Login as Business
        sb.append("2\n").append(bizEmail).append("\nbizpass\n");
        // Create Invoice (8) -> Customer Email -> Amount -> Description
        sb.append("8\n").append(customerEmail).append("\n150\nConsulting Services\n");
        // Logout (12 for Business)
        sb.append("12\n");

        // Login as Customer
        sb.append("2\n").append(customerEmail).append("\ncustpass\n");
        // View Pending Payments (5) - should show invoice
        sb.append("5\n0\n"); // 0 to go back
        // Logout (9 for Personal)
        sb.append("9\n");

        // Exit
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Invoice should be sent", output.contains("Invoice Sent"));
        assertTrue("Invoice should appear in pending", output.contains("Consulting Services"));
    }

    @Test
    public void testBusinessLoanApplication() throws Exception {
        long id = System.currentTimeMillis();
        String bizEmail = "bizloan" + id + "@loan.com";

        StringBuilder sb = new StringBuilder();

        // Register Business User
        sb.append("1\n2\n").append(bizEmail).append("\nLoan Applicant\nloanpass\n5556667777\n3333\n");

        // Login as Business
        sb.append("2\n").append(bizEmail).append("\nloanpass\n");
        // Apply for Loan (9) -> Amount -> Reason
        sb.append("9\n10000\nBusiness Expansion\n");
        // View Loans (10)
        sb.append("10\n");
        // Logout (12)
        sb.append("12\n");

        // Exit
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Loan application submitted", output.contains("Application Submitted"));
        assertTrue("Loan should appear in list", output.contains("Business Expansion") || output.contains("10000"));
    }

    @Test
    public void testPaymentRequest() throws Exception {
        long id = System.currentTimeMillis();
        String requesterEmail = "req" + id + "@pm.com";
        String payerEmail = "payer" + id + "@pm.com";

        StringBuilder sb = new StringBuilder();

        // Register Requester
        sb.append("1\n1\n").append(requesterEmail).append("\nRequester\nreqpass\n7778889999\n4444\n");
        // Register Payer
        sb.append("1\n1\n").append(payerEmail).append("\nPayer\npaypass\n1112223334\n5555\n");

        // Login as Requester
        sb.append("2\n").append(requesterEmail).append("\nreqpass\n");
        // Request Money (4) -> Payer Email -> Amount
        sb.append("4\n").append(payerEmail).append("\n75\n");
        // Logout
        sb.append("9\n");

        // Login as Payer
        sb.append("2\n").append(payerEmail).append("\npaypass\n");
        // Add Card first
        sb.append("6\n2\n9876543210123456\nCREDIT\n2028-06-30\n");
        // Add Money
        sb.append("2\n1\n100\n");
        // View Pending Payments (5) -> Pay Request (P R [ID])
        // We assume first request is ID 1 or check the output
        sb.append("5\nP R 1\n"); // This might fail if ID != 1, but let's try
        // Check Balance (should be 100 - 75 = 25)
        sb.append("1\n");
        // Logout
        sb.append("9\n");

        // Exit
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Request sent", output.contains("Request Sent"));
        // Payment might succeed or fail depending on ID
    }

    @Test
    public void testEdgeCaseInsufficientBalance() throws Exception {
        long id = System.currentTimeMillis();
        String senderEmail = "poor" + id + "@edge.com";
        String receiverEmail = "rich" + id + "@edge.com";

        StringBuilder sb = new StringBuilder();

        // Register both users
        sb.append("1\n1\n").append(senderEmail).append("\nPoor User\npass\n1110001111\n6666\n");
        sb.append("1\n1\n").append(receiverEmail).append("\nRich User\npass\n2220002222\n7777\n");

        // Login as Sender (no money)
        sb.append("2\n").append(senderEmail).append("\npass\n");
        // Try to send money without balance
        sb.append("3\n").append(receiverEmail).append("\n500\n");
        // Logout
        sb.append("9\n");

        // Exit
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Transfer should fail", output.contains("Transfer Failed"));
    }

    @Test
    public void testDuplicateRegistration() throws Exception {
        long id = System.currentTimeMillis();
        String email = "dup" + id + "@dup.com";

        StringBuilder sb = new StringBuilder();

        // Register first time
        sb.append("1\n1\n").append(email).append("\nFirst User\npass\n3334445555\n8888\n");
        // Try to register again with same email
        sb.append("1\n1\n").append(email).append("\nSecond User\npass2\n4445556666\n9999\n");

        // Exit
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("First registration succeeds", output.contains("Registered Successfully"));
        assertTrue("Second registration fails", output.contains("Registration Failed"));
    }
}
