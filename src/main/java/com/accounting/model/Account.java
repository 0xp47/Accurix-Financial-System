package com.accounting.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Account entity representing an item in the Chart of Accounts.
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String accountCode;
    private String accountName;
    private AccountType accountType;
    private String description;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Account() {
        this.isActive = true;
    }

    public Account(int id, String accountCode, String accountName, AccountType accountType, String description, boolean isActive) {
        this.id = id;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.description = description;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return accountCode + " - " + accountName + " (" + accountType + ")";
    }
}
