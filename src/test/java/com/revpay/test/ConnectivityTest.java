package com.revpay.test;

import com.revpay.config.DatabaseConnection;
import org.junit.Test;
import java.sql.Connection;
import static org.junit.Assert.assertNotNull;

public class ConnectivityTest {
    @Test
    public void testDatabaseConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull("Connection should not be null", conn);
            System.out.println("✅ Database Connection Successful!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
