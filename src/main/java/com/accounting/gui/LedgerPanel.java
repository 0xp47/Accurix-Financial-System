package com.accounting.gui;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.JournalEntryDAO;
import com.accounting.dao.LedgerDAO;
import com.accounting.model.Account;
import com.accounting.model.AccountLedger;
import com.accounting.model.JournalEntry;
import com.accounting.model.LedgerEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

/**
 * General Ledger Panel matching Vercel Geist aesthetics:
 * - Header with subtitle and Print Ledger action.
 * - Dedicated filter card for account selection and date bounds.
 * - 4 Stat Cards row for beginning/ending running balance metrics.
 * - Table card containing chronologically sorted account postings.
 */
public class LedgerPanel extends JPanel {

    private final AccountDAO accountDAO = new AccountDAO();
    private final LedgerDAO ledgerDAO = new LedgerDAO();
    private final JournalEntryDAO journalDAO = new JournalEntryDAO();

    private JComboBox<Account> accountComboBox;
    private JTextField startDateField;
    private JTextField endDateField;

    private JLabel beginningBalanceLabel;
    private JLabel periodDebitLabel;
    private JLabel periodCreditLabel;
    private JLabel endingBalanceLabel;
    private JPanel endingBalanceCard;

    private JTable ledgerTable;
    private DefaultTableModel tableModel;
    private AccountLedger currentLedger;

    public LedgerPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.APP_BACKGROUND);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        loadAccounts();
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

        JLabel title = new JLabel("General Ledger");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Individual account histories, debit and credit postings, and running balances.");
        subtitle.setFont(ThemeManager.FONT_REGULAR);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(subtitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JButton printBtn = GuiUtils.createSecondaryButton("Print Ledger");
        printBtn.setIcon(GuiUtils.createNavIcon("PRINT", false));
        printBtn.setIconTextGap(8);
        printBtn.addActionListener(e -> printLedger());
        headerPanel.add(printBtn, BorderLayout.EAST);

        topArea.add(headerPanel);
        topArea.add(Box.createVerticalStrut(18));

        // Filter Card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        filterCard.setBackground(ThemeManager.CARD_BACKGROUND);
        filterCard.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

        // Account Selector
        JPanel pAccount = new JPanel(new BorderLayout(0, 4));
        pAccount.setOpaque(false);
        JLabel lblAcc = new JLabel("SELECT ACCOUNT *");
        lblAcc.setFont(ThemeManager.FONT_TINY);
        lblAcc.setForeground(ThemeManager.MUTED_FOREGROUND);
        pAccount.add(lblAcc, BorderLayout.NORTH);

        accountComboBox = new JComboBox<>();
        accountComboBox.setPreferredSize(new Dimension(260, 32));
        accountComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Account) {
                    Account a = (Account) value;
                    setText(a.getAccountCode() + " — " + a.getAccountName() + " (" + a.getAccountType().getDisplayName() + ")");
                }
                return this;
            }
        });
        accountComboBox.addActionListener(e -> refreshData());
        pAccount.add(accountComboBox, BorderLayout.CENTER);
        filterCard.add(pAccount);

        // From Date
        JPanel pFrom = new JPanel(new BorderLayout(0, 4));
        pFrom.setOpaque(false);
        JLabel lblFrom = new JLabel("FROM DATE");
        lblFrom.setFont(ThemeManager.FONT_TINY);
        lblFrom.setForeground(ThemeManager.MUTED_FOREGROUND);
        pFrom.add(lblFrom, BorderLayout.NORTH);
        startDateField = new JTextField(9);
        startDateField.setPreferredSize(new Dimension(105, 32));
        pFrom.add(startDateField, BorderLayout.CENTER);
        filterCard.add(pFrom);

        // To Date
        JPanel pTo = new JPanel(new BorderLayout(0, 4));
        pTo.setOpaque(false);
        JLabel lblTo = new JLabel("TO DATE");
        lblTo.setFont(ThemeManager.FONT_TINY);
        lblTo.setForeground(ThemeManager.MUTED_FOREGROUND);
        pTo.add(lblTo, BorderLayout.NORTH);
        endDateField = new JTextField(9);
        endDateField.setPreferredSize(new Dimension(105, 32));
        pTo.add(endDateField, BorderLayout.CENTER);
        filterCard.add(pTo);

        // Filter & Reset Buttons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pBtn.setOpaque(false);
        pBtn.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton filterBtn = GuiUtils.createSecondaryButton("Filter");
        filterBtn.addActionListener(e -> refreshData());
        pBtn.add(filterBtn);

        JButton resetBtn = GuiUtils.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            startDateField.setText("");
            endDateField.setText("");
            refreshData();
        });
        pBtn.add(resetBtn);

        filterCard.add(pBtn);
        topArea.add(filterCard);

        add(topArea, BorderLayout.NORTH);

        // 2. CENTER CONTENT (METRICS ROW + TABLE CARD)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // KPI 4-Card Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        beginningBalanceLabel = new JLabel("₱0.00");
        beginningBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        beginningBalanceLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        periodDebitLabel = new JLabel("₱0.00");
        periodDebitLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        periodDebitLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        periodCreditLabel = new JLabel("₱0.00");
        periodCreditLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        periodCreditLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        endingBalanceLabel = new JLabel("₱0.00");
        endingBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        endingBalanceLabel.setForeground(ThemeManager.SUCCESS_COLOR);

        statsRow.add(GuiUtils.createStatCard("BEGINNING BALANCE", beginningBalanceLabel, "Carried forward balance"));
        statsRow.add(GuiUtils.createStatCard("PERIOD TOTAL DEBITS", periodDebitLabel, "Period debit activity"));
        statsRow.add(GuiUtils.createStatCard("PERIOD TOTAL CREDITS", periodCreditLabel, "Period credit activity"));

        endingBalanceCard = GuiUtils.createStatCard("ENDING BALANCE", endingBalanceLabel, new JLabel("Net running balance"), ThemeManager.SUCCESS_COLOR);
        statsRow.add(endingBalanceCard);

        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Reference #", "Particulars", "Debit (₱)", "Credit (₱)", "Running Balance (₱)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ledgerTable = new JTable(tableModel);
        GuiUtils.styleTable(ledgerTable);

        ledgerTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        ledgerTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        ledgerTable.getColumnModel().getColumn(2).setPreferredWidth(340);
        ledgerTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        ledgerTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        ledgerTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        ledgerTable.getColumnModel().getColumn(3).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        ledgerTable.getColumnModel().getColumn(4).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        ledgerTable.getColumnModel().getColumn(5).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        ledgerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ledgerTable.getSelectedRow() >= 0 && currentLedger != null) {
                    int row = ledgerTable.getSelectedRow();
                    if (row < currentLedger.getEntries().size()) {
                        LedgerEntry le = currentLedger.getEntries().get(row);
                        JournalEntry je = journalDAO.findById(le.getJournalEntryId());
                        if (je != null) {
                            Window parentWindow = SwingUtilities.getWindowAncestor(LedgerPanel.this);
                            new JournalViewDialog((Frame) parentWindow, je).setVisible(true);
                        }
                    }
                }
            }
        });

        JPanel tableCard = GuiUtils.createTableCard(
                "General Ledger Postings",
                "Chronological account activity with running balance calculation",
                ledgerTable,
                null
        );

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadAccounts() {
        List<Account> accounts = accountDAO.findAllActive();
        accountComboBox.removeAllItems();
        for (Account a : accounts) {
            accountComboBox.addItem(a);
        }
        if (!accounts.isEmpty()) {
            accountComboBox.setSelectedIndex(0);
        }
    }

    public void refreshData() {
        Account selected = (Account) accountComboBox.getSelectedItem();
        if (selected == null) return;

        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();

        currentLedger = ledgerDAO.getAccountLedger(
                selected.getId(),
                start.isEmpty() ? null : start,
                end.isEmpty() ? null : end
        );

        beginningBalanceLabel.setText(GuiUtils.formatPeso(currentLedger.getBeginningBalance()));
        periodDebitLabel.setText(GuiUtils.formatPeso(currentLedger.getTotalDebit()));
        periodCreditLabel.setText(GuiUtils.formatPeso(currentLedger.getTotalCredit()));
        endingBalanceLabel.setText(GuiUtils.formatPeso(currentLedger.getEndingBalance()));

        tableModel.setRowCount(0);
        for (LedgerEntry entry : currentLedger.getEntries()) {
            tableModel.addRow(new Object[]{
                    entry.getEntryDate() != null ? entry.getEntryDate().toString() : "—",
                    entry.getReferenceNumber(),
                    entry.getParticulars(),
                    entry.getDebit(),
                    entry.getCredit(),
                    entry.getRunningBalance()
            });
        }
    }

    public void printLedger() {
        Account acc = (Account) accountComboBox.getSelectedItem();
        String accName = acc != null ? acc.getAccountCode() + " - " + acc.getAccountName() : "Account";
        try {
            MessageFormat header = new MessageFormat("Accurix Financial — General Ledger: " + accName);
            MessageFormat footer = new MessageFormat("Page {0,number,integer}   •   Enterprise Ledger Statement");
            ledgerTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize printer: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
