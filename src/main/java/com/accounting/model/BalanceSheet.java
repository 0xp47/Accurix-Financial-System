package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Report model for the Balance Sheet (Statement of Financial Position).
 * Represents the fundamental identity: Assets = Liabilities + Equity
 */
public class BalanceSheet implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<BalanceSheetItem> assets = new ArrayList<>();
    private List<BalanceSheetItem> liabilities = new ArrayList<>();
    private List<BalanceSheetItem> equityItems = new ArrayList<>();

    private BigDecimal totalAssets = BigDecimal.ZERO;
    private BigDecimal totalLiabilities = BigDecimal.ZERO;
    private BigDecimal totalEquity = BigDecimal.ZERO;
    private BigDecimal currentNetIncome = BigDecimal.ZERO;
    private String asOfDate;

    public BalanceSheet() {
    }

    public List<BalanceSheetItem> getAssets() {
        return assets;
    }

    public void setAssets(List<BalanceSheetItem> assets) {
        this.assets = (assets != null) ? assets : new ArrayList<>();
    }

    public void addAsset(BalanceSheetItem item) {
        if (item != null) {
            this.assets.add(item);
            this.totalAssets = this.totalAssets.add(item.getAmount());
        }
    }

    public List<BalanceSheetItem> getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(List<BalanceSheetItem> liabilities) {
        this.liabilities = (liabilities != null) ? liabilities : new ArrayList<>();
    }

    public void addLiability(BalanceSheetItem item) {
        if (item != null) {
            this.liabilities.add(item);
            this.totalLiabilities = this.totalLiabilities.add(item.getAmount());
        }
    }

    public List<BalanceSheetItem> getEquityItems() {
        return equityItems;
    }

    public void setEquityItems(List<BalanceSheetItem> equityItems) {
        this.equityItems = (equityItems != null) ? equityItems : new ArrayList<>();
    }

    public void addEquityItem(BalanceSheetItem item) {
        if (item != null) {
            this.equityItems.add(item);
            this.totalEquity = this.totalEquity.add(item.getAmount());
        }
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = (totalAssets != null) ? totalAssets : BigDecimal.ZERO;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = (totalLiabilities != null) ? totalLiabilities : BigDecimal.ZERO;
    }

    public BigDecimal getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(BigDecimal totalEquity) {
        this.totalEquity = (totalEquity != null) ? totalEquity : BigDecimal.ZERO;
    }

    public BigDecimal getCurrentNetIncome() {
        return currentNetIncome;
    }

    public void setCurrentNetIncome(BigDecimal currentNetIncome) {
        this.currentNetIncome = (currentNetIncome != null) ? currentNetIncome : BigDecimal.ZERO;
    }

    public boolean isCurrentNetIncomeNonNegative() {
        return currentNetIncome != null && currentNetIncome.compareTo(BigDecimal.ZERO) >= 0;
    }

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    /**
     * Total Liabilities and Equity = Total Liabilities + Total Equity + Current Period Net Income
     *
     * @return sum of claims on assets
     */
    public BigDecimal getTotalLiabilitiesAndEquity() {
        return totalLiabilities.add(totalEquity).add(currentNetIncome);
    }

    /**
     * Verifies the accounting equation: Assets = Liabilities + Equity
     *
     * @return true if balanced
     */
    public boolean isBalanced() {
        return totalAssets.compareTo(getTotalLiabilitiesAndEquity()) == 0;
    }

    /**
     * Calculates the imbalance discrepancy.
     *
     * @return absolute difference
     */
    public BigDecimal getDifference() {
        return totalAssets.subtract(getTotalLiabilitiesAndEquity()).abs();
    }
}
