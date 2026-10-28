package com.accounting.dao;

import com.accounting.model.AccountType;
import com.accounting.model.BalanceSheet;
import com.accounting.model.BalanceSheetItem;
import com.accounting.model.IncomeStatement;
import com.accounting.model.IncomeStatementItem;
import com.accounting.model.TrialBalance;
import com.accounting.model.TrialBalanceItem;
import com.accounting.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for generating core accounting and financial reports.
 */
public class ReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    /**
     * Generates a Trial Balance as of a specified cut-off date.
     *
     * @param asOfDate optional cut-off date filter
     * @return populated TrialBalance
     */
    public TrialBalance generateTrialBalance(String asOfDate) {
        TrialBalance tb = new TrialBalance();
        tb.setAsOfDate(asOfDate);

        StringBuilder sql = new StringBuilder(
            "SELECT coa.account_code, coa.account_name, coa.account_type, " +
            "       COALESCE(SUM(jel.debit), 0) AS total_debit, " +
            "       COALESCE(SUM(jel.credit), 0) AS total_credit " +
            "FROM chart_of_accounts coa " +
            "LEFT JOIN journal_entry_lines jel ON coa.id = jel.account_id " +
            "LEFT JOIN journal_entries je ON jel.journal_entry_id = je.id "
        );

        List<Object> params = new ArrayList<>();
        if (asOfDate != null && !asOfDate.trim().isEmpty()) {
            sql.append("AND je.entry_date <= ? ");
            params.add(Date.valueOf(asOfDate.trim()));
        }

        sql.append("WHERE coa.is_active = TRUE ");
        sql.append("GROUP BY coa.id, coa.account_code, coa.account_name, coa.account_type ");
        sql.append("HAVING total_debit > 0 OR total_credit > 0 ");
        sql.append("ORDER BY coa.account_code ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("account_code");
                    String name = rs.getString("account_name");
                    AccountType type = AccountType.fromString(rs.getString("account_type"));
                    BigDecimal debit = rs.getBigDecimal("total_debit");
                    BigDecimal credit = rs.getBigDecimal("total_credit");

                    BigDecimal debitBalance = BigDecimal.ZERO;
                    BigDecimal creditBalance = BigDecimal.ZERO;

                    if (type.isNormalDebit()) {
                        BigDecimal net = debit.subtract(credit);
                        if (net.compareTo(BigDecimal.ZERO) >= 0) {
                            debitBalance = net;
                        } else {
                            creditBalance = net.abs();
                        }
                    } else {
                        BigDecimal net = credit.subtract(debit);
                        if (net.compareTo(BigDecimal.ZERO) >= 0) {
                            creditBalance = net;
                        } else {
                            debitBalance = net.abs();
                        }
                    }

                    tb.addItem(new TrialBalanceItem(code, name, type, debitBalance, creditBalance));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating Trial Balance", e);
        }

        return tb;
    }

    /**
     * Generates an Income Statement (Profit & Loss) for the given date range.
     *
     * @param startDate start date (inclusive)
     * @param endDate   end date (inclusive)
     * @return populated IncomeStatement
     */
    public IncomeStatement generateIncomeStatement(String startDate, String endDate) {
        IncomeStatement stmt = new IncomeStatement();
        stmt.setStartDate(startDate);
        stmt.setEndDate(endDate);

        StringBuilder sql = new StringBuilder(
            "SELECT coa.account_code, coa.account_name, coa.account_type, " +
            "       COALESCE(SUM(jel.debit), 0) AS total_debit, " +
            "       COALESCE(SUM(jel.credit), 0) AS total_credit " +
            "FROM chart_of_accounts coa " +
            "JOIN journal_entry_lines jel ON coa.id = jel.account_id " +
            "JOIN journal_entries je ON jel.journal_entry_id = je.id " +
            "WHERE coa.account_type IN ('REVENUE', 'EXPENSE') "
        );

        List<Object> params = new ArrayList<>();
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND je.entry_date >= ? ");
            params.add(Date.valueOf(startDate.trim()));
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND je.entry_date <= ? ");
            params.add(Date.valueOf(endDate.trim()));
        }

        sql.append("GROUP BY coa.id, coa.account_code, coa.account_name, coa.account_type ");
        sql.append("ORDER BY coa.account_code ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("account_code");
                    String name = rs.getString("account_name");
                    AccountType type = AccountType.fromString(rs.getString("account_type"));
                    BigDecimal debit = rs.getBigDecimal("total_debit");
                    BigDecimal credit = rs.getBigDecimal("total_credit");

                    if (type == AccountType.REVENUE) {
                        BigDecimal netRevenue = credit.subtract(debit);
                        stmt.addRevenue(new IncomeStatementItem(code, name, netRevenue));
                    } else if (type == AccountType.EXPENSE) {
                        BigDecimal netExpense = debit.subtract(credit);
                        stmt.addExpense(new IncomeStatementItem(code, name, netExpense));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating Income Statement", e);
        }

        return stmt;
    }

    /**
     * Generates a Balance Sheet as of a specified date, verifying: Assets = Liabilities + Equity
     *
     * @param asOfDate optional cut-off date filter
     * @return populated BalanceSheet
     */
    public BalanceSheet generateBalanceSheet(String asOfDate) {
        BalanceSheet sheet = new BalanceSheet();
        sheet.setAsOfDate(asOfDate);

        // 1. Fetch Assets, Liabilities, and Equity
        StringBuilder sql = new StringBuilder(
            "SELECT coa.account_code, coa.account_name, coa.account_type, " +
            "       COALESCE(SUM(jel.debit), 0) AS total_debit, " +
            "       COALESCE(SUM(jel.credit), 0) AS total_credit " +
            "FROM chart_of_accounts coa " +
            "JOIN journal_entry_lines jel ON coa.id = jel.account_id " +
            "JOIN journal_entries je ON jel.journal_entry_id = je.id "
        );

        List<Object> params = new ArrayList<>();
        if (asOfDate != null && !asOfDate.trim().isEmpty()) {
            sql.append("AND je.entry_date <= ? ");
            params.add(Date.valueOf(asOfDate.trim()));
        }

        sql.append("WHERE coa.account_type IN ('ASSET', 'LIABILITY', 'EQUITY') ");
        sql.append("GROUP BY coa.id, coa.account_code, coa.account_name, coa.account_type ");
        sql.append("ORDER BY coa.account_code ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("account_code");
                    String name = rs.getString("account_name");
                    AccountType type = AccountType.fromString(rs.getString("account_type"));
                    BigDecimal debit = rs.getBigDecimal("total_debit");
                    BigDecimal credit = rs.getBigDecimal("total_credit");

                    BigDecimal balance = type.calculateBalance(debit, credit);

                    if (type == AccountType.ASSET) {
                        sheet.addAsset(new BalanceSheetItem(code, name, balance));
                    } else if (type == AccountType.LIABILITY) {
                        sheet.addLiability(new BalanceSheetItem(code, name, balance));
                    } else if (type == AccountType.EQUITY) {
                        sheet.addEquityItem(new BalanceSheetItem(code, name, balance));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating Balance Sheet items", e);
        }

        // 2. Calculate Current Net Income up to the cut-off date to integrate into Equity
        IncomeStatement is = generateIncomeStatement(null, asOfDate);
        sheet.setCurrentNetIncome(is.getNetIncome());

        return sheet;
    }
}
