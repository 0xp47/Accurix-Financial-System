package com.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Journal entry header entity containing transactional metadata and lines.
 */
public class JournalEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date entryDate;
    private String particulars;
    private String referenceNumber;
    private Integer createdBy;
    private String creatorName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<JournalEntryLine> lines = new ArrayList<>();

    public JournalEntry() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<JournalEntryLine> getLines() {
        return lines;
    }

    public void setLines(List<JournalEntryLine> lines) {
        this.lines = (lines != null) ? lines : new ArrayList<>();
    }

    public void addLine(JournalEntryLine line) {
        if (line != null) {
            this.lines.add(line);
        }
    }

    /**
     * Calculates the total debit amount across all lines in this entry.
     *
     * @return sum of debits
     */
    public BigDecimal getTotalDebit() {
        BigDecimal total = BigDecimal.ZERO;
        for (JournalEntryLine line : lines) {
            if (line.getDebit() != null) {
                total = total.add(line.getDebit());
            }
        }
        return total;
    }

    /**
     * Calculates the total credit amount across all lines in this entry.
     *
     * @return sum of credits
     */
    public BigDecimal getTotalCredit() {
        BigDecimal total = BigDecimal.ZERO;
        for (JournalEntryLine line : lines) {
            if (line.getCredit() != null) {
                total = total.add(line.getCredit());
            }
        }
        return total;
    }

    /**
     * Invariant check: Total Debit must equal Total Credit.
     *
     * @return true if balanced
     */
    public boolean isBalanced() {
        return getTotalDebit().compareTo(getTotalCredit()) == 0;
    }

    /**
     * Calculates absolute difference between total debit and total credit.
     *
     * @return absolute difference
     */
    public BigDecimal getDifference() {
        return getTotalDebit().subtract(getTotalCredit()).abs();
    }
}
