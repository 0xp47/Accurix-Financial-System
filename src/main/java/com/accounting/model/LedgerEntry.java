package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Represents a single transaction posting in the General Ledger.
 */
public class LedgerEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int lineId;
    private int journalEntryId;
    private String referenceNumber;
    private Date entryDate;
    private String particulars;
    private BigDecimal debit = BigDecimal.ZERO;
    private BigDecimal credit = BigDecimal.ZERO;
    private BigDecimal runningBalance = BigDecimal.ZERO;

    public LedgerEntry() {
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(int journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = (debit != null) ? debit : BigDecimal.ZERO;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = (credit != null) ? credit : BigDecimal.ZERO;
    }

    public BigDecimal getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = (runningBalance != null) ? runningBalance : BigDecimal.ZERO;
    }

    public boolean isDebitPositive() {
        return debit != null && debit.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCreditPositive() {
        return credit != null && credit.compareTo(BigDecimal.ZERO) > 0;
    }
}
