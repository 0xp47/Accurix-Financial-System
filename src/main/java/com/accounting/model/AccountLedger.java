package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete General Ledger view for a specific account.
 */
public class AccountLedger implements Serializable {
    private static final long serialVersionUID = 1L;

    private Account account;
    private BigDecimal beginningBalance = BigDecimal.ZERO;
    private List<LedgerEntry> entries = new ArrayList<>();
    private BigDecimal totalDebit = BigDecimal.ZERO;
    private BigDecimal totalCredit = BigDecimal.ZERO;
    private BigDecimal endingBalance = BigDecimal.ZERO;
    private String startDate;
    private String endDate;

    public AccountLedger() {
    }

    public AccountLedger(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(BigDecimal beginningBalance) {
        this.beginningBalance = (beginningBalance != null) ? beginningBalance : BigDecimal.ZERO;
    }

    public List<LedgerEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = (entries != null) ? entries : new ArrayList<>();
    }

    public void addEntry(LedgerEntry entry) {
        if (entry != null) {
            this.entries.add(entry);
        }
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = (totalDebit != null) ? totalDebit : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = (totalCredit != null) ? totalCredit : BigDecimal.ZERO;
    }

    public BigDecimal getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(BigDecimal endingBalance) {
        this.endingBalance = (endingBalance != null) ? endingBalance : BigDecimal.ZERO;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
