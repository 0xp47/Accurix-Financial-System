package com.accounting.gui;

import com.accounting.dao.ReportDAO;
import com.accounting.model.IncomeStatement;
import com.accounting.model.IncomeStatementItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Income Statement Panel matching Vercel Geist aesthetics:
 * - Header with subtitle and Print Report action.
 * - Form filter card for reporting period date bounds.
 * - 3-Card KPI metric row with profit/loss accent indicator.
 * - Two side-by-side Table Cards for Revenues and Operating Expenses.
 * - Bottom summary card with double-underline net operating income.
 */
public class IncomeStatementPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();

    private JTextField startDateField;
    private JTextField endDateField;

    private JLabel totalRevenueLabel;
    private JLabel totalExpenseLabel;
    private JLabel netIncomeStatLabel;
    private JPanel netIncomeCard;

    private DefaultTableModel revenueModel;
    private JTable revenueTable;
    private JLabel revenueSubtotalLabel;

    private DefaultTableModel expenseModel;
    private JTable expenseTable;
    private JLabel expenseSubtotalLabel;

    private JLabel netIncomeBanner;
    private JLabel lblNetIncomePill;

    public IncomeStatementPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.APP_BACKGROUND);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        refreshData();
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

        JLabel title = new JLabel("Income Statement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Statement of profit & loss, operating revenues, and cost allocations.");
        subtitle.setFont(ThemeManager.FONT_REGULAR);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(subtitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JButton printBtn = GuiUtils.createSecondaryButton("Print Report");
        printBtn.setIcon(GuiUtils.createNavIcon("PRINT", false));
        printBtn.setIconTextGap(8);
        printBtn.addActionListener(e -> printReport());
        headerPanel.add(printBtn, BorderLayout.EAST);

        topArea.add(headerPanel);
        topArea.add(Box.createVerticalStrut(18));

        // Filter Card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        filterCard.setBackground(ThemeManager.CARD_BACKGROUND);
        filterCard.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

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

        // Buttons
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pBtn.setOpaque(false);
        pBtn.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton runBtn = GuiUtils.createSecondaryButton("Run Report");
        runBtn.addActionListener(e -> refreshData());
        pBtn.add(runBtn);

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

        // 2. CENTER CONTENT (METRICS ROW + TWO TABLES SPLIT + BOTTOM NET INCOME CARD)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // Metrics Row (1x3)
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        totalRevenueLabel = new JLabel("₱0.00");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalRevenueLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        totalExpenseLabel = new JLabel("₱0.00");
        totalExpenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalExpenseLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        netIncomeStatLabel = new JLabel("₱0.00");
        netIncomeStatLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        netIncomeStatLabel.setForeground(ThemeManager.SUCCESS_COLOR);

        statsRow.add(GuiUtils.createStatCard("TOTAL OPERATING REVENUES", totalRevenueLabel, "Gross operating receipts"));
        statsRow.add(GuiUtils.createStatCard("TOTAL OPERATING EXPENSES", totalExpenseLabel, "Operating disbursements"));

        netIncomeCard = GuiUtils.createStatCard("NET OPERATING INCOME", netIncomeStatLabel, new JLabel("Revenue minus expenses"), ThemeManager.SUCCESS_COLOR);
        statsRow.add(netIncomeCard);

        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Tables Split (1x2)
        JPanel tablesContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesContainer.setOpaque(false);

        // Revenues Table Card
        String[] cols = {"Code", "Account Title", "Amount (₱)"};
        revenueModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        revenueTable = new JTable(revenueModel);
        GuiUtils.styleTable(revenueTable);
        revenueTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        revenueTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        revenueTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        revenueTable.getColumnModel().getColumn(2).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        revenueSubtotalLabel = new JLabel("Subtotal: ₱0.00", JLabel.RIGHT);
        revenueSubtotalLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        revenueSubtotalLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        revenueSubtotalLabel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel revCard = GuiUtils.createTableCard("Operating Revenues", "Inflow from core business operations", revenueTable, null);
        revCard.add(revenueSubtotalLabel, BorderLayout.SOUTH);
        tablesContainer.add(revCard);

        // Expenses Table Card
        expenseModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(expenseModel);
        GuiUtils.styleTable(expenseTable);
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(2).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        expenseSubtotalLabel = new JLabel("Subtotal: ₱0.00", JLabel.RIGHT);
        expenseSubtotalLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        expenseSubtotalLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        expenseSubtotalLabel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel expCard = GuiUtils.createTableCard("Operating Expenses", "Outflows for utilities, services, and payroll", expenseTable, null);
        expCard.add(expenseSubtotalLabel, BorderLayout.SOUTH);
        tablesContainer.add(expCard);

        centerPanel.add(tablesContainer, BorderLayout.CENTER);

        // 3. BOTTOM NET INCOME SUMMARY CARD (Double Underline Style)
        JPanel footerCard = new JPanel(new BorderLayout());
        footerCard.setBackground(ThemeManager.CARD_BACKGROUND);
        footerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel leftSummary = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftSummary.setOpaque(false);

        JLabel lblSummaryTag = new JLabel("NET OPERATING INCOME / (LOSS):");
        lblSummaryTag.setFont(ThemeManager.FONT_TINY);
        lblSummaryTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        leftSummary.add(lblSummaryTag);

        netIncomeBanner = new JLabel("₱0.00");
        netIncomeBanner.setFont(new Font("Segoe UI", Font.BOLD, 18));
        netIncomeBanner.setForeground(ThemeManager.SUCCESS_COLOR);
        netIncomeBanner.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        leftSummary.add(netIncomeBanner);

        footerCard.add(leftSummary, BorderLayout.WEST);

        lblNetIncomePill = GuiUtils.createPill("Net Profit", "revenue");
        footerCard.add(lblNetIncomePill, BorderLayout.EAST);

        centerPanel.add(footerCard, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();

        IncomeStatement is = reportDAO.generateIncomeStatement(start, end);

        revenueModel.setRowCount(0);
        for (IncomeStatementItem item : is.getRevenues()) {
            revenueModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAmount()
            });
        }
        revenueSubtotalLabel.setText("Total Revenues: " + GuiUtils.formatPeso(is.getTotalRevenue()));
        totalRevenueLabel.setText(GuiUtils.formatPeso(is.getTotalRevenue()));

        expenseModel.setRowCount(0);
        for (IncomeStatementItem item : is.getExpenses()) {
            expenseModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAmount()
            });
        }
        expenseSubtotalLabel.setText("Total Expenses: " + GuiUtils.formatPeso(is.getTotalExpense()));
        totalExpenseLabel.setText(GuiUtils.formatPeso(is.getTotalExpense()));

        BigDecimal net = is.getNetIncome();
        boolean isProfit = net.compareTo(BigDecimal.ZERO) >= 0;
        Color c = isProfit ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR;

        netIncomeStatLabel.setText(GuiUtils.formatPeso(net));
        netIncomeStatLabel.setForeground(c);

        netIncomeBanner.setText(GuiUtils.formatPeso(net));
        netIncomeBanner.setForeground(c);

        lblNetIncomePill.setText(isProfit ? "Net Profit" : "Net Loss");
        lblNetIncomePill.setForeground(c);
        lblNetIncomePill.setBackground(isProfit ? ThemeManager.SUCCESS_BG : ThemeManager.DANGER_BG);

        netIncomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 3, 0, 0, c),
                        BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true)
                ),
                new EmptyBorder(18, 20, 18, 20)
        ));
    }

    public void printReport() {
        try {
            MessageFormat header = new MessageFormat("Accurix Financial — Statement of Profit & Loss");
            MessageFormat footer = new MessageFormat("Page {0,number,integer}   •   Income Statement");
            revenueTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize printer: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
