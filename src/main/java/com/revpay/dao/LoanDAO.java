package com.revpay.dao;

import com.revpay.config.DatabaseConnection;
import com.revpay.model.Loan;
import com.revpay.model.LoanStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LoanDAO {

    // Initialize Log4j Logger
    private static final Logger logger = LogManager.getLogger(LoanDAO.class);


    public boolean applyForLoan(Loan loan) {
        String sql = "INSERT INTO loans (user_id, amount, reason, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getUserId());
            stmt.setBigDecimal(2, loan.getAmount());
            stmt.setString(3, loan.getReason());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("  Loan Application Submitted: User ID " + loan.getUserId() + " requested $" + loan.getAmount());
                return true;
            }
        } catch (SQLException e) {
            logger.error("  Failed to submit loan application for User ID " + loan.getUserId(), e);
        }
        return false;
    }


    public List<Loan> getLoansByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setAmount(rs.getBigDecimal("amount"));
                loan.setReason(rs.getString("reason"));

                // Safely parse enum string
                try {
                    loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
                } catch (IllegalArgumentException e) {
                    logger.warn("Unknown loan status found in DB: " + rs.getString("status"));
                }

                loan.setAppliedAt(rs.getTimestamp("applied_at"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            logger.error("  Error retrieving loans for User ID " + userId, e);
        }
        return loans;
    }
}