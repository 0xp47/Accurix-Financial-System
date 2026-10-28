package com.accounting.controller;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.JournalEntryDAO;
import com.accounting.dao.ReportDAO;
import com.accounting.model.Account;
import com.accounting.model.IncomeStatement;
import com.accounting.model.JournalEntry;
import com.accounting.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for main system dashboard, aggregating real-time metrics from MySQL.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private final JournalEntryDAO journalEntryDAO = new JournalEntryDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalEntries = 0;
        int totalAccounts = 0;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        // 1. Fetch counts & cumulative totals directly from MySQL
        String statsSql = 
            "SELECT " +
            "  (SELECT COUNT(*) FROM journal_entries) AS total_entries, " +
            "  (SELECT COUNT(*) FROM chart_of_accounts WHERE is_active = TRUE) AS total_accounts, " +
            "  (SELECT COALESCE(SUM(debit), 0) FROM journal_entry_lines) AS total_debit, " +
            "  (SELECT COALESCE(SUM(credit), 0) FROM journal_entry_lines) AS total_credit";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(statsSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalEntries = rs.getInt("total_entries");
                totalAccounts = rs.getInt("total_accounts");
                totalDebit = rs.getBigDecimal("total_debit");
                totalCredit = rs.getBigDecimal("total_credit");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching dashboard statistics", e);
        }

        // 2. Fetch Net Operating Income
        IncomeStatement statement = reportDAO.generateIncomeStatement(null, null);
        BigDecimal netIncome = (statement != null) ? statement.getNetIncome() : BigDecimal.ZERO;

        // 3. Fetch Recent Journal Entries (Top 5)
        List<JournalEntry> allEntries = journalEntryDAO.findAll(null, null, null);
        List<JournalEntry> recentEntries = (allEntries.size() > 5) ? allEntries.subList(0, 5) : allEntries;

        req.setAttribute("totalEntries", totalEntries);
        req.setAttribute("totalAccounts", totalAccounts);
        req.setAttribute("totalDebit", totalDebit);
        req.setAttribute("totalCredit", totalCredit);
        req.setAttribute("netIncome", netIncome);
        req.setAttribute("isNetProfit", netIncome.compareTo(BigDecimal.ZERO) >= 0);
        req.setAttribute("recentEntries", recentEntries);
        req.setAttribute("isBalanced", totalDebit.compareTo(totalCredit) == 0);

        req.getRequestDispatcher("/pages/dashboard.jsp").forward(req, resp);
    }
}
