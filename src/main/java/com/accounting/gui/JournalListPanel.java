package com.accounting.gui;

import com.accounting.dao.JournalEntryDAO;
import com.accounting.model.JournalEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Journal Entries Management Panel matching Vercel Geist aesthetics:
 * - Clean header with "+ New Entry" primary black button.
 * - Form filter toolbar card with uppercase field tags.
 * - Table card with alternating clean rows, right-aligned monetary values, and status pills.
 */
public class JournalListPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JournalEntryDAO journalEntryDAO = new JournalEntryDAO();
    private List<JournalEntry> loadedEntries = new ArrayList<>();

    private JTextField txtSearch;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblGrandTotals;
    private JLabel lblStatusPill;
    private JLabel lblTableSubtitle;

    public JournalListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.APP_BACKGROUND);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        loadEntries();
    }

    private void initComponents() {
        // 1. TOP HEADER & FILTER TOOLBAR
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        // Header Row
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Journal Entries");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Manage transaction vouchers and double-entry general journal records.");
        lblSubtitle.setFont(ThemeManager.FONT_REGULAR);
        lblSubtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(lblTitle);
        titleBox.add(lblSubtitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JButton btnNew = GuiUtils.createPrimaryButton("+ New Entry");
        btnNew.addActionListener(e -> {
            JournalEntryDialog dlg = new JournalEntryDialog(mainFrame, mainFrame.getCurrentUser(), null);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadEntries();
            }
        });
        headerPanel.add(btnNew, BorderLayout.EAST);

        topArea.add(headerPanel);
        topArea.add(Box.createVerticalStrut(18));

        // Filter Toolbar Card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        filterCard.setBackground(ThemeManager.CARD_BACKGROUND);
        filterCard.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

        // Start Date
        JPanel pStart = createFormGroup("START DATE", txtStartDate = new JTextField(9), 110);
        // End Date
        JPanel pEnd = createFormGroup("END DATE", txtEndDate = new JTextField(9), 110);
        // Search
        JPanel pSearch = createFormGroup("SEARCH PARTICULARS / REF", txtSearch = new JTextField(16), 220);

        filterCard.add(pStart);
        filterCard.add(pEnd);
        filterCard.add(pSearch);

        // Buttons
        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pButtons.setOpaque(false);
        pButtons.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton btnFilter = GuiUtils.createSecondaryButton("Filter");
        btnFilter.addActionListener(e -> loadEntries());
        pButtons.add(btnFilter);

        JButton btnReset = GuiUtils.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            txtStartDate.setText("");
            txtEndDate.setText("");
            loadEntries();
        });
        pButtons.add(btnReset);

        filterCard.add(pButtons);
        topArea.add(filterCard);

        add(topArea, BorderLayout.NORTH);

        // 2. CENTER TABLE CARD
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        String[] cols = { "ID", "Date", "Reference #", "Particulars", "Debit Amount (₱)", "Credit Amount (₱)", "Status" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        GuiUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(95);
        table.getColumnModel().getColumn(2).setPreferredWidth(125);
        table.getColumnModel().getColumn(3).setPreferredWidth(320);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        table.getColumnModel().getColumn(4).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int row, int col) {
                boolean balanced = "Balanced".equals(v);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                p.setOpaque(true);
                p.setBackground(isSel ? t.getSelectionBackground() : t.getBackground());
                p.add(GuiUtils.createBadgePill(balanced ? "Balanced" : "Imbalance", balanced));
                return p;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    viewSelected();
                }
            }
        });

        // Header for Table Card
        JPanel tableHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableHeaderRight.setOpaque(false);

        JButton btnView = GuiUtils.createSecondaryButton("View Voucher");
        btnView.addActionListener(e -> viewSelected());

        JButton btnEdit = GuiUtils.createSecondaryButton("Edit Entry");
        btnEdit.addActionListener(e -> editSelected());

        JButton btnDelete = GuiUtils.createDangerButton("Delete Entry");
        btnDelete.addActionListener(e -> deleteSelected());

        tableHeaderRight.add(btnView);
        tableHeaderRight.add(btnEdit);
        tableHeaderRight.add(btnDelete);

        JPanel tableCard = GuiUtils.createTableCard(
                "Transaction Ledger Journal",
                "0 transaction vouchers in record",
                table,
                tableHeaderRight
        );

        centerPanel.add(tableCard, BorderLayout.CENTER);

        // 3. BOTTOM AUDITED TOTALS CARD
        JPanel bottomCard = new JPanel(new BorderLayout());
        bottomCard.setBackground(ThemeManager.CARD_BACKGROUND);
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 20, 14, 20)
        ));

        lblGrandTotals = new JLabel("Audited Totals: Computing...");
        lblGrandTotals.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblGrandTotals.setForeground(ThemeManager.TEXT_PRIMARY);

        lblStatusPill = GuiUtils.createBadgePill("Balanced", true);

        bottomCard.add(lblGrandTotals, BorderLayout.CENTER);
        bottomCard.add(lblStatusPill, BorderLayout.EAST);

        centerPanel.add(bottomCard, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormGroup(String labelText, JTextField field, int width) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ThemeManager.FONT_TINY);
        lbl.setForeground(ThemeManager.MUTED_FOREGROUND);
        p.add(lbl, BorderLayout.NORTH);

        field.setPreferredSize(new Dimension(width, 32));
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    public void refreshData() {
        loadEntries();
    }

    public void loadEntries() {
        String search = txtSearch.getText().trim();
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();

        loadedEntries = journalEntryDAO.findAll(
                start.isEmpty() ? null : start,
                end.isEmpty() ? null : end,
                search.isEmpty() ? null : search
        );

        model.setRowCount(0);
        BigDecimal grandDebit = BigDecimal.ZERO;
        BigDecimal grandCredit = BigDecimal.ZERO;

        for (JournalEntry entry : loadedEntries) {
            grandDebit = grandDebit.add(entry.getTotalDebit());
            grandCredit = grandCredit.add(entry.getTotalCredit());

            model.addRow(new Object[]{
                    entry.getId(),
                    entry.getEntryDate() != null ? entry.getEntryDate().toString() : "—",
                    entry.getReferenceNumber(),
                    entry.getParticulars(),
                    entry.getTotalDebit(),
                    entry.getTotalCredit(),
                    entry.isBalanced() ? "Balanced" : "Unbalanced"
            });
        }

        boolean balanced = grandDebit.compareTo(grandCredit) == 0;
        lblGrandTotals.setText(String.format("Audited Total (%d entries):   Total Debit: %s      Total Credit: %s",
                loadedEntries.size(),
                GuiUtils.formatPeso(grandDebit),
                GuiUtils.formatPeso(grandCredit)));

        if (balanced) {
            lblStatusPill.setText("In Equilibrium");
            lblStatusPill.setBackground(ThemeManager.SUCCESS_BG);
            lblStatusPill.setForeground(ThemeManager.SUCCESS_COLOR);
        } else {
            lblStatusPill.setText("Imbalance Detected");
            lblStatusPill.setBackground(ThemeManager.DANGER_BG);
            lblStatusPill.setForeground(ThemeManager.DANGER_COLOR);
        }
    }

    private JournalEntry getSelectedEntry() {
        int sel = table.getSelectedRow();
        if (sel < 0 || sel >= loadedEntries.size()) {
            JOptionPane.showMessageDialog(this, "Please select an entry from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int entryId = (Integer) model.getValueAt(sel, 0);
        return journalEntryDAO.findById(entryId);
    }

    private void viewSelected() {
        JournalEntry entry = getSelectedEntry();
        if (entry != null) {
            new JournalViewDialog(mainFrame, entry).setVisible(true);
        }
    }

    private void editSelected() {
        JournalEntry entry = getSelectedEntry();
        if (entry != null) {
            JournalEntryDialog dlg = new JournalEntryDialog(mainFrame, mainFrame.getCurrentUser(), entry);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadEntries();
            }
        }
    }

    private void deleteSelected() {
        JournalEntry entry = getSelectedEntry();
        if (entry != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to permanently delete Journal Entry #" + entry.getId() + " (" + entry.getReferenceNumber() + ")?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = journalEntryDAO.deleteEntry(entry.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Journal entry successfully deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEntries();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete entry.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
