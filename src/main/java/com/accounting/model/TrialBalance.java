package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Report model for the Trial Balance.
 */
public class TrialBalance implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TrialBalanceItem> items = new ArrayList<>();
    private BigDecimal totalDebit = BigDecimal.ZERO;
    private BigDecimal totalCredit = BigDecimal.ZERO;
    private String asOfDate;

    public TrialBalance() {
    }

    public List<TrialBalanceItem> getItems() {
        return items;
    }

    public void setItems(List<TrialBalanceItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public void addItem(TrialBalanceItem item) {
        if (item != null) {
            this.items.add(item);
            this.totalDebit = this.totalDebit.add(item.getDebitBalance());
            this.totalCredit = this.totalCredit.add(item.getCreditBalance());
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

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    public boolean isBalanced() {
        return totalDebit.compareTo(totalCredit) == 0;
    }

    public BigDecimal getDifference() {
        return totalDebit.subtract(totalCredit).abs();
    }
}
