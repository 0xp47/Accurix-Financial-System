package com.accounting.gui;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.JournalEntryDAO;
import com.accounting.dao.ReportDAO;
import com.accounting.model.JournalEntry;
import com.accounting.model.TrialBalance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Executive Dashboard Panel mirroring the Vercel Geist web layout:
 * - Personalized greeting header with primary/secondary actions.
 * - Dynamic system equilibrium alert card.
 * - 4-Card KPI metric row with profit/loss accent indicator.
 * - Table card containing recent journal entries with status badges.
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JournalEntryDAO journalEntryDAO = new JournalEntryDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    private JLabel lblTitle;
    private JPanel bannerContainer;
    private JLabel lblTotalEntries;
    private JLabel lblTotalAccounts;
    private JLabel lblNetIncome;
    private JPanel pnlNetProfitContainer;
    private JPanel netIncomeCard;
    private JLabel lblCumulativeVolume;
    private DefaultTableModel tableModel;
    private JTable recentTable;
    private List<JournalEntry> loadedRecent;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.APP_BACKGROUND);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // 1. TOP HEADER & EQUILIBRIUM BANNER
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        String userDisplay = mainFrame.getCurrentUser() != null && mainFrame.getCurrentUser().getFullName() != null
                ? mainFrame.getCurrentUser().getFullName()
                : "Administrator";

        lblTitle = new JLabel("Welcome back, " + userDisplay);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Accounting system summary and recent transaction ledger activity.");
        lblSubtitle.setFont(ThemeManager.FONT_REGULAR);
        lblSubtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(lblTitle);
        titleBox.add(lblSubtitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        // Header Actions: [+ New Entry] [Trial Balance] [Refresh]
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);

        JButton btnNewEntry = GuiUtils.createPrimaryButton("+ New Entry");
        btnNewEntry.addActionListener(e -> mainFrame.showNewJournalDialog());

        JButton btnTrialBalance = GuiUtils.createSecondaryButton("Trial Balance");
        btnTrialBalance.addActionListener(e -> mainFrame.selectNav("TRIAL_BALANCE"));

        JButton btnRefresh = GuiUtils.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());

        actionsPanel.add(btnNewEntry);
        actionsPanel.add(btnTrialBalance);
        actionsPanel.add(btnRefresh);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        topArea.add(headerPanel);
        topArea.add(Box.createVerticalStrut(18));

        // Equilibrium Alert Container
        bannerContainer = new JPanel(new BorderLayout());
        bannerContainer.setOpaque(false);
        topArea.add(bannerContainer);

        add(topArea, BorderLayout.NORTH);

        // 2. CENTER CONTENT (4 STAT CARDS + RECENT TRANSACTIONS TABLE CARD)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // KPI 4-Card Row (Grid Layout 1x4 with 16px gap)
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        // Card 1: Total Journal Entries
        lblTotalEntries = new JLabel("0");
        lblTotalEntries.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalEntries.setForeground(ThemeManager.TEXT_PRIMARY);
        statsRow.add(GuiUtils.createStatCard("TOTAL JOURNAL ENTRIES", lblTotalEntries, "Recorded transaction vouchers"));

        // Card 2: Chart of Accounts
        lblTotalAccounts = new JLabel("0");
        lblTotalAccounts.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalAccounts.setForeground(ThemeManager.TEXT_PRIMARY);
        statsRow.add(GuiUtils.createStatCard("CHART OF ACCOUNTS", lblTotalAccounts, "Active ledger accounts"));

        // Card 3: Net Operating Income (with emerald accent border)
        lblNetIncome = new JLabel("₱0.00");
        lblNetIncome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblNetIncome.setForeground(ThemeManager.SUCCESS_COLOR);

        pnlNetProfitContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlNetProfitContainer.setOpaque(false);
        pnlNetProfitContainer.add(GuiUtils.createPill("Net Profit", "revenue"));

        netIncomeCard = GuiUtils.createStatCard("NET OPERATING INCOME", lblNetIncome, pnlNetProfitContainer, ThemeManager.SUCCESS_COLOR);
        statsRow.add(netIncomeCard);

        // Card 4: Cumulative Volume
        lblCumulativeVolume = new JLabel("₱0.00");
        lblCumulativeVolume.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCumulativeVolume.setForeground(ThemeManager.TEXT_PRIMARY);
        statsRow.add(GuiUtils.createStatCard("CUMULATIVE VOLUME", lblCumulativeVolume, "Total debits posted"));

        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Recent Journal Postings Table
        String[] columns = {"Date", "Reference #", "Particulars", "Total Debit (₱)", "Total Credit (₱)", "Ledger Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        recentTable = new JTable(tableModel);
        GuiUtils.styleTable(recentTable);

        recentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        recentTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        recentTable.getColumnModel().getColumn(2).setPreferredWidth(340);
        recentTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        recentTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        recentTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        recentTable.getColumnModel().getColumn(3).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        recentTable.getColumnModel().getColumn(4).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        // Status pill renderer
        recentTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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

        // Double click to open Journal Voucher View
        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recentTable.getSelectedRow() >= 0 && loadedRecent != null) {
                    int row = recentTable.getSelectedRow();
                    if (row < loadedRecent.size()) {
                        JournalEntry je = loadedRecent.get(row);
                        JournalEntry full = journalEntryDAO.findById(je.getId());
                        if (full != null) {
                            new JournalViewDialog(mainFrame, full).setVisible(true);
                        }
                    }
                }
            }
        });

        // Wrap inside Vercel Table Card
        JButton btnViewAll = GuiUtils.createSecondaryButton("View All Entries →");
        btnViewAll.addActionListener(e -> mainFrame.selectNav("JOURNALS"));

        JPanel tableCard = GuiUtils.createTableCard(
                "Recent Journal Postings",
                "Latest transactions recorded across all accounts",
                recentTable,
                btnViewAll
        );

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        if (mainFrame.getCurrentUser() != null && mainFrame.getCurrentUser().getFullName() != null) {
            lblTitle.setText("Welcome back, " + mainFrame.getCurrentUser().getFullName());
        }

        int totalEntries = journalEntryDAO.countEntries();
        int totalAccounts = accountDAO.countAccounts();
        BigDecimal netIncome = reportDAO.generateIncomeStatement(null, null).getNetIncome();
        TrialBalance tb = reportDAO.generateTrialBalance(null);

        lblTotalEntries.setText(String.valueOf(totalEntries));
        lblTotalAccounts.setText(String.valueOf(totalAccounts));
        lblNetIncome.setText(GuiUtils.formatPeso(netIncome));
        lblCumulativeVolume.setText(GuiUtils.formatPeso(tb.getTotalDebit()));

        // Profit or Loss accent styling
        boolean isProfit = netIncome.compareTo(BigDecimal.ZERO) >= 0;
        lblNetIncome.setForeground(isProfit ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR);
        pnlNetProfitContainer.removeAll();
        pnlNetProfitContainer.add(GuiUtils.createPill(isProfit ? "Net Profit" : "Net Loss", isProfit ? "revenue" : "expense"));
        pnlNetProfitContainer.revalidate();
        pnlNetProfitContainer.repaint();

        netIncomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 3, 0, 0, isProfit ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR),
                        BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true)
                ),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Dynamic Equilibrium Banner
        bannerContainer.removeAll();
        if (tb.isBalanced()) {
            JPanel banner = GuiUtils.createAlertBanner(
                    true,
                    "System in Equilibrium:",
                    "Total System Debits (" + GuiUtils.formatPeso(tb.getTotalDebit()) + ") perfectly match Total System Credits (" + GuiUtils.formatPeso(tb.getTotalCredit()) + ")."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
        } else {
            JPanel banner = GuiUtils.createAlertBanner(
                    false,
                    "System Imbalance Alert:",
                    "Total debits do not equal total credits (Discrepancy: " + GuiUtils.formatPeso(tb.getDifference()) + "). Review recent journal entries."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
        }
        bannerContainer.revalidate();
        bannerContainer.repaint();

        // Populate recent entries
        tableModel.setRowCount(0);
        loadedRecent = journalEntryDAO.findRecent(8);
        for (JournalEntry je : loadedRecent) {
            tableModel.addRow(new Object[]{
                    je.getEntryDate() != null ? je.getEntryDate().toString() : "—",
                    je.getReferenceNumber(),
                    je.getParticulars(),
                    je.getTotalDebit(),
                    je.getTotalCredit(),
                    je.isBalanced() ? "Balanced" : "Unbalanced"
            });
        }
    }
}
