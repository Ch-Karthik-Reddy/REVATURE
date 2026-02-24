/**
 * --------------------------------------------------------------------
 * DatabaseConnectionTest
 * --------------------------------------------------------------------
 *
 * This JUnit test class is responsible for validating the database
 * connectivity configuration used in the RevPay application.
 *
 * Purpose:
 * - Ensures that a valid Oracle database connection can be established.
 * - Verifies that the connection object returned is not null.
 * - Confirms that the database connection is open and active.
 * - Helps detect configuration or credential issues early during testing.
 *
 * Technologies Used:
 * - Java
 * - JDBC
 * - Oracle Database
 * - JUnit 4
 *
 * Tested Component:
 * - {@link com.revpay.config.DatabaseConnection}
 *
 * Expected Outcome:
 * - The test passes if the Oracle database connection is successfully created
 *   and remains open.
 * - The test fails if the connection is null, closed, or if any
 *   SQLException occurs.
 *
 * Usage:
 * - Run this test before application startup or deployment.
 * - Useful for CI/CD pipelines and interview demonstrations.
 *
 * Developed By:
 * Karthik
 *
 * Project:
 * RevPay – Secure Digital Banking System
 * --------------------------------------------------------------------
 */
package com.revpay.test.config; // Ensure this matches your folder structure

import org.junit.Test;
import java.sql.Connection;
import java.sql.SQLException;

// 1. Explicit Import (Fixes the resolution error)
import com.revpay.config.DatabaseConnection;

import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    @Test
    public void testGetConnection() {
        // 1. Attempt to get connection
        Connection conn = DatabaseConnection.getConnection();

        // 2. Assertions
        assertNotNull("Connection object should not be null. Check DB credentials.", conn);

        try {
            assertFalse("Connection should be open (not closed)", conn.isClosed());
            System.out.println("✅ Database Connection Test Passed!");

            // Clean up
            conn.close();
        } catch (SQLException e) {
            fail("SQLException occurred during test: " + e.getMessage());
        }
    }
}