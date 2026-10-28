package com.accounting.gui;

import com.accounting.dao.UserDAO;
import com.accounting.model.User;
import com.accounting.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings and Profile panel built with authentic Vercel Geist modular Section Cards.
 */
public class SettingsPanel extends JPanel {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final MainFrame mainFrame;

    private JTextField fullNameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public SettingsPanel(User user, MainFrame mainFrame) {
        this.currentUser = user;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.APP_BACKGROUND);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        add(buildHeaderSection(), BorderLayout.NORTH);
        add(buildContentSection(), BorderLayout.CENTER);
    }

    private JPanel buildHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("Account Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage operator credentials, audit identity, and system configurations.");
        subtitle.setFont(ThemeManager.FONT_REGULAR);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildContentSection() {
        // Scrollable content area so cards retain natural heights and never stretch awkwardly
        JPanel scrollRoot = new JPanel(new BorderLayout());
        scrollRoot.setOpaque(false);

        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsPanel.setOpaque(false);

        // LEFT COLUMN: Profile Card & Password Card stacked
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        leftCol.add(buildProfileSectionCard());
        leftCol.add(Box.createVerticalStrut(18));
        leftCol.add(buildSecuritySectionCard());

        // RIGHT COLUMN: Diagnostics & System Specs Card + Governance Card
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        rightCol.add(buildDiagnosticsSectionCard());
        rightCol.add(Box.createVerticalStrut(18));
        rightCol.add(buildAuditSafeguardsSectionCard());

        columnsPanel.add(leftCol);
        columnsPanel.add(rightCol);

        scrollRoot.add(columnsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollRoot);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildProfileSectionCard() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.32;
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        uLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(uLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        JTextField usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(ThemeManager.SUBTLE_BACKGROUND);
        usernameField.setPreferredSize(new Dimension(0, 36));
        body.add(usernameField, gbc);

        // Assigned Role
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.32;
        JLabel rLabel = new JLabel("Assigned Role");
        rLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        rLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(rLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        JPanel rolePillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePillPanel.setOpaque(false);
        JLabel rolePill = GuiUtils.createBadgePill(currentUser.getRole(), true);
        rolePillPanel.add(rolePill);
        body.add(rolePillPanel, gbc);

        // Display Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.32;
        JLabel dLabel = new JLabel("Display Name");
        dLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        dLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(dLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        fullNameField = new JTextField(currentUser.getFullName());
        fullNameField.setPreferredSize(new Dimension(0, 36));
        body.add(fullNameField, gbc);

        JButton saveProfileBtn = GuiUtils.createPrimaryButton("Save Display Name");
        saveProfileBtn.addActionListener(e -> updateProfile());

        return GuiUtils.createVercelSectionCard(
                "Profile & Operator Identity",
                "Your full name appears on all signed journal entry vouchers and audit logs.",
                body,
                "Please use 50 characters at maximum.",
                saveProfileBtn
        );
    }

    private JPanel buildSecuritySectionCard() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Password
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.32;
        JLabel cpLabel = new JLabel("Current Password");
        cpLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        cpLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(cpLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        currentPasswordField = new JPasswordField();
        currentPasswordField.setPreferredSize(new Dimension(0, 36));
        body.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.32;
        JLabel npLabel = new JLabel("New Password");
        npLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        npLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(npLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        newPasswordField = new JPasswordField();
        newPasswordField.setPreferredSize(new Dimension(0, 36));
        body.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.32;
        JLabel cnpLabel = new JLabel("Confirm Password");
        cnpLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        cnpLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        body.add(cnpLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.68;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(0, 36));
        body.add(confirmPasswordField, gbc);

        JButton updatePasswordBtn = GuiUtils.createPrimaryButton("Update Password");
        updatePasswordBtn.addActionListener(e -> updatePassword());

        return GuiUtils.createVercelSectionCard(
                "Security & Password",
                "Ensure your account is protected with a secure password (minimum 6 characters).",
                body,
                "Protected with 10-round salted BCrypt hashing.",
                updatePasswordBtn
        );
    }

    private JPanel buildDiagnosticsSectionCard() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        body.add(createEntityRow("Currency Policy", "Philippine Peso (₱)", "ISO 4217 Currency Code PHP — Strictly enforced across all modules"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Accounting Standard", "Double-Entry GAAP", "Mathematical equality strictly enforced (Total Debits = Total Credits)"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Database Engine", "MySQL 8.0+ InnoDB", "Port 3306 (accounting_db) with atomic transactions & foreign keys"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Security Protocol", "BCrypt Hashing", "Work Factor 10 salted cryptographic password verification"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Application Runtime", "Java 17 LTS", "100% Native Desktop Java GUI powered by FlatLaf (Vercel Geist Theme)"));

        JLabel activePill = GuiUtils.createBadgePill("Database Active", true);

        return GuiUtils.createVercelSectionCard(
                "System Standards & Architecture",
                "Core enterprise specifications and active database connectivity status.",
                body,
                "Accurix Financial v1.0.0 — All transactions synchronized.",
                activePill
        );
    }

    private JPanel buildAuditSafeguardsSectionCard() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        body.add(createEntityRow("Transaction Integrity", "Strict Equality", "Debits must equal credits before journal voucher commit"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Audit Immutability", "Append Only", "Posted vouchers maintain historical audit records and user IDs"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Real-Time Ledger", "Atomic Sync", "Ledger balances calculate dynamically from validated line items"));
        body.add(Box.createVerticalStrut(12));
        body.add(createEntityRow("Financial Interval", "Fiscal Year", "Standard reporting timeframe with instantaneous closing calculations"));

        JLabel auditPill = GuiUtils.createBadgePill("Fully Compliant", true);

        return GuiUtils.createVercelSectionCard(
                "Governance & Compliance Safeguards",
                "Automated financial controls and GAAP compliance rules enforced by the system.",
                body,
                "Audit standards verified against Philippine GAAP & IFRS.",
                auditPill
        );
    }

    private JPanel createEntityRow(String title, String badgeText, String description) {
        JPanel row = new JPanel(new BorderLayout(8, 2));
        row.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel lblBadge = new JLabel(badgeText);
        lblBadge.setFont(ThemeManager.FONT_MONO_BOLD);
        lblBadge.setForeground(ThemeManager.PRIMARY_ACCENT);

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblBadge, BorderLayout.EAST);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(ThemeManager.FONT_TINY);
        lblDesc.setForeground(ThemeManager.MUTED_FOREGROUND);

        row.add(top, BorderLayout.NORTH);
        row.add(lblDesc, BorderLayout.CENTER);

        return row;
    }

    private void updateProfile() {
        String newName = fullNameField.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name cannot be blank.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = userDAO.updateProfile(currentUser.getId(), newName);
        if (ok) {
            currentUser.setFullName(newName);
            mainFrame.updateUserHeader(currentUser);
            JOptionPane.showMessageDialog(this, "Display name updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePassword() {
        String currentPass = new String(currentPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All password fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User fresh = userDAO.findById(currentUser.getId());
        if (fresh == null || !PasswordUtil.checkPassword(currentPass, fresh.getPasswordHash())) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newHash = PasswordUtil.hashPassword(newPass);
        boolean ok = userDAO.updatePassword(currentUser.getId(), newHash);
        if (ok) {
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
