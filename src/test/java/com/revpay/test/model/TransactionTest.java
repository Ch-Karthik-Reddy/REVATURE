package com.revpay.test.model;

import com.revpay.model.Transaction;
import com.revpay.model.TransactionStatus;
import com.revpay.model.TransactionType;
import org.junit.Test;
import java.math.BigDecimal;
import java.sql.Timestamp;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Transaction} model class.
 * <p>
 * Verifies that the Transaction object correctly stores financial data
 * and associated Enums.
 * </p>
 */
public class TransactionTest {

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testTransactionConstructor() {
        int sender = 101;
        int receiver = 202;
        BigDecimal amount = new BigDecimal("500.00");
        TransactionType type = TransactionType.TRANSFER;
        TransactionStatus status = TransactionStatus.SUCCESS;

        Transaction t = new Transaction(sender, receiver, amount, type, status);

        assertEquals("Sender ID match", sender, t.getSenderId());
        assertEquals("Receiver ID match", receiver, t.getReceiverId());
        assertEquals("Amount match", amount, t.getAmount());
        assertEquals("Type match", type, t.getType());
        assertEquals("Status match", status, t.getStatus());
    }

    /**
     * Test Setters and Getters, including Timestamp.
     */
    @Test
    public void testSettersAndTimestamp() {
        Transaction t = new Transaction();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        t.setTransactionId(999);
        t.setTimestamp(now);
        t.setType(TransactionType.DEPOSIT);

        assertEquals(999, t.getTransactionId());
        assertEquals(now, t.getTimestamp());
        assertEquals(TransactionType.DEPOSIT, t.getType());
    }

    /**
     * Test toString format.
     */
    @Test
    public void testToString() {
        Transaction t = new Transaction(1, 2, new BigDecimal("10.00"), TransactionType.TRANSFER, TransactionStatus.FAILED);
        t.setTransactionId(55);
        
        String result = t.toString();
        
        assertTrue("Output contains ID", result.contains("ID=55"));
        assertTrue("Output contains Status", result.contains("Status=FAILED"));
    }
}