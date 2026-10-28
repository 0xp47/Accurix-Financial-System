package com.accounting.dao;

import com.accounting.model.AccountType;
import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;
import com.accounting.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Journal Entries and Lines with ACID transaction integrity.
 */
public class JournalEntryDAO {

    private static final Logger LOGGER = Logger.getLogger(JournalEntryDAO.class.getName());

    /**
     * Persists a complete journal entry header and all associated lines within a single transaction.
     *
     * @param entry entry to save
     * @return generated entry ID, or -1 on error
     */
    public int createEntry(JournalEntry entry) {
        String insertHeaderSql = 
            "INSERT INTO journal_entries (entry_date, particulars, reference_number, created_by) VALUES (?, ?, ?, ?)";
        String insertLineSql = 
            "INSERT INTO journal_entry_lines (journal_entry_id, account_id, debit, credit, line_order) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            int entryId = -1;
            try (PreparedStatement headerStmt = conn.prepareStatement(insertHeaderSql, Statement.RETURN_GENERATED_KEYS)) {
                headerStmt.setDate(1, entry.getEntryDate());
                headerStmt.setString(2, entry.getParticulars());
                headerStmt.setString(3, entry.getReferenceNumber());
                if (entry.getCreatedBy() != null) {
                    headerStmt.setInt(4, entry.getCreatedBy());
                } else {
                    headerStmt.setNull(4, java.sql.Types.INTEGER);
                }

                headerStmt.executeUpdate();

                try (ResultSet rs = headerStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entryId = rs.getInt(1);
                        entry.setId(entryId);
                    }
                }
            }

            if (entryId <= 0) {
                conn.rollback();
                return -1;
            }

            try (PreparedStatement lineStmt = conn.prepareStatement(insertLineSql)) {
                List<JournalEntryLine> lines = entry.getLines();
                for (int i = 0; i < lines.size(); i++) {
                    JournalEntryLine line = lines.get(i);
                    lineStmt.setInt(1, entryId);
                    lineStmt.setInt(2, line.getAccountId());
                    lineStmt.setBigDecimal(3, line.getDebit());
                    lineStmt.setBigDecimal(4, line.getCredit());
                    lineStmt.setInt(5, i + 1);
                    lineStmt.addBatch();
                }
                lineStmt.executeBatch();
            }

