package com.accounting.service;

import com.accounting.model.AccountType;
import com.accounting.model.BalanceSheet;
import com.accounting.model.BalanceSheetItem;
import com.accounting.model.IncomeStatement;
import com.accounting.model.IncomeStatementItem;
import com.accounting.model.TrialBalance;
import com.accounting.model.TrialBalanceItem;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Unit tests validating financial reporting calculations and accounting identities.
 */
public class FinancialReportsCalculationTest {

    @Test
    public void testIncomeStatementNetProfitCalculation() {
        IncomeStatement stmt = new IncomeStatement();
        stmt.addRevenue(new IncomeStatementItem("4010", "Service Revenue", new BigDecimal("35000.00")));
        stmt.addRevenue(new IncomeStatementItem("4020", "Consulting Revenue", new BigDecimal("15000.00")));

        stmt.addExpense(new IncomeStatementItem("5010", "Rent Expense", new BigDecimal("12000.00")));
        stmt.addExpense(new IncomeStatementItem("5020", "Salaries Expense", new BigDecimal("8000.00")));

        assertEquals(new BigDecimal("50000.00"), stmt.getTotalRevenue());
        assertEquals(new BigDecimal("20000.00"), stmt.getTotalExpense());

        // Net Income = 50,000 - 20,000 = 30,000
        assertEquals(new BigDecimal("30000.00"), stmt.getNetIncome());
        assertTrue(stmt.isNetProfit());
    }

    @Test
    public void testIncomeStatementNetLossCalculation() {
        IncomeStatement stmt = new IncomeStatement();
        stmt.addRevenue(new IncomeStatementItem("4010", "Service Revenue", new BigDecimal("5000.00")));
        stmt.addExpense(new IncomeStatementItem("5010", "Rent Expense", new BigDecimal("12000.00")));

        // Net Income = 5,000 - 12,000 = -7,000
        assertEquals(new BigDecimal("-7000.00"), stmt.getNetIncome());
        assertFalse(stmt.isNetProfit());
    }

    @Test
    public void testTrialBalanceBalanced() {
        TrialBalance tb = new TrialBalance();
        tb.addItem(new TrialBalanceItem("1010", "Cash", AccountType.ASSET, new BigDecimal("100000.00"), BigDecimal.ZERO));
        tb.addItem(new TrialBalanceItem("1050", "Equipment", AccountType.ASSET, new BigDecimal("35000.00"), BigDecimal.ZERO));
        tb.addItem(new TrialBalanceItem("2010", "Accounts Payable", AccountType.LIABILITY, BigDecimal.ZERO, new BigDecimal("35000.00")));
        tb.addItem(new TrialBalanceItem("3010", "Owner's Capital", AccountType.EQUITY, BigDecimal.ZERO, new BigDecimal("100000.00")));

        assertEquals(new BigDecimal("135000.00"), tb.getTotalDebit());
        assertEquals(new BigDecimal("135000.00"), tb.getTotalCredit());
        assertTrue(tb.isBalanced());
        assertEquals(0, tb.getDifference().compareTo(BigDecimal.ZERO));
    }

    @Test
    public void testTrialBalanceUnbalanced() {
        TrialBalance tb = new TrialBalance();
        tb.addItem(new TrialBalanceItem("1010", "Cash", AccountType.ASSET, new BigDecimal("100000.00"), BigDecimal.ZERO));
        tb.addItem(new TrialBalanceItem("3010", "Owner's Capital", AccountType.EQUITY, BigDecimal.ZERO, new BigDecimal("95000.00")));

        assertFalse(tb.isBalanced());
        assertEquals(new BigDecimal("5000.00"), tb.getDifference());
    }

    @Test
    public void testBalanceSheetEquationSatisfied() {
        BalanceSheet sheet = new BalanceSheet();

        // Assets
        sheet.addAsset(new BalanceSheetItem("1010", "Cash", new BigDecimal("120000.00")));
        sheet.addAsset(new BalanceSheetItem("1050", "Office Equipment", new BigDecimal("35000.00")));
        assertEquals(new BigDecimal("155000.00"), sheet.getTotalAssets());

        // Liabilities
        sheet.addLiability(new BalanceSheetItem("2010", "Accounts Payable", new BigDecimal("20000.00")));
        assertEquals(new BigDecimal("20000.00"), sheet.getTotalLiabilities());

        // Equity: Capital 110,000 + Current Net Income 25,000 = 135,000
        sheet.addEquityItem(new BalanceSheetItem("3010", "Owner's Capital", new BigDecimal("110000.00")));
        sheet.setCurrentNetIncome(new BigDecimal("25000.00"));

        // Assets (155,000) = Liabilities (20,000) + Equity (110,000 + 25,000) = 155,000
        assertEquals(new BigDecimal("155000.00"), sheet.getTotalLiabilitiesAndEquity());
        assertTrue(sheet.isBalanced());
        assertEquals(0, sheet.getDifference().compareTo(BigDecimal.ZERO));
    }
}
