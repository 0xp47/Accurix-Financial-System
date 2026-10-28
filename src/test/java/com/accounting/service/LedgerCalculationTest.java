package com.accounting.service;

import com.accounting.model.Account;
import com.accounting.model.AccountLedger;
import com.accounting.model.AccountType;
import com.accounting.model.LedgerEntry;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Unit test validating General Ledger balance calculation rules.
 */
public class LedgerCalculationTest {

    @Test
    public void testAssetNormalDebitRunningBalance() {
        Account cashAccount = new Account(1, "1010", "Cash", AccountType.ASSET, "Asset account", true);
        AccountLedger ledger = new AccountLedger(cashAccount);

        BigDecimal balance = BigDecimal.ZERO;

        // Entry 1: Debit 150,000 (Initial Investment)
        LedgerEntry e1 = new LedgerEntry();
        e1.setDebit(new BigDecimal("150000.00"));
        e1.setCredit(BigDecimal.ZERO);
        balance = balance.add(e1.getDebit()).subtract(e1.getCredit());
        e1.setRunningBalance(balance);
        ledger.addEntry(e1);

        assertEquals(new BigDecimal("150000.00"), e1.getRunningBalance());

        // Entry 2: Credit 20,000 (Rent payment)
        LedgerEntry e2 = new LedgerEntry();
        e2.setDebit(BigDecimal.ZERO);
        e2.setCredit(new BigDecimal("20000.00"));
        balance = balance.add(e2.getDebit()).subtract(e2.getCredit());
        e2.setRunningBalance(balance);
        ledger.addEntry(e2);

        assertEquals(new BigDecimal("130000.00"), e2.getRunningBalance());

        // Entry 3: Debit 30,000 (Service cash received)
        LedgerEntry e3 = new LedgerEntry();
        e3.setDebit(new BigDecimal("30000.00"));
        e3.setCredit(BigDecimal.ZERO);
        balance = balance.add(e3.getDebit()).subtract(e3.getCredit());
        e3.setRunningBalance(balance);
        ledger.addEntry(e3);

        assertEquals(new BigDecimal("160000.00"), e3.getRunningBalance());
        assertEquals(new BigDecimal("160000.00"), balance);
    }

    @Test
    public void testLiabilityNormalCreditRunningBalance() {
        Account apAccount = new Account(6, "2010", "Accounts Payable", AccountType.LIABILITY, "Liability account", true);
        AccountLedger ledger = new AccountLedger(apAccount);

        BigDecimal balance = BigDecimal.ZERO;

        // Entry 1: Credit 35,000 (Equipment bought on credit)
        LedgerEntry e1 = new LedgerEntry();
        e1.setDebit(BigDecimal.ZERO);
        e1.setCredit(new BigDecimal("35000.00"));
        balance = balance.add(e1.getCredit()).subtract(e1.getDebit());
        e1.setRunningBalance(balance);
        ledger.addEntry(e1);

        assertEquals(new BigDecimal("35000.00"), e1.getRunningBalance());

        // Entry 2: Debit 15,000 (Partial payment to vendor)
        LedgerEntry e2 = new LedgerEntry();
        e2.setDebit(new BigDecimal("15000.00"));
        e2.setCredit(BigDecimal.ZERO);
        balance = balance.add(e2.getCredit()).subtract(e2.getDebit());
        e2.setRunningBalance(balance);
        ledger.addEntry(e2);

        assertEquals(new BigDecimal("20000.00"), e2.getRunningBalance());
        assertEquals(new BigDecimal("20000.00"), balance);
    }

    @Test
    public void testAccountTypeCalculateBalance() {
        // Asset (Normal Debit)
        BigDecimal assetBalance = AccountType.ASSET.calculateBalance(new BigDecimal("100.00"), new BigDecimal("30.00"));
        assertEquals(new BigDecimal("70.00"), assetBalance);

        // Liability (Normal Credit)
        BigDecimal liabilityBalance = AccountType.LIABILITY.calculateBalance(new BigDecimal("20.00"), new BigDecimal("100.00"));
        assertEquals(new BigDecimal("80.00"), liabilityBalance);

        // Revenue (Normal Credit)
        BigDecimal revenueBalance = AccountType.REVENUE.calculateBalance(BigDecimal.ZERO, new BigDecimal("5000.00"));
        assertEquals(new BigDecimal("5000.00"), revenueBalance);

        // Expense (Normal Debit)
        BigDecimal expenseBalance = AccountType.EXPENSE.calculateBalance(new BigDecimal("2500.00"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("2500.00"), expenseBalance);
    }
}
