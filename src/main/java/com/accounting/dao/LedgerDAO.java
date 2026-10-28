package com.accounting.dao;

import com.accounting.model.Account;
import com.accounting.model.AccountLedger;
import com.accounting.model.AccountType;
import com.accounting.model.LedgerEntry;
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
 * Data Access Object for General Ledger postings and balance computations.
 */
public class LedgerDAO {

    private static final Logger LOGGER = Logger.getLogger(LedgerDAO.class.getName());
    private final AccountDAO accountDAO = new AccountDAO();

    /**
     * Computes the complete ledger for an account, including beginning balance,
     * chronological transaction postings, running balance, and ending balance.
     *
     * @param accountId target account ID
     * @param startDate optional start date filter
     * @param endDate   optional end date filter
     * @return populated AccountLedger, or null if account does not exist
     */
    public AccountLedger getAccountLedger(int accountId, String startDate, String endDate) {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            return null;
        }

        AccountLedger ledger = new AccountLedger(account);
        ledger.setStartDate(startDate);
        ledger.setEndDate(endDate);

        AccountType type = account.getAccountType();
        BigDecimal runningBalance = BigDecimal.ZERO;

        // 1. Calculate Beginning Balance if a start date is specified
        if (startDate != null && !startDate.trim().isEmpty()) {
            String priorSql = 
                "SELECT COALESCE(SUM(jel.debit), 0) AS prior_debit, " +
                "       COALESCE(SUM(jel.credit), 0) AS prior_credit " +
                "FROM journal_entry_lines jel " +
                "JOIN journal_entries je ON jel.journal_entry_id = je.id " +
                "WHERE jel.account_id = ? AND je.entry_date < ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(priorSql)) {

                stmt.setInt(1, accountId);
                stmt.setDate(2, Date.valueOf(startDate.trim()));

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal priorDebit = rs.getBigDecimal("prior_debit");
                        BigDecimal priorCredit = rs.getBigDecimal("prior_credit");
                        runningBalance = type.calculateBalance(priorDebit, priorCredit);
                        ledger.setBeginningBalance(runningBalance);
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error calculating beginning balance for account: " + accountId, e);
            }
        }

        // 2. Fetch all postings within the period ordered chronologically
        StringBuilder sql = new StringBuilder(
            "SELECT jel.id AS line_id, je.id AS journal_entry_id, je.reference_number, " +
            "       je.entry_date, je.particulars, jel.debit, jel.credit " +
            "FROM journal_entry_lines jel " +
            "JOIN journal_entries je ON jel.journal_entry_id = je.id " +
            "WHERE jel.account_id = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(accountId);

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND je.entry_date >= ? ");
            params.add(Date.valueOf(startDate.trim()));
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND je.entry_date <= ? ");
            params.add(Date.valueOf(endDate.trim()));
        }

        sql.append("ORDER BY je.entry_date ASC, je.id ASC, jel.id ASC");

        BigDecimal totalPeriodDebit = BigDecimal.ZERO;
        BigDecimal totalPeriodCredit = BigDecimal.ZERO;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LedgerEntry entry = new LedgerEntry();
                    entry.setLineId(rs.getInt("line_id"));
                    entry.setJournalEntryId(rs.getInt("journal_entry_id"));
                    entry.setReferenceNumber(rs.getString("reference_number"));
                    entry.setEntryDate(rs.getDate("entry_date"));
                    entry.setParticulars(rs.getString("particulars"));

                    BigDecimal debit = rs.getBigDecimal("debit");
                    BigDecimal credit = rs.getBigDecimal("credit");

                    entry.setDebit(debit);
                    entry.setCredit(credit);

                    totalPeriodDebit = totalPeriodDebit.add(debit);
                    totalPeriodCredit = totalPeriodCredit.add(credit);

                    // Accounting running balance rule based on normal balance type
                    if (type.isNormalDebit()) {
                        runningBalance = runningBalance.add(debit).subtract(credit);
                    } else {
                        runningBalance = runningBalance.add(credit).subtract(debit);
                    }

                    entry.setRunningBalance(runningBalance);
                    ledger.addEntry(entry);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving ledger entries for account: " + accountId, e);
        }

        ledger.setTotalDebit(totalPeriodDebit);
        ledger.setTotalCredit(totalPeriodCredit);
        ledger.setEndingBalance(runningBalance);

        return ledger;
    }

    /**
     * Generates a summary overview of all accounts with current debit/credit activity and ending balances.
     *
     * @param startDate optional start date filter
     * @param endDate   optional end date filter
     * @return list of populated AccountLedger summary objects
     */
    public List<AccountLedger> getLedgerSummary(String startDate, String endDate) {
        List<Account> accounts = accountDAO.findAllActive();
        List<AccountLedger> summaries = new ArrayList<>();

        for (Account acc : accounts) {
            AccountLedger ledger = getAccountLedger(acc.getId(), startDate, endDate);
            if (ledger != null) {
                summaries.add(ledger);
            }
        }
        return summaries;
    }
}
