package com.accounting.gui;

import com.accounting.dao.ReportDAO;
import com.accounting.model.TrialBalance;
import com.accounting.model.TrialBalanceItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Trial Balance Report Panel matching Vercel Geist aesthetics:
 * - Header with subtitle and Print Report action.
 * - Form filter card for cut-off date selection.
 * - Dynamic system equilibrium alert card.
 * - Table card with classification pills, right-aligned monetary values, and double-underline grand totals.
 */
public class TrialBalancePanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();

    private JTextField asOfDateField;
    private JPanel bannerContainer;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel totalDebitFooterLabel;
    private JLabel totalCreditFooterLabel;
    private JLabel balanceStatusFooterLabel;

    public TrialBalancePanel() {
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

        JLabel title = new JLabel("Trial Balance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Verifies mathematical equality of total debits and total credits across all accounts.");
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

        // Equilibrium Banner Container
        bannerContainer = new JPanel(new BorderLayout());
        bannerContainer.setOpaque(false);
        topArea.add(bannerContainer);

        add(topArea, BorderLayout.NORTH);

        // 2. CENTER CONTENT (TABLE CARD + FOOTER SUMMARY)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        String[] columns = {"Account Code", "Account Title", "Classification", "Debit Balance (₱)", "Credit Balance (₱)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        GuiUtils.styleTable(reportTable);

        reportTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(160);

        reportTable.getColumnModel().getColumn(3).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        reportTable.getColumnModel().getColumn(4).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        // Classification pill renderer
        reportTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int row, int col) {
                String typeStr = v != null ? v.toString() : "";
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                p.setOpaque(true);
                p.setBackground(isSel ? t.getSelectionBackground() : t.getBackground());
                p.add(GuiUtils.createPill(typeStr, typeStr));
                return p;
            }
        });

        JPanel tableCard = GuiUtils.createTableCard(
                "Trial Balance Statement",
                "Double-entry mathematical balance audit report",
                reportTable,
                null
        );

        centerPanel.add(tableCard, BorderLayout.CENTER);

        // 3. BOTTOM AUDITED TOTALS CARD (Double Underline Style)
        JPanel footerCard = new JPanel(new BorderLayout());
        footerCard.setBackground(ThemeManager.CARD_BACKGROUND);
        footerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        totalsPanel.setOpaque(false);

        JPanel pDeb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pDeb.setOpaque(false);
        JLabel lblDebTag = new JLabel("TOTAL DEBITS:");
        lblDebTag.setFont(ThemeManager.FONT_TINY);
        lblDebTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        totalDebitFooterLabel = new JLabel("₱0.00");
        totalDebitFooterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalDebitFooterLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        totalDebitFooterLabel.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        pDeb.add(lblDebTag);
        pDeb.add(totalDebitFooterLabel);
        totalsPanel.add(pDeb);

        JPanel pCred = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pCred.setOpaque(false);
        JLabel lblCredTag = new JLabel("TOTAL CREDITS:");
        lblCredTag.setFont(ThemeManager.FONT_TINY);
        lblCredTag.setForeground(ThemeManager.MUTED_FOREGROUND);
        totalCreditFooterLabel = new JLabel("₱0.00");
        totalCreditFooterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalCreditFooterLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        totalCreditFooterLabel.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        pCred.add(lblCredTag);
        pCred.add(totalCreditFooterLabel);
        totalsPanel.add(pCred);

        footerCard.add(totalsPanel, BorderLayout.WEST);

        balanceStatusFooterLabel = GuiUtils.createBadgePill("Balanced", true);
        footerCard.add(balanceStatusFooterLabel, BorderLayout.EAST);

        centerPanel.add(footerCard, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel lblSubtitle(JLabel l) {
        return l;
    }

    public void refreshData() {
        String asOf = asOfDateField.getText().trim();
        TrialBalance tb = reportDAO.generateTrialBalance(asOf);

        tableModel.setRowCount(0);

        for (TrialBalanceItem item : tb.getItems()) {
            tableModel.addRow(new Object[]{
                    item.getAccountCode(),
                    item.getAccountName(),
                    item.getAccountType() != null ? item.getAccountType().name() : "",
                    item.getDebitBalance().compareTo(BigDecimal.ZERO) > 0 ? item.getDebitBalance() : BigDecimal.ZERO,
                    item.getCreditBalance().compareTo(BigDecimal.ZERO) > 0 ? item.getCreditBalance() : BigDecimal.ZERO
            });
        }

        totalDebitFooterLabel.setText(GuiUtils.formatPeso(tb.getTotalDebit()));
        totalCreditFooterLabel.setText(GuiUtils.formatPeso(tb.getTotalCredit()));

        boolean balanced = tb.isBalanced();
        bannerContainer.removeAll();
        if (balanced) {
            JPanel banner = GuiUtils.createAlertBanner(
                    true,
                    "System in Equilibrium:",
                    "Total System Debits (" + GuiUtils.formatPeso(tb.getTotalDebit()) + ") perfectly match Total System Credits (" + GuiUtils.formatPeso(tb.getTotalCredit()) + ")."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
            balanceStatusFooterLabel.setText("In Equilibrium");
            balanceStatusFooterLabel.setBackground(ThemeManager.SUCCESS_BG);
            balanceStatusFooterLabel.setForeground(ThemeManager.SUCCESS_COLOR);
        } else {
            JPanel banner = GuiUtils.createAlertBanner(
                    false,
                    "System Imbalance Alert:",
                    "Total debits do not equal total credits (Discrepancy: " + GuiUtils.formatPeso(tb.getDifference()) + "). Review recent journal entries."
            );
            bannerContainer.add(banner, BorderLayout.CENTER);
            balanceStatusFooterLabel.setText("Imbalance Detected");
            balanceStatusFooterLabel.setBackground(ThemeManager.DANGER_BG);
            balanceStatusFooterLabel.setForeground(ThemeManager.DANGER_COLOR);
        }
        bannerContainer.revalidate();
        bannerContainer.repaint();
    }

    public void printReport() {
        try {
            String dateText = asOfDateField.getText().trim().isEmpty() ? "All Dates" : asOfDateField.getText().trim();
            MessageFormat header = new MessageFormat("Accurix Financial — Trial Balance (As of: " + dateText + ")");
            MessageFormat footer = new MessageFormat("Page {0,number,integer}   •   Mathematical Equality Statement");
            reportTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize printer: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
