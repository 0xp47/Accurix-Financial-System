package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Line item in the Income Statement report.
 */
public class IncomeStatementItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountCode;
    private String accountName;
    private BigDecimal amount = BigDecimal.ZERO;

    public IncomeStatementItem() {
    }

    public IncomeStatementItem(String accountCode, String accountName, BigDecimal amount) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }
}
