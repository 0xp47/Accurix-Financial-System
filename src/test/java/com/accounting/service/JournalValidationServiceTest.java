package com.accounting.service;

import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;
import com.accounting.service.JournalValidationService.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Unit test suite validating accounting double-entry rules.
 */
public class JournalValidationServiceTest {

    private JournalValidationService service;

    @Before
    public void setUp() {
        service = new JournalValidationService();
    }

    @Test
    public void testValidBalancedEntry() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Paid October office rent in cash");

        entry.addLine(new JournalEntryLine(13, new BigDecimal("12000.00"), BigDecimal.ZERO)); // Rent Expense
        entry.addLine(new JournalEntryLine(1, BigDecimal.ZERO, new BigDecimal("12000.00")));  // Cash

        ValidationResult result = service.validate(entry);
        assertTrue("Entry should be valid and balanced", result.isValid());
        assertNull(result.getMessage());
    }

    @Test
    public void testMultiLineBalancedEntry() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-20"));
        entry.setParticulars("Mixed revenue settlement: cash and accounts receivable");

        entry.addLine(new JournalEntryLine(1, new BigDecimal("20000.00"), BigDecimal.ZERO));  // Cash
        entry.addLine(new JournalEntryLine(2, new BigDecimal("10000.00"), BigDecimal.ZERO));  // AR
        entry.addLine(new JournalEntryLine(11, BigDecimal.ZERO, new BigDecimal("30000.00"))); // Revenue

        ValidationResult result = service.validate(entry);
        assertTrue("Compound multi-line entry should be valid", result.isValid());
    }

    @Test
    public void testUnbalancedEntryFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Unbalanced transaction");

        entry.addLine(new JournalEntryLine(1, new BigDecimal("10000.00"), BigDecimal.ZERO));
        entry.addLine(new JournalEntryLine(9, BigDecimal.ZERO, new BigDecimal("9500.00")));

        ValidationResult result = service.validate(entry);
        assertFalse("Unbalanced entry must fail validation", result.isValid());
        assertTrue(result.getMessage().contains("Unbalanced entry"));
    }

    @Test
    public void testMissingDateFails() {
        JournalEntry entry = new JournalEntry();
        entry.setParticulars("No date provided");
        entry.addLine(new JournalEntryLine(1, new BigDecimal("100.00"), BigDecimal.ZERO));
        entry.addLine(new JournalEntryLine(2, BigDecimal.ZERO, new BigDecimal("100.00")));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertEquals("Entry date is required.", result.getMessage());
    }

    @Test
    public void testBlankParticularsFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("   ");
        entry.addLine(new JournalEntryLine(1, new BigDecimal("100.00"), BigDecimal.ZERO));
        entry.addLine(new JournalEntryLine(2, BigDecimal.ZERO, new BigDecimal("100.00")));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertEquals("Particulars / description cannot be blank.", result.getMessage());
    }

    @Test
    public void testFewerThanTwoLinesFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Only one line");
        entry.addLine(new JournalEntryLine(1, new BigDecimal("100.00"), BigDecimal.ZERO));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("at least two account lines"));
    }

    @Test
    public void testBothDebitAndCreditOnSameLineFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Invalid line amounts");

        entry.addLine(new JournalEntryLine(1, new BigDecimal("100.00"), new BigDecimal("50.00")));
        entry.addLine(new JournalEntryLine(2, BigDecimal.ZERO, new BigDecimal("50.00")));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("both Debit and Credit"));
    }

    @Test
    public void testNegativeAmountFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Negative amount");

        entry.addLine(new JournalEntryLine(1, new BigDecimal("-100.00"), BigDecimal.ZERO));
        entry.addLine(new JournalEntryLine(2, BigDecimal.ZERO, new BigDecimal("100.00")));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("cannot be negative"));
    }

    @Test
    public void testInvalidAccountSelectionFails() {
        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(Date.valueOf("2026-10-15"));
        entry.setParticulars("Zero account ID");

        entry.addLine(new JournalEntryLine(0, new BigDecimal("100.00"), BigDecimal.ZERO));
        entry.addLine(new JournalEntryLine(2, BigDecimal.ZERO, new BigDecimal("100.00")));

        ValidationResult result = service.validate(entry);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("valid account must be selected"));
    }
}
