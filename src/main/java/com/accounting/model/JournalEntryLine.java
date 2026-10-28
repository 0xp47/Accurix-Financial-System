package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Journal entry line item representing an individual debit or credit.
 */
public class JournalEntryLine implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int journalEntryId;
    private int accountId;
    private String accountCode;
    private String accountName;
    private AccountType accountType;
    private BigDecimal debit = BigDecimal.ZERO;
    private BigDecimal credit = BigDecimal.ZERO;
    private int lineOrder;

    public JournalEntryLine() {
    }

    public JournalEntryLine(int accountId, BigDecimal debit, BigDecimal credit) {
        this.accountId = accountId;
        this.debit = (debit != null) ? debit : BigDecimal.ZERO;
        this.credit = (credit != null) ? credit : BigDecimal.ZERO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(int journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
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

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = (debit != null) ? debit : BigDecimal.ZERO;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = (credit != null) ? credit : BigDecimal.ZERO;
    }

    public int getLineOrder() {
        return lineOrder;
    }

    public void setLineOrder(int lineOrder) {
        this.lineOrder = lineOrder;
    }

    public boolean isDebitPositive() {
        return debit != null && debit.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCreditPositive() {
        return credit != null && credit.compareTo(BigDecimal.ZERO) > 0;
    }
}