            conn.commit(); // Commit Transaction
            return entryId;

        } catch (SQLException e) {
            DBConnection.rollbackQuietly(conn);
            LOGGER.log(Level.SEVERE, "Failed to save journal entry in transaction", e);
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                DBConnection.closeQuietly(conn);
            }
        }
    }

    /**
     * Updates an existing journal entry header and replaces its lines in an ACID transaction.
     *
     * @param entry updated journal entry
     * @return true if updated successfully
     */
    public boolean updateEntry(JournalEntry entry) {
        String updateHeaderSql = 
            "UPDATE journal_entries SET entry_date = ?, particulars = ?, reference_number = ? WHERE id = ?";
        String deleteLinesSql = 
            "DELETE FROM journal_entry_lines WHERE journal_entry_id = ?";
        String insertLineSql = 
            "INSERT INTO journal_entry_lines (journal_entry_id, account_id, debit, credit, line_order) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement headerStmt = conn.prepareStatement(updateHeaderSql)) {
                headerStmt.setDate(1, entry.getEntryDate());
                headerStmt.setString(2, entry.getParticulars());
                headerStmt.setString(3, entry.getReferenceNumber());
                headerStmt.setInt(4, entry.getId());
                headerStmt.executeUpdate();
            }

            // Remove existing lines
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteLinesSql)) {
                deleteStmt.setInt(1, entry.getId());
                deleteStmt.executeUpdate();
            }

            // Insert new lines
            try (PreparedStatement lineStmt = conn.prepareStatement(insertLineSql)) {
                List<JournalEntryLine> lines = entry.getLines();
                for (int i = 0; i < lines.size(); i++) {
                    JournalEntryLine line = lines.get(i);
                    lineStmt.setInt(1, entry.getId());
                    lineStmt.setInt(2, line.getAccountId());
                    lineStmt.setBigDecimal(3, line.getDebit());
                    lineStmt.setBigDecimal(4, line.getCredit());
                    lineStmt.setInt(5, i + 1);
                    lineStmt.addBatch();
                }
                lineStmt.executeBatch();
            }

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            DBConnection.rollbackQuietly(conn);
            LOGGER.log(Level.SEVERE, "Failed to update journal entry: " + entry.getId(), e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                DBConnection.closeQuietly(conn);
            }
        }
    }

    /**
     * Deletes a journal entry and its lines in a transaction.
     *
     * @param id entry ID
     * @return true if deleted
     */
    public boolean deleteEntry(int id) {
        String deleteLinesSql = "DELETE FROM journal_entry_lines WHERE journal_entry_id = ?";
        String deleteHeaderSql = "DELETE FROM journal_entries WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement deleteLines = conn.prepareStatement(deleteLinesSql)) {
                deleteLines.setInt(1, id);
                deleteLines.executeUpdate();
            }

            boolean deleted;
            try (PreparedStatement deleteHeader = conn.prepareStatement(deleteHeaderSql)) {
                deleteHeader.setInt(1, id);
                deleted = (deleteHeader.executeUpdate() > 0);
            }

            conn.commit();
            return deleted;

        } catch (SQLException e) {
            DBConnection.rollbackQuietly(conn);
            LOGGER.log(Level.SEVERE, "Failed to delete journal entry: " + id, e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                DBConnection.closeQuietly(conn);
            }
        }
    }

    /**
     * Retrieves a journal entry by ID with lines and account metadata.
     *
     * @param id entry ID
     * @return populated JournalEntry or null
     */
    public JournalEntry findById(int id) {
        String headerSql = 
            "SELECT je.id, je.entry_date, je.particulars, je.reference_number, je.created_by, " +
            "u.full_name AS creator_name, je.created_at, je.updated_at " +
            "FROM journal_entries je " +
            "LEFT JOIN users u ON je.created_by = u.id " +
            "WHERE je.id = ?";

        String linesSql = 
            "SELECT jel.id, jel.journal_entry_id, jel.account_id, jel.debit, jel.credit, jel.line_order, " +
            "coa.account_code, coa.account_name, coa.account_type " +
            "FROM journal_entry_lines jel " +
            "JOIN chart_of_accounts coa ON jel.account_id = coa.id " +
            "WHERE jel.journal_entry_id = ? " +
            "ORDER BY jel.line_order ASC, jel.id ASC";

        try (Connection conn = DBConnection.getConnection()) {
            JournalEntry entry = null;

            try (PreparedStatement headerStmt = conn.prepareStatement(headerSql)) {
                headerStmt.setInt(1, id);
                try (ResultSet rs = headerStmt.executeQuery()) {
                    if (rs.next()) {
                        entry = new JournalEntry();
                        entry.setId(rs.getInt("id"));
                        entry.setEntryDate(rs.getDate("entry_date"));
                        entry.setParticulars(rs.getString("particulars"));
                        entry.setReferenceNumber(rs.getString("reference_number"));
                        entry.setCreatedBy((Integer) rs.getObject("created_by"));
                        entry.setCreatorName(rs.getString("creator_name"));
                        entry.setCreatedAt(rs.getTimestamp("created_at"));
                        entry.setUpdatedAt(rs.getTimestamp("updated_at"));
                    }
                }
            }

            if (entry == null) {
                return null;
            }

            try (PreparedStatement linesStmt = conn.prepareStatement(linesSql)) {
                linesStmt.setInt(1, id);
                try (ResultSet rs = linesStmt.executeQuery()) {
                    while (rs.next()) {
                        JournalEntryLine line = new JournalEntryLine();
                        line.setId(rs.getInt("id"));
                        line.setJournalEntryId(rs.getInt("journal_entry_id"));
                        line.setAccountId(rs.getInt("account_id"));
                        line.setDebit(rs.getBigDecimal("debit"));
                        line.setCredit(rs.getBigDecimal("credit"));
                        line.setLineOrder(rs.getInt("line_order"));
                        line.setAccountCode(rs.getString("account_code"));
                        line.setAccountName(rs.getString("account_name"));
                        line.setAccountType(AccountType.fromString(rs.getString("account_type")));
                        entry.addLine(line);
                    }
                }
            }

            return entry;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding journal entry by ID: " + id, e);
            return null;
        }
    }

    /**
     * Retrieves all journal entries with date filtering and search.
     *
     * @param startDate start date filter (inclusive)
     * @param endDate   end date filter (inclusive)
     * @param query     keyword filter on particulars or reference
     * @return list of journal entries
     */
    public List<JournalEntry> findAll(String startDate, String endDate, String query) {
        List<JournalEntry> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT je.id, je.entry_date, je.particulars, je.reference_number, je.created_by, " +
            "u.full_name AS creator_name, je.created_at, je.updated_at, " +
            "COALESCE(SUM(jel.debit), 0) AS total_debit, " +
            "COALESCE(SUM(jel.credit), 0) AS total_credit " +
            "FROM journal_entries je " +
            "LEFT JOIN users u ON je.created_by = u.id " +
            "LEFT JOIN journal_entry_lines jel ON je.id = jel.journal_entry_id " +
            "WHERE 1=1 "
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

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (je.particulars LIKE ? OR je.reference_number LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }

        sql.append("GROUP BY je.id, je.entry_date, je.particulars, je.reference_number, je.created_by, u.full_name, je.created_at, je.updated_at ");
        sql.append("ORDER BY je.entry_date DESC, je.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JournalEntry entry = new JournalEntry();
                    entry.setId(rs.getInt("id"));
                    entry.setEntryDate(rs.getDate("entry_date"));
                    entry.setParticulars(rs.getString("particulars"));
                    entry.setReferenceNumber(rs.getString("reference_number"));
                    entry.setCreatedBy((Integer) rs.getObject("created_by"));
                    entry.setCreatorName(rs.getString("creator_name"));
                    entry.setCreatedAt(rs.getTimestamp("created_at"));
                    entry.setUpdatedAt(rs.getTimestamp("updated_at"));

                    // Mock a dummy line with debit/credit totals for summary row display
                    BigDecimal debit = rs.getBigDecimal("total_debit");
                    BigDecimal credit = rs.getBigDecimal("total_credit");
                    JournalEntryLine summaryLine = new JournalEntryLine(0, debit, credit);
                    entry.addLine(summaryLine);

                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving journal entries", e);
        }
        return list;
    }

    public int countEntries() {
        String sql = "SELECT COUNT(*) FROM journal_entries";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting journal entries", e);
        }
        return 0;
    }

    public List<JournalEntry> findRecent(int limit) {
        List<JournalEntry> all = findAll(null, null, null);
        if (all.size() <= limit) {
            return all;
        }
        return all.subList(0, limit);
    }
}
