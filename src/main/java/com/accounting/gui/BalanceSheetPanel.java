package com.accounting.gui;

import com.accounting.dao.ReportDAO;
import com.accounting.model.BalanceSheet;
import com.accounting.model.BalanceSheetItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Balance Sheet Panel matching Vercel Geist aesthetics:
 * - Header with subtitle and Print Report action.
 * - Form filter card for cut-off as-of date selection.
 * - Fundamental Accounting Equation (Assets = Liabilities + Equity) equilibrium alert.
 * - 3-Card KPI metric row for high-level position overview.
 * - Split Table Cards for Assets and Liabilities & Equity.
 * - Double-underline fundamental equation balance verification card.
 */
public class BalanceSheetPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();

    private JTextField asOfDateField;
    private JPanel bannerContainer;

    private JLabel totalAssetsStatLabel;
    private JLabel totalLiabilitiesStatLabel;
    private JLabel totalEquityStatLabel;

    private DefaultTableModel assetsModel;
    private JTable assetsTable;
    private JLabel assetsSubtotalLabel;

    private DefaultTableModel liabilitiesModel;
    private JTable liabilitiesTable;
    private JLabel liabilitiesSubtotalLabel;

    private DefaultTableModel equityModel;
    private JTable equityTable;
    private JLabel equitySubtotalLabel;

    private JLabel totalLiabEquitySummaryLabel;
    private JLabel totalAssetsSummaryLabel;
    private JLabel statusPill;

    public BalanceSheetPanel() {
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

        JLabel title = new JLabel("Balance Sheet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Statement of financial position verifying Assets = Liabilities + Equity.");
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

        JPanel pDate = new JPanel(new BorderLayout(0, 4));
        pDate.setOpaque(false);
        JLabel lblDate = new JLabel("CUT-OFF DATE (AS OF)");
        lblDate.setFont(ThemeManager.FONT_TINY);
        lblDate.setForeground(ThemeManager.MUTED_FOREGROUND);
        pDate.add(lblDate, BorderLayout.NORTH);

        asOfDateField = new JTextField(10);
        asOfDateField.setPreferredSize(new Dimension(120, 32));
        pDate.add(asOfDateField, BorderLayout.CENTER);
        filterCard.add(pDate);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pBtn.setOpaque(false);
        pBtn.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton runBtn = GuiUtils.createSecondaryButton("Apply Date");
        runBtn.addActionListener(e -> refreshData());
        pBtn.add(runBtn);

        JButton resetBtn = GuiUtils.createSecondaryButton("Current");
        resetBtn.addActionListener(e -> {
            asOfDateField.setText("");
            refreshData();
        });
        pBtn.add(resetBtn);

        filterCard.add(pBtn);
        topArea.add(filterCard);
        topArea.add(Box.createVerticalStrut(18));

        // Equation Banner Container
        bannerContainer = new JPanel(new BorderLayout());
        bannerContainer.setOpaque(false);
        topArea.add(bannerContainer);

        add(topArea, BorderLayout.NORTH);

        // 2. CENTER CONTENT (METRICS ROW + SPLIT TABLES + BOTTOM SUMMARY CARD)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // Metrics Row (1x3)
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        totalAssetsStatLabel = new JLabel("₱0.00");
        totalAssetsStatLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalAssetsStatLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        totalLiabilitiesStatLabel = new JLabel("₱0.00");
        totalLiabilitiesStatLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLiabilitiesStatLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        totalEquityStatLabel = new JLabel("₱0.00");
        totalEquityStatLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalEquityStatLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        statsRow.add(GuiUtils.createStatCard("TOTAL ASSETS", totalAssetsStatLabel, "Economic resources owned"));
        statsRow.add(GuiUtils.createStatCard("TOTAL LIABILITIES", totalLiabilitiesStatLabel, "External creditor obligations"));
        statsRow.add(GuiUtils.createStatCard("TOTAL OWNER'S EQUITY", totalEquityStatLabel, "Residual business net worth"));

        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Split Tables (Left: Assets, Right: Liabilities & Equity)
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);

        String[] cols = {"Code", "Account Title", "Amount (₱)"};

        // LEFT: Assets Table Card
        assetsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        assetsTable = new JTable(assetsModel);
        GuiUtils.styleTable(assetsTable);
        assetsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        assetsTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        assetsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        assetsTable.getColumnModel().getColumn(2).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        assetsSubtotalLabel = new JLabel("Total Assets: ₱0.00", JLabel.RIGHT);
        assetsSubtotalLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        assetsSubtotalLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        assetsSubtotalLabel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel assetsCard = GuiUtils.createTableCard("Assets", "Current and non-current balance sheet holdings", assetsTable, null);
        assetsCard.add(assetsSubtotalLabel, BorderLayout.SOUTH);
        splitPanel.add(assetsCard);

        // RIGHT: Liabilities and Equity Stacked
        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 16));
        rightCol.setOpaque(false);

        // Liabilities Card
        liabilitiesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        liabilitiesTable = new JTable(liabilitiesModel);
        GuiUtils.styleTable(liabilitiesTable);
        liabilitiesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        liabilitiesTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        liabilitiesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        liabilitiesTable.getColumnModel().getColumn(2).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        liabilitiesSubtotalLabel = new JLabel("Total Liabilities: ₱0.00", JLabel.RIGHT);
        liabilitiesSubtotalLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        liabilitiesSubtotalLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        liabilitiesSubtotalLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        JPanel liabCard = GuiUtils.createTableCard("Liabilities", "Short-term payables and long-term debt", liabilitiesTable, null);
        liabCard.add(liabilitiesSubtotalLabel, BorderLayout.SOUTH);
        rightCol.add(liabCard);

        // Equity Card
        equityModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        equityTable = new JTable(equityModel);
        GuiUtils.styleTable(equityTable);
        equityTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        equityTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        equityTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        equityTable.getColumnModel().getColumn(2).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        equitySubtotalLabel = new JLabel("Total Equity: ₱0.00", JLabel.RIGHT);
        equitySubtotalLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        equitySubtotalLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        equitySubtotalLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        JPanel equityCard = GuiUtils.createTableCard("Owner's Equity", "Capital, drawings, and accumulated retained earnings", equityTable, null);
        equityCard.add(equitySubtotalLabel, BorderLayout.SOUTH);
        rightCol.add(equityCard);

        splitPanel.add(rightCol);
        centerPanel.add(splitPanel, BorderLayout.CENTER);

        // 3. BOTTOM ACCOUNTING EQUATION EQUALITY CARD
        JPanel footerCard = new JPanel(new BorderLayout());
        footerCard.setBackground(ThemeManager.CARD_BACKGROUND);
        footerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        totalsPanel.setOpaque(false);

        JPanel pAssets = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pAssets.setOpaque(false);
        JLabel lblAssetTag = new JLabel("TOTAL ASSETS:");
        lblAssetTag.setFont(ThemeManager.FONT_TINY);
        lblAssetTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        totalAssetsSummaryLabel = new JLabel("₱0.00");
        totalAssetsSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalAssetsSummaryLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        totalAssetsSummaryLabel.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        pAssets.add(lblAssetTag);
        pAssets.add(totalAssetsSummaryLabel);
        totalsPanel.add(pAssets);

        JPanel pLiabEq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pLiabEq.setOpaque(false);
        JLabel lblLiabEqTag = new JLabel("TOTAL LIABILITIES & EQUITY:");
        lblLiabEqTag.setFont(ThemeManager.FONT_TINY);
        lblLiabEqTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        totalLiabEquitySummaryLabel = new JLabel("₱0.00");
        totalLiabEquitySummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLiabEquitySummaryLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        totalLiabEquitySummaryLabel.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        pLiabEq.add(lblLiabEqTag);
        pLiabEq.add(totalLiabEquitySummaryLabel);
        totalsPanel.add(pLiabEq);

        footerCard.add(totalsPanel, BorderLayout.WEST);

        statusPill = GuiUtils.createBadgePill("Equation Balanced", true);
        footerCard.add(statusPill, BorderLayout.EAST);

        centerPanel.add(footerCard, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        String asOf = asOfDateField.getText().trim();
        BalanceSheet bs = reportDAO.generateBalanceSheet(asOf);

        // Assets
        assetsModel.setRowCount(0);
        for (BalanceSheetItem item : bs.getAssets()) {
            assetsModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAmount()
            });
        }
        assetsSubtotalLabel.setText("Total Assets: " + GuiUtils.formatPeso(bs.getTotalAssets()));
        totalAssetsStatLabel.setText(GuiUtils.formatPeso(bs.getTotalAssets()));
        totalAssetsSummaryLabel.setText(GuiUtils.formatPeso(bs.getTotalAssets()));

        // Liabilities
        liabilitiesModel.setRowCount(0);
        for (BalanceSheetItem item : bs.getLiabilities()) {
            liabilitiesModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAmount()
            });
        }
        liabilitiesSubtotalLabel.setText("Total Liabilities: " + GuiUtils.formatPeso(bs.getTotalLiabilities()));
        totalLiabilitiesStatLabel.setText(GuiUtils.formatPeso(bs.getTotalLiabilities()));

        // Equity
        equityModel.setRowCount(0);
        for (BalanceSheetItem item : bs.getEquityItems()) {
            equityModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAmount()
            });
        }
        equitySubtotalLabel.setText("Total Equity: " + GuiUtils.formatPeso(bs.getTotalEquity()));
        totalEquityStatLabel.setText(GuiUtils.formatPeso(bs.getTotalEquity()));

        BigDecimal totalLiabAndEquity = bs.getTotalLiabilitiesAndEquity();
        totalLiabEquitySummaryLabel.setText(GuiUtils.formatPeso(totalLiabAndEquity));

        boolean balanced = bs.isBalanced();
        bannerContainer.removeAll();
        if (balanced) {
            JPanel banner = GuiUtils.createAlertBanner(
                    true,
                    "Accounting Equation Balanced:",
                    "Total Assets (" + GuiUtils.formatPeso(bs.getTotalAssets()) + ") equal Total Liabilities + Equity (" + GuiUtils.formatPeso(totalLiabAndEquity) + ")."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
            statusPill.setText("In Equilibrium");
            statusPill.setBackground(ThemeManager.SUCCESS_BG);
            statusPill.setForeground(ThemeManager.SUCCESS_COLOR);
        } else {
            BigDecimal diff = bs.getTotalAssets().subtract(totalLiabAndEquity).abs();
            JPanel banner = GuiUtils.createAlertBanner(
                    false,
                    "Accounting Equation Imbalance:",
                    "Assets and Liabilities + Equity differ by " + GuiUtils.formatPeso(diff) + ". Please review general ledger journal postings."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
            statusPill.setText("Imbalance Detected");
            statusPill.setBackground(ThemeManager.DANGER_BG);
            statusPill.setForeground(ThemeManager.DANGER_COLOR);
        }
        bannerContainer.revalidate();
        bannerContainer.repaint();
    }

    public void printReport() {
        try {
            MessageFormat header = new MessageFormat("Accurix Financial — Balance Sheet (Statement of Financial Position)");
            MessageFormat footer = new MessageFormat("Page {0,number,integer}   •   Assets = Liabilities + Equity");
            assetsTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize printer: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
