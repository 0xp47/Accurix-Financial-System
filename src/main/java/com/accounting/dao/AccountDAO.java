package com.accounting.dao;

import com.accounting.model.Account;
import com.accounting.model.AccountType;
import com.accounting.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Chart of Accounts.
 */
public class AccountDAO {

    private static final Logger LOGGER = Logger.getLogger(AccountDAO.class.getName());

    /**
     * Retrieves all active accounts ordered by account code.
     *
     * @return list of active accounts
     */
    public List<Account> findAllActive() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, account_code, account_name, account_type, description, is_active, created_at, updated_at " +
                     "FROM chart_of_accounts WHERE is_active = TRUE ORDER BY account_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active accounts", e);
        }
        return accounts;
    }

    /**
     * Retrieves all accounts with optional search by code or name or type.
     *
     * @param query search query
     * @param typeFilter optional account type
     * @return filtered list of accounts
     */
    public List<Account> search(String query, String typeFilter) {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, account_code, account_name, account_type, description, is_active, created_at, updated_at " +
            "FROM chart_of_accounts WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (account_code LIKE ? OR account_name LIKE ? OR description LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter.trim())) {
            sql.append("AND account_type = ? ");
            params.add(typeFilter.trim().toUpperCase());
        }

        sql.append("ORDER BY account_code ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRowToAccount(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching accounts", e);
        }
        return accounts;
    }

    /**
     * Finds an account by ID.
     *
     * @param id account ID
     * @return Account or null
     */
    public Account findById(int id) {
        String sql = "SELECT id, account_code, account_name, account_type, description, is_active, created_at, updated_at " +
                     "FROM chart_of_accounts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAccount(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding account by ID: " + id, e);
        }
        return null;
    }

    /**
     * Finds an account by account code.
     *
     * @param code account code
     * @return Account or null
     */
    public Account findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, account_code, account_name, account_type, description, is_active, created_at, updated_at " +
                     "FROM chart_of_accounts WHERE account_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAccount(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding account by code: " + code, e);
        }
        return null;
    }

    /**
     * Creates a new account in Chart of Accounts.
     *
     * @param account account to create
     * @return generated ID or -1 on failure
     */
    public int createAccount(Account account) {
        String sql = "INSERT INTO chart_of_accounts (account_code, account_name, account_type, description, is_active) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getAccountCode().trim());
            stmt.setString(2, account.getAccountName().trim());
            stmt.setString(3, account.getAccountType().name());
            stmt.setString(4, account.getDescription());
            stmt.setBoolean(5, account.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        account.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating account: " + account.getAccountCode(), e);
        }
        return -1;
    }

    /**
     * Updates an existing account.
     *
     * @param account account to update
     * @return true if updated successfully
     */
    public boolean updateAccount(Account account) {
        String sql = "UPDATE chart_of_accounts SET account_name = ?, account_type = ?, description = ?, is_active = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountName().trim());
            stmt.setString(2, account.getAccountType().name());
            stmt.setString(3, account.getDescription());
            stmt.setBoolean(4, account.isActive());
            stmt.setInt(5, account.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating account: " + account.getId(), e);
            return false;
        }
    }

    public int countAccounts() {
        String sql = "SELECT COUNT(*) FROM chart_of_accounts WHERE is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting active accounts", e);
        }
        return 0;
    }

    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setAccountCode(rs.getString("account_code"));
        account.setAccountName(rs.getString("account_name"));
        account.setAccountType(AccountType.fromString(rs.getString("account_type")));
        account.setDescription(rs.getString("description"));
        account.setActive(rs.getBoolean("is_active"));
        account.setCreatedAt(rs.getTimestamp("created_at"));
        account.setUpdatedAt(rs.getTimestamp("updated_at"));
        return account;
    }
}
