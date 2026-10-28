package com.accounting.model;

import java.math.BigDecimal;

/**
 * Standard account classifications and their accounting balance rules.
 */
public enum AccountType {
    ASSET("Asset", true),
    LIABILITY("Liability", false),
    EQUITY("Equity", false),
    REVENUE("Revenue", false),
    EXPENSE("Expense", true);

    private final String displayName;
    private final boolean normalDebit;

    AccountType(String displayName, boolean normalDebit) {
        this.displayName = displayName;
        this.normalDebit = normalDebit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNormalDebit() {
        return normalDebit;
    }

    public boolean isNormalCredit() {
        return !normalDebit;
    }

    /**
     * Calculates the account's ending balance according to accounting standards:
     * - Normal Debit (Asset, Expense): Debit - Credit
     * - Normal Credit (Liability, Equity, Revenue): Credit - Debit
     *
     * @param totalDebit  sum of debits
     * @param totalCredit sum of credits
     * @return calculated net balance
     */
    public BigDecimal calculateBalance(BigDecimal totalDebit, BigDecimal totalCredit) {
        BigDecimal debit = (totalDebit != null) ? totalDebit : BigDecimal.ZERO;
        BigDecimal credit = (totalCredit != null) ? totalCredit : BigDecimal.ZERO;

        if (normalDebit) {
            return debit.subtract(credit);
        } else {
            return credit.subtract(debit);
        }
    }

    public static AccountType fromString(String text) {
        if (text == null) {
            return null;
        }
        for (AccountType type : AccountType.values()) {
            if (type.name().equalsIgnoreCase(text.trim()) || type.displayName.equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown account type: " + text);
    }
}
