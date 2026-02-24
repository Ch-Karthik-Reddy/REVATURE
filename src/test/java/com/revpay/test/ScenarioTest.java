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

public class ScenarioTest {

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

        // Reset Main.scanner via reflection
        Field field = Main.class.getDeclaredField("scanner");
        field.setAccessible(true);
        field.set(null, new Scanner(testInput));
    }

    @Test
    public void testRegistrationPersonal() throws Exception {
        long id = System.currentTimeMillis();
        String email = "user" + id + "@personal.com";

        // 1 (Register) -> 1 (Personal) -> Email -> Name -> Pass -> Phone -> PIN -> 3
        // (Exit)
        String input = "1\n1\n" + email + "\nPersonal User\npass123\n9000000001\n1234\n3\n";

        prepareInput(input);
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Output should indicate success", output.contains("Registered Successfully"));
    }

    @Test
    public void testRegistrationBusiness() throws Exception {
        long id = System.currentTimeMillis();
        String email = "biz" + id + "@business.com";

        // 1 (Register) -> 2 (Business) -> Email -> Name -> Pass -> Phone -> PIN -> 3
        // (Exit)
        String input = "1\n2\n" + email + "\nBusiness Owner\nbizpass\n9000000002\n5678\n3\n";

        prepareInput(input);
        Main.main(new String[] {});

        String output = testOut.toString();
        assertTrue("Output should indicate success", output.contains("Registered Successfully"));
    }

    @Test
    public void testInvalidLogin() throws Exception {
        // 2 (Login) -> wrong@mail.com -> wrongpass -> 3 (Exit)
        String input = "2\nwrong@mail.com\nwrongpass\n3\n";

        prepareInput(input);
        Main.main(new String[] {});

        String output = testOut.toString();
        // Main.java: login returns null -> "Login Successful" is NOT printed.
        // It likely prints "Invalid option" or just loops back.
        // Actually Main logic:
        // switch("2") -> handleLogin() -> userService.login() -> if(null) nothing?
        // handleLogin prints "Login Successful" only if user != null.
        // If null, it just returns to menu.
        // Wait, handleLogin() doesn't print "Failed".
        // It simply exits integration if login fails? No, handleLogin void.
        // Code:
        // if (user != null) { ... }
        // (implied else: do nothing)

        // So output should NOT contain "Login Successful"
        assertTrue("Should not login", !output.contains("Login Successful"));
    }

    @Test
    public void testTransactionLifecycle() throws Exception {
        long id = System.currentTimeMillis();
        String senderEmail = "sender" + id + "@test.com";
        String receiverEmail = "receiver" + id + "@test.com";

        StringBuilder sb = new StringBuilder();

        // 1. Register Sender
        sb.append("1\n1\n").append(senderEmail).append("\nSender User\npass\n1111111111\n1234\n");
        // 2. Register Receiver
        sb.append("1\n1\n").append(receiverEmail).append("\nReceiver User\npass\n2222222222\n1234\n");

        // 3. Login Sender
        sb.append("2\n").append(senderEmail).append("\npass\n");
        // a. Add Card (needed for deposit)
        // 6 (Manage Cards) -> 2 (Add) -> Number -> Type -> Expiry
        sb.append("6\n2\n1234567812345678\nDEBIT\n2029-12-31\n");
        // b. Add Money
        // 2 (Add Money) -> 1 (Select Card 1) -> 500
        sb.append("2\n1\n500\n");
        // c. Send Money
        // 3 (Send Money) -> Receiver Email -> 200
        sb.append("3\n").append(receiverEmail).append("\n200\n");
        // d. Check Balance (Should be 300)
        sb.append("1\n");
        // e. Logout (9 for Personal)
        sb.append("9\n");

        // 4. Login Receiver
        sb.append("2\n").append(receiverEmail).append("\npass\n");
        // a. Check Balance (Should be 200)
        sb.append("1\n");
        // b. Logout
        sb.append("9\n");

        // 5. Exit App
        sb.append("3\n");

        prepareInput(sb.toString());
        Main.main(new String[] {});

        String output = testOut.toString();

        // Verify key events
        assertTrue("Sender registered", output.contains("Registered Successfully"));
        assertTrue("Card added", output.contains("Card Added"));
        assertTrue("Deposit success", output.contains("Deposit Successful"));
        assertTrue("Transfer success", output.contains("Sent Successfully"));

        // Verify Balances (finding precise strings might be tricky due to formatting,
        // looking for substrings)
        // Sender Balance: 300
        assertTrue("Sender balance should be 300", output.contains("Balance: \u20B9300.00")); // \u20B9 is Rupee symbol
        // Receiver Balance: 200
        assertTrue("Receiver balance should be 200", output.contains("Balance: \u20B9200.00"));
    }
}
