/**
 * --------------------------------------------------------------------
 * DatabaseConnection.java
 * --------------------------------------------------------------------
 *
 * This class is responsible for establishing and managing the
 * database connection for the RevPay application.
 *
 * Description:
 * - Provides a centralized method to obtain a JDBC connection.
 * - Uses Oracle Database as the backend database.
 * - Ensures proper logging of critical failures using Log4j.
 *
 * Responsibilities:
 * - Load the Oracle JDBC driver.
 * - Establish a connection using configured credentials.
 * - Handle and log SQL and driver-related exceptions.
 *
 * Design Pattern:
 * - Utility / Singleton-style class (static method usage)
 *
 * Technologies Used:
 * - Java JDBC
 * - Oracle JDBC Driver (ojdbc11)
 * - Log4j 2
 *
 * Security Note:
 * - Database credentials are hardcoded ONLY for development/testing.
 * - In production, credentials should be fetched from:
 *   - Environment variables
 *   - Configuration server
 *   - Secret vault (AWS Secrets Manager, HashiCorp Vault, etc.)
 *
 * Logging Strategy:
 * - FATAL: Missing JDBC driver or database connection failure
 *
 * Developed By:
 * Karthik
 *
 * Project:
 * RevPay – Secure Digital Banking System
 * --------------------------------------------------------------------
 */
package com.revpay.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    // Oracle Database Configuration Constants
    // Format: jdbc:oracle:thin:@<host>:<port>:<SID> or
    // jdbc:oracle:thin:@<host>:<port>/<service_name>
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "system";
    // ⚠️ SECURITY NOTE: In a production environment, fetch this from an environment
    // variable or secret vault.
    private static final String PASSWORD = "123456789";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the Oracle JDBC Driver
            Class.forName("oracle.jdbc.OracleDriver");

            // Attempt to connect
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            // Fatal error: Missing dependency indicates a broken build
            logger.fatal(" Oracle JDBC Driver not found. Ensure the dependency is in pom.xml.", e);
        } catch (SQLException e) {
            // Fatal error: Database connectivity is required for the app to function
            logger.fatal(" Database Connection Failed. Verify URL, User, and Password.", e);
        }
        return connection;
    }
}