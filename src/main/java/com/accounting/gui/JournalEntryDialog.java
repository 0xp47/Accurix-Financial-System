package com.accounting.gui;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.JournalEntryDAO;
import com.accounting.model.Account;
import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;
import com.accounting.model.User;
import com.accounting.service.JournalValidationService;
import com.accounting.service.JournalValidationService.ValidationResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic Multi-Line Journal Entry Creation & Edit Dialog with live validation.
 */
public class JournalEntryDialog extends JDialog {

    private final JournalEntryDAO journalEntryDAO = new JournalEntryDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final JournalValidationService validationService = new JournalValidationService();

    private final User currentUser;
    private final JournalEntry existingEntry;
    private boolean saved = false;

    private JTextField txtDate;
    private JTextField txtRef;
    private JTextField txtParticulars;
    private JTable linesTable;
    private DefaultTableModel linesModel;
    private JLabel lblTotalDebit;
    private JLabel lblTotalCredit;
    private JLabel lblStatus;
    private JButton btnSave;

    private final List<Account> activeAccounts;
    private final Map<String, Account> accountMap = new HashMap<>();

    public JournalEntryDialog(JFrame parent, User user, JournalEntry entryToEdit) {
        super(parent, (entryToEdit == null ? "New Journal Entry" : "Edit Journal Entry #" + entryToEdit.getId()), true);
        this.currentUser = user;
        this.existingEntry = entryToEdit;

        this.activeAccounts = accountDAO.findAllActive();
        for (Account a : activeAccounts) {
            accountMap.put(a.getAccountCode() + " - " + a.getAccountName(), a);
        }

        initComponents();
        populateData();
        recalculateTotals();
    }

    private void initComponents() {
        setSize(820, 600);
        setLocationRelativeTo(getParent());
        GuiUtils.setWindowIcons(this);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(22, 24, 22, 24));
        root.setBackground(ThemeManager.APP_BACKGROUND);

