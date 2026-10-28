package com.accounting.service;

import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service enforcing double-entry bookkeeping validation rules.
 */
public class JournalValidationService {

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Validates a complete journal entry and its lines against accounting standards.
     *
     * @param entry journal entry to validate
     * @return ValidationResult indicating success or failure with message
     */
    public ValidationResult validate(JournalEntry entry) {
        if (entry == null) {
            return ValidationResult.failure("Journal entry cannot be null.");
        }

        // 1. Date validation
        if (entry.getEntryDate() == null) {
            return ValidationResult.failure("Entry date is required.");
        }

        // 2. Particulars validation
        if (entry.getParticulars() == null || entry.getParticulars().trim().isEmpty()) {
            return ValidationResult.failure("Particulars / description cannot be blank.");
        }

        List<JournalEntryLine> lines = entry.getLines();

        // 3. Line count validation
        if (lines == null || lines.size() < 2) {
            return ValidationResult.failure("A journal entry must contain at least two account lines.");
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        int debitCount = 0;
        int creditCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            JournalEntryLine line = lines.get(i);
            int rowNum = i + 1;

            if (line.getAccountId() <= 0) {
                return ValidationResult.failure("Line " + rowNum + ": A valid account must be selected.");
            }

            BigDecimal debit = (line.getDebit() != null) ? line.getDebit() : BigDecimal.ZERO;
            BigDecimal credit = (line.getCredit() != null) ? line.getCredit() : BigDecimal.ZERO;

            if (debit.compareTo(BigDecimal.ZERO) < 0 || credit.compareTo(BigDecimal.ZERO) < 0) {
                return ValidationResult.failure("Line " + rowNum + ": Amounts cannot be negative.");
            }

            boolean hasDebit = debit.compareTo(BigDecimal.ZERO) > 0;
            boolean hasCredit = credit.compareTo(BigDecimal.ZERO) > 0;

            if (hasDebit && hasCredit) {
                return ValidationResult.failure("Line " + rowNum + ": A single line cannot have both Debit and Credit amounts.");
            }

            if (!hasDebit && !hasCredit) {
                return ValidationResult.failure("Line " + rowNum + ": Please enter a Debit or Credit amount.");
            }

            if (hasDebit) {
                totalDebit = totalDebit.add(debit);
                debitCount++;
            }
            if (hasCredit) {
                totalCredit = totalCredit.add(credit);
                creditCount++;
            }
        }

        if (debitCount == 0 || creditCount == 0) {
            return ValidationResult.failure("A valid journal entry must contain at least one Debit and at least one Credit line.");
        }

        if (totalDebit.compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Total entry amount must be greater than zero.");
        }

        // 4. Double-Entry Core Rule: Total Debit = Total Credit
        if (totalDebit.compareTo(totalCredit) != 0) {
            BigDecimal diff = totalDebit.subtract(totalCredit).abs();
            return ValidationResult.failure(String.format(
                "Unbalanced entry: Total Debit (₱%.2f) must equal Total Credit (₱%.2f). Difference is ₱%.2f.",
                totalDebit, totalCredit, diff
            ));
        }

        return ValidationResult.success();
    }
}
