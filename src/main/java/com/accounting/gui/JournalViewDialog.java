package com.accounting.gui;

import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Formal Journal Voucher View & Print Preview Dialog.
 */
public class JournalViewDialog extends JDialog {

    public JournalViewDialog(Frame parent, JournalEntry entry) {
        super(parent, "Journal Voucher — " + entry.getReferenceNumber(), true);
        setSize(760, 560);
        setLocationRelativeTo(parent);
        GuiUtils.setWindowIcons(this);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(ThemeManager.APP_BACKGROUND);

        // Header Card
        JPanel headerCard = GuiUtils.createCard();
        headerCard.setLayout(new BorderLayout(16, 0));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel title = new JLabel("JOURNAL ENTRY VOUCHER #" + entry.getId());
        title.setFont(ThemeManager.FONT_HEADER);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel meta = new JLabel(String.format("Posting Date: %s   •   Reference: %s",
                entry.getEntryDate() != null ? entry.getEntryDate().toString() : "—",
                entry.getReferenceNumber() != null ? entry.getReferenceNumber() : "—"));
        meta.setFont(ThemeManager.FONT_SMALL);
        meta.setForeground(ThemeManager.TEXT_SECONDARY);

        JLabel part = new JLabel("Particulars: " + (entry.getParticulars() != null ? entry.getParticulars() : "—"));
        part.setFont(ThemeManager.FONT_REGULAR);
        part.setForeground(ThemeManager.TEXT_PRIMARY);

        leftBox.add(title);
        leftBox.add(Box.createVerticalStrut(4));
        leftBox.add(meta);
        leftBox.add(Box.createVerticalStrut(6));
        leftBox.add(part);
        headerCard.add(leftBox, BorderLayout.CENTER);

        JLabel pill = GuiUtils.createBadgePill(entry.isBalanced() ? "In Equilibrium" : "Imbalance", entry.isBalanced());
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBox.setOpaque(false);
        rightBox.add(pill);
        headerCard.add(rightBox, BorderLayout.EAST);

        root.add(headerCard, BorderLayout.NORTH);

        // Lines Table Card
        JPanel tableCard = GuiUtils.createCard();
        tableCard.setLayout(new BorderLayout());

        String[] cols = { "Code", "Account Title", "Classification", "Debit (₱)", "Credit (₱)" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        for (JournalEntryLine line : entry.getLines()) {
            model.addRow(new Object[]{
                    line.getAccountCode(),
                    line.getAccountName(),
                    line.getAccountType() != null ? line.getAccountType().name() : "—",
                    line.getDebit(),
                    line.getCredit()
            });
        }

        JTable table = new JTable(model);
        GuiUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int row, int col) {
                String typeStr = v != null ? v.toString() : "";
                return GuiUtils.createPill(typeStr, typeStr);
            }
        });

        table.getColumnModel().getColumn(3).setCellRenderer(GuiUtils.createNumberTableCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(GuiUtils.createNumberTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        root.add(tableCard, BorderLayout.CENTER);

        // Bottom Summary & Actions Card
        JPanel bottomCard = GuiUtils.createCard();
        bottomCard.setLayout(new BorderLayout());

        JLabel lblTotals = new JLabel(String.format("Audited Totals:   Debit: %s      Credit: %s",
                GuiUtils.formatPeso(entry.getTotalDebit()),
                GuiUtils.formatPeso(entry.getTotalCredit())));
        lblTotals.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblTotals.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnPrint = GuiUtils.createSecondaryButton("Print Voucher");
        btnPrint.addActionListener(e -> {
            try {
                boolean complete = table.print(JTable.PrintMode.FIT_WIDTH,
                        new java.text.MessageFormat("Journal Voucher #" + entry.getId() + " (" + entry.getReferenceNumber() + ")"),
                        new java.text.MessageFormat("Page {0}"));
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Voucher sent to printer successfully.", "Print Completed", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnClose = GuiUtils.createPrimaryButton("Close");
        btnClose.addActionListener(e -> dispose());

        actions.add(btnPrint);
        actions.add(btnClose);

        bottomCard.add(lblTotals, BorderLayout.WEST);
        bottomCard.add(actions, BorderLayout.EAST);

        root.add(bottomCard, BorderLayout.SOUTH);

        getContentPane().add(root);
    }
}