        // Top Form Card
        JPanel topForm = GuiUtils.createCard();
        topForm.setLayout(new BoxLayout(topForm, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(existingEntry == null ? "Create Balanced Journal Entry" : "Edit Journal Entry #" + existingEntry.getId());
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        topForm.add(lblTitle);
        topForm.add(Box.createVerticalStrut(12));

        JPanel fieldsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        fieldsRow.setOpaque(false);

        // Date
        JPanel pDate = new JPanel(new BorderLayout(0, 4));
        pDate.setOpaque(false);
        JLabel lblDateTag = new JLabel("Date (yyyy-MM-dd) *");
        lblDateTag.setFont(ThemeManager.FONT_TINY);
        lblDateTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        pDate.add(lblDateTag, BorderLayout.NORTH);
        txtDate = new JTextField(LocalDate.now().toString(), 10);
        txtDate.setPreferredSize(new Dimension(110, 32));
        pDate.add(txtDate, BorderLayout.CENTER);
        fieldsRow.add(pDate);

        // Reference
        JPanel pRef = new JPanel(new BorderLayout(0, 4));
        pRef.setOpaque(false);
        JLabel lblRefTag = new JLabel("Reference #");
        lblRefTag.setFont(ThemeManager.FONT_TINY);
        lblRefTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        pRef.add(lblRefTag, BorderLayout.NORTH);
        txtRef = new JTextField(12);
        txtRef.setPreferredSize(new Dimension(130, 32));
        pRef.add(txtRef, BorderLayout.CENTER);
        fieldsRow.add(pRef);

        // Particulars
        JPanel pPart = new JPanel(new BorderLayout(0, 4));
        pPart.setOpaque(false);
        JLabel lblPartTag = new JLabel("Particulars / Explanation *");
        lblPartTag.setFont(ThemeManager.FONT_TINY);
        lblPartTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        pPart.add(lblPartTag, BorderLayout.NORTH);
        txtParticulars = new JTextField(26);
        txtParticulars.setPreferredSize(new Dimension(280, 32));
        pPart.add(txtParticulars, BorderLayout.CENTER);
        fieldsRow.add(pPart);

        topForm.add(fieldsRow);
        root.add(topForm, BorderLayout.NORTH);

        // Center: Dynamic Lines Table Card
        JPanel tableCard = GuiUtils.createCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        // Table toolbar (+ Add Line, - Remove Line)
        JPanel tableToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tableToolbar.setOpaque(false);

        JButton btnAddLine = GuiUtils.createSecondaryButton("+ Add Posting Line");
        btnAddLine.addActionListener(e -> linesModel.addRow(new Object[]{ "-- Select Account --", "0.00", "0.00" }));

        JButton btnRemoveLine = GuiUtils.createSecondaryButton("Remove Selected Line");
        btnRemoveLine.addActionListener(e -> {
            int sel = linesTable.getSelectedRow();
            if (sel >= 0 && linesModel.getRowCount() > 2) {
                linesModel.removeRow(sel);
            } else if (linesModel.getRowCount() <= 2) {
                JOptionPane.showMessageDialog(this, "A double-entry transaction must contain at least 2 lines.", "Validation Rule", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        tableToolbar.add(btnAddLine);
        tableToolbar.add(btnRemoveLine);
        tableCard.add(tableToolbar, BorderLayout.NORTH);

        String[] cols = { "Account Title *", "Debit Amount (₱)", "Credit Amount (₱)" };
        linesModel = new DefaultTableModel(cols, 0);
        linesTable = new JTable(linesModel);
        GuiUtils.styleTable(linesTable);

        // Setup Account ComboBox editor
        JComboBox<String> accountCombo = new JComboBox<>();
        accountCombo.addItem("-- Select Account --");
        for (Account a : activeAccounts) {
            accountCombo.addItem(a.getAccountCode() + " - " + a.getAccountName());
        }
        linesTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(accountCombo));
        linesTable.getColumnModel().getColumn(0).setPreferredWidth(380);
        linesTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        linesTable.getColumnModel().getColumn(2).setPreferredWidth(160);

        linesModel.addTableModelListener((TableModelEvent e) -> recalculateTotals());

        JScrollPane scrollPane = new JScrollPane(linesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        root.add(tableCard, BorderLayout.CENTER);

        // Bottom Totals & Actions Bar
        JPanel bottomCard = GuiUtils.createCard();
        bottomCard.setLayout(new BorderLayout());

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        totalsPanel.setOpaque(false);

        lblTotalDebit = new JLabel("Total Debit: ₱0.00");
        lblTotalDebit.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblTotalDebit.setForeground(ThemeManager.TEXT_PRIMARY);

        lblTotalCredit = new JLabel("Total Credit: ₱0.00");
        lblTotalCredit.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblTotalCredit.setForeground(ThemeManager.TEXT_PRIMARY);

        lblStatus = new JLabel("Status: Calculating...");
        lblStatus.setFont(ThemeManager.FONT_REGULAR_BOLD);

        totalsPanel.add(lblTotalDebit);
        totalsPanel.add(lblTotalCredit);
        totalsPanel.add(lblStatus);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton btnCancel = GuiUtils.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        btnSave = GuiUtils.createPrimaryButton("Save Journal Entry");
        btnSave.addActionListener(e -> saveEntry());

        actionsPanel.add(btnCancel);
        actionsPanel.add(btnSave);

        bottomCard.add(totalsPanel, BorderLayout.WEST);
        bottomCard.add(actionsPanel, BorderLayout.EAST);

        root.add(bottomCard, BorderLayout.SOUTH);

        getContentPane().add(root);
    }

    private void populateData() {
        if (existingEntry != null) {
            txtDate.setText(existingEntry.getEntryDate() != null ? existingEntry.getEntryDate().toString() : "");
            txtRef.setText(existingEntry.getReferenceNumber());
            txtParticulars.setText(existingEntry.getParticulars());

            for (JournalEntryLine line : existingEntry.getLines()) {
                String accountDisplay = line.getAccountCode() + " - " + line.getAccountName();
                linesModel.addRow(new Object[]{
                        accountDisplay,
                        line.getDebit() != null ? line.getDebit().toPlainString() : "0.00",
                        line.getCredit() != null ? line.getCredit().toPlainString() : "0.00"
                });
            }
        } else {
            // Initial 2 lines
            linesModel.addRow(new Object[]{ "-- Select Account --", "0.00", "0.00" });
            linesModel.addRow(new Object[]{ "-- Select Account --", "0.00", "0.00" });
        }
    }

    private void recalculateTotals() {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (int i = 0; i < linesModel.getRowCount(); i++) {
            totalDebit = totalDebit.add(parseAmount(linesModel.getValueAt(i, 1)));
            totalCredit = totalCredit.add(parseAmount(linesModel.getValueAt(i, 2)));
        }

        lblTotalDebit.setText("Total Debit: " + GuiUtils.formatPeso(totalDebit));
        lblTotalCredit.setText("Total Credit: " + GuiUtils.formatPeso(totalCredit));

        BigDecimal diff = totalDebit.subtract(totalCredit).abs();
        boolean balanced = (diff.compareTo(new BigDecimal("0.001")) < 0) && (totalDebit.compareTo(BigDecimal.ZERO) > 0);

        if (balanced) {
            lblStatus.setText("In Balance (" + GuiUtils.formatPeso(totalDebit) + ")");
            lblStatus.setForeground(ThemeManager.SUCCESS_COLOR);
            btnSave.setEnabled(true);
        } else {
            lblStatus.setText("Imbalance: " + GuiUtils.formatPeso(diff));
            lblStatus.setForeground(ThemeManager.DANGER_COLOR);
            btnSave.setEnabled(false);
        }
    }

    private BigDecimal parseAmount(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        try {
            String str = obj.toString().replace("₱", "").replace(",", "").trim();
            if (str.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(str);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void saveEntry() {
        String dateStr = txtDate.getText().trim();
        String particulars = txtParticulars.getText().trim();
        String ref = txtRef.getText().trim();

        Date entryDate;
        try {
            entryDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JournalEntry entry = (existingEntry != null) ? existingEntry : new JournalEntry();
        entry.setEntryDate(entryDate);
        entry.setParticulars(particulars);
        entry.setReferenceNumber(ref);
        if (currentUser != null) {
            entry.setCreatedBy(currentUser.getId());
        }

        List<JournalEntryLine> lines = new ArrayList<>();
        for (int i = 0; i < linesModel.getRowCount(); i++) {
            String accStr = String.valueOf(linesModel.getValueAt(i, 0));
            Account acc = accountMap.get(accStr);
            if (acc == null) continue;

            BigDecimal deb = parseAmount(linesModel.getValueAt(i, 1));
            BigDecimal cred = parseAmount(linesModel.getValueAt(i, 2));

            if (deb.compareTo(BigDecimal.ZERO) > 0 || cred.compareTo(BigDecimal.ZERO) > 0) {
                JournalEntryLine line = new JournalEntryLine(acc.getId(), deb, cred);
                line.setLineOrder(i + 1);
                lines.add(line);
            }
        }
        entry.setLines(lines);

        ValidationResult vr = validationService.validate(entry);
        if (!vr.isValid()) {
            JOptionPane.showMessageDialog(this, vr.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok;
        if (existingEntry != null) {
            ok = journalEntryDAO.updateEntry(entry);
        } else {
            int newId = journalEntryDAO.createEntry(entry);
            ok = (newId > 0);
        }

        if (ok) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Journal entry successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Database transaction failed. Please check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
