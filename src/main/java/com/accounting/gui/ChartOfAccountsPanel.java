package com.accounting.gui;

import com.accounting.dao.AccountDAO;
import com.accounting.model.Account;
import com.accounting.model.AccountType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Chart of Accounts Panel matching Vercel Geist aesthetics:
 * - Header with subtitle and "+ Add Account" primary black action.
 * - Form filter card for search queries and classification filtering.
 * - Table card with classification and active status pill badges.
 */
public class ChartOfAccountsPanel extends JPanel {

    private final AccountDAO accountDAO = new AccountDAO();

    private JTextField searchField;
    private JComboBox<String> typeFilterCombo;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private List<Account> currentAccounts;

    public ChartOfAccountsPanel() {
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

        JLabel title = new JLabel("Chart of Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Master directory of operational accounts, financial classifications, and normal balance rules.");
        subtitle.setFont(ThemeManager.FONT_REGULAR);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(subtitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        JButton newAccountBtn = GuiUtils.createPrimaryButton("+ Add Account");
        newAccountBtn.addActionListener(e -> openAccountDialog(null));
        headerPanel.add(newAccountBtn, BorderLayout.EAST);

        topArea.add(headerPanel);
        topArea.add(Box.createVerticalStrut(18));

        // Filter Card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        filterCard.setBackground(ThemeManager.CARD_BACKGROUND);
        filterCard.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

        // Search Field
        JPanel pSearch = new JPanel(new BorderLayout(0, 4));
        pSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("SEARCH CODE / TITLE");
        lblSearch.setFont(ThemeManager.FONT_TINY);
        lblSearch.setForeground(ThemeManager.MUTED_FOREGROUND);
        pSearch.add(lblSearch, BorderLayout.NORTH);

        searchField = new JTextField(16);
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshData();
            }
        });
        pSearch.add(searchField, BorderLayout.CENTER);
        filterCard.add(pSearch);

        // Classification Filter
        JPanel pType = new JPanel(new BorderLayout(0, 4));
        pType.setOpaque(false);
        JLabel lblType = new JLabel("CLASSIFICATION");
        lblType.setFont(ThemeManager.FONT_TINY);
        lblType.setForeground(ThemeManager.MUTED_FOREGROUND);
        pType.add(lblType, BorderLayout.NORTH);

        typeFilterCombo = new JComboBox<>(new String[]{"ALL", "ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE"});
        typeFilterCombo.setPreferredSize(new Dimension(140, 32));
        typeFilterCombo.addActionListener(e -> refreshData());
        pType.add(typeFilterCombo, BorderLayout.CENTER);
        filterCard.add(pType);

        // Reset Button
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pBtn.setOpaque(false);
        pBtn.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton resetBtn = GuiUtils.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilterCombo.setSelectedIndex(0);
            refreshData();
        });
        pBtn.add(resetBtn);
        filterCard.add(pBtn);

        topArea.add(filterCard);
        add(topArea, BorderLayout.NORTH);

        // 2. CENTER TABLE CARD
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        String[] columns = {"Code", "Account Title", "Classification", "Normal Balance", "Status", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        accountsTable = new JTable(tableModel);
        GuiUtils.styleTable(accountsTable);

        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(280);

        // Classification pill renderer
        accountsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
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

        // Status pill renderer
        accountsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int row, int col) {
                boolean active = "Active".equals(v);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                p.setOpaque(true);
                p.setBackground(isSel ? t.getSelectionBackground() : t.getBackground());
                p.add(GuiUtils.createBadgePill(active ? "Active" : "Inactive", active));
                return p;
            }
        });

        // Edit on double click
        accountsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && accountsTable.getSelectedRow() >= 0 && currentAccounts != null) {
                    int row = accountsTable.getSelectedRow();
                    if (row < currentAccounts.size()) {
                        openAccountDialog(currentAccounts.get(row));
                    }
                }
            }
        });

        JPanel tableHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableHeaderRight.setOpaque(false);

        JButton editBtn = GuiUtils.createSecondaryButton("Edit Account");
        editBtn.addActionListener(e -> {
            int row = accountsTable.getSelectedRow();
            if (row >= 0 && currentAccounts != null && row < currentAccounts.size()) {
                openAccountDialog(currentAccounts.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an account from the table to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tableHeaderRight.add(editBtn);

        JPanel tableCard = GuiUtils.createTableCard(
                "Chart of Accounts Directory",
                "Master catalog of general ledger accounts and normal balance definitions",
                accountsTable,
                tableHeaderRight
        );

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        String query = searchField.getText().trim();
        String type = (String) typeFilterCombo.getSelectedItem();

        currentAccounts = accountDAO.search(query, type);
        tableModel.setRowCount(0);

        for (Account a : currentAccounts) {
            tableModel.addRow(new Object[]{
                    a.getAccountCode(),
                    a.getAccountName(),
                    a.getAccountType() != null ? a.getAccountType().name() : "",
                    a.getAccountType() != null ? (a.getAccountType().isNormalDebit() ? "Debit" : "Credit") : "",
                    a.isActive() ? "Active" : "Inactive",
                    a.getDescription() != null ? a.getDescription() : "—"
            });
        }
    }

    private void openAccountDialog(Account existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Create New Account" : "Edit Account: " + existing.getAccountCode(), true);
        dialog.setSize(520, 460);
        dialog.setLocationRelativeTo(this);
        GuiUtils.setWindowIcons(dialog);
        dialog.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(ThemeManager.APP_BACKGROUND);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(ThemeManager.CARD_BACKGROUND);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Code
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblCode = new JLabel("Account Code *");
        lblCode.setFont(ThemeManager.FONT_REGULAR_BOLD);
        formCard.add(lblCode, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JTextField codeField = new JTextField();
        codeField.setPreferredSize(new Dimension(200, 32));
        if (existing != null) {
            codeField.setText(existing.getAccountCode());
            codeField.setEditable(false);
            codeField.setEnabled(false);
        }
        formCard.add(codeField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel lblName = new JLabel("Account Title *");
        lblName.setFont(ThemeManager.FONT_REGULAR_BOLD);
        formCard.add(lblName, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 32));
        if (existing != null) nameField.setText(existing.getAccountName());
        formCard.add(nameField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel lblType = new JLabel("Classification *");
        lblType.setFont(ThemeManager.FONT_REGULAR_BOLD);
        formCard.add(lblType, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JComboBox<AccountType> typeCombo = new JComboBox<>(AccountType.values());
        typeCombo.setPreferredSize(new Dimension(200, 32));
        if (existing != null) typeCombo.setSelectedItem(existing.getAccountType());
        formCard.add(typeCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        JLabel lblDesc = new JLabel("Description");
        lblDesc.setFont(ThemeManager.FONT_REGULAR);
        formCard.add(lblDesc, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JTextField descField = new JTextField();
        descField.setPreferredSize(new Dimension(200, 32));
        if (existing != null && existing.getDescription() != null) descField.setText(existing.getDescription());
        formCard.add(descField, gbc);

        // Active
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        JLabel lblActive = new JLabel("Active Status");
        lblActive.setFont(ThemeManager.FONT_REGULAR);
        formCard.add(lblActive, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JCheckBox activeBox = new JCheckBox("Active in General Ledger", existing == null || existing.isActive());
        activeBox.setOpaque(false);
        formCard.add(activeBox, gbc);

        root.add(formCard, BorderLayout.CENTER);

        // Button Bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);

        JButton cancelBtn = GuiUtils.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(cancelBtn);

        JButton saveBtn = GuiUtils.createPrimaryButton("Save Account");
        saveBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            AccountType type = (AccountType) typeCombo.getSelectedItem();
            String desc = descField.getText().trim();
            boolean active = activeBox.isSelected();

            if (code.isEmpty() || name.isEmpty() || type == null) {
                JOptionPane.showMessageDialog(dialog, "Account code and title are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existing == null) {
                if (accountDAO.findByCode(code) != null) {
                    JOptionPane.showMessageDialog(dialog, "Account code '" + code + "' already exists.", "Duplicate Code", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Account newAcc = new Account(0, code, name, type, desc, active);
                int newId = accountDAO.createAccount(newAcc);
                boolean success = newId > 0;
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Account successfully created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create account.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                existing.setAccountName(name);
                existing.setAccountType(type);
                existing.setDescription(desc);
                existing.setActive(active);
                boolean success = accountDAO.updateAccount(existing);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Account successfully updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update account.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnBar.add(saveBtn);

        root.add(btnBar, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }
}
