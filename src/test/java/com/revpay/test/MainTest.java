package com.revpay.test;

import com.revpay.Main;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Integration test for the {@link Main} entry point.
 * <p>
 * This test verifies that the application starts and exits correctly
 * by simulating User Input via the System.in stream.
 * </p>
 */
public class MainTest {

    /**
     * Test Scenario: Start App -> Type "3" (Exit) -> Application Stops.
     * This ensures the main loop and menu system are functioning.
     */
    @Test
    public void testAppStartupAndExit() {
        // 1. Prepare simulated user input: "3" followed by Enter key
        String simulatedInput = "3\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        
        // 2. Backup the original System.in
        InputStream originalSystemIn = System.in;

        try {
            // 3. Inject our simulated input
            System.setIn(in);

            // 4. Run the Main Application
            // It should print the menu, read "3", and return.
            Main.main(new String[]{});

            // If we reach this line without hanging or crashing, the test passes.
            System.out.println("✅ Main Application Start/Stop Test Passed");

        } catch (Exception e) {
            throw new RuntimeException("Main app crashed during test", e);
        } finally {
            // 5. Restore original System.in (Crucial!)
            System.setIn(originalSystemIn);
        }
    }
}