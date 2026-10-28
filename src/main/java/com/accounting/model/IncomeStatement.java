package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Report model for the Income Statement (Statement of Profit or Loss).
 */
public class IncomeStatement implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<IncomeStatementItem> revenues = new ArrayList<>();
    private List<IncomeStatementItem> expenses = new ArrayList<>();
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;
    private String startDate;
    private String endDate;

    public IncomeStatement() {
    }

    public List<IncomeStatementItem> getRevenues() {
        return revenues;
    }

    public void setRevenues(List<IncomeStatementItem> revenues) {
        this.revenues = (revenues != null) ? revenues : new ArrayList<>();
    }

    public void addRevenue(IncomeStatementItem item) {
        if (item != null) {
            this.revenues.add(item);
            this.totalRevenue = this.totalRevenue.add(item.getAmount());
        }
    }

    public List<IncomeStatementItem> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<IncomeStatementItem> expenses) {
        this.expenses = (expenses != null) ? expenses : new ArrayList<>();
    }

    public void addExpense(IncomeStatementItem item) {
        if (item != null) {
            this.expenses.add(item);
            this.totalExpense = this.totalExpense.add(item.getAmount());
        }
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = (totalExpense != null) ? totalExpense : BigDecimal.ZERO;
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

    /**
     * Net Income = Total Revenue - Total Expenses
     *
     * @return net profit (positive) or net loss (negative)
     */
    public BigDecimal getNetIncome() {
        return totalRevenue.subtract(totalExpense);
    }

    public boolean isNetProfit() {
        return getNetIncome().compareTo(BigDecimal.ZERO) >= 0;
    }
}
