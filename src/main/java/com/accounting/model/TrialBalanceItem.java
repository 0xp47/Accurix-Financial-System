package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Single line item in the Trial Balance report.
 */
public class TrialBalanceItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountCode;
    private String accountName;
    private AccountType accountType;
    private BigDecimal debitBalance = BigDecimal.ZERO;
    private BigDecimal creditBalance = BigDecimal.ZERO;

    public TrialBalanceItem() {
    }

    public TrialBalanceItem(String accountCode, String accountName, AccountType accountType, BigDecimal debitBalance, BigDecimal creditBalance) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.debitBalance = (debitBalance != null) ? debitBalance : BigDecimal.ZERO;
        this.creditBalance = (creditBalance != null) ? creditBalance : BigDecimal.ZERO;
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

    public BigDecimal getDebitBalance() {
        return debitBalance;
    }

    public void setDebitBalance(BigDecimal debitBalance) {
        this.debitBalance = (debitBalance != null) ? debitBalance : BigDecimal.ZERO;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = (creditBalance != null) ? creditBalance : BigDecimal.ZERO;
    }

    public boolean isDebitPositive() {
        return debitBalance != null && debitBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCreditPositive() {
        return creditBalance != null && creditBalance.compareTo(BigDecimal.ZERO) > 0;
    }
}
