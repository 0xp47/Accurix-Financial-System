package com.accounting.gui;

import com.accounting.dao.UserDAO;
import com.accounting.model.User;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Executive Authentication Dialog.
 * Uses GridBagLayout to guarantee pixel-perfect centering and alignment.
 */
public class LoginDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private User authenticatedUser = null;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private JButton btnLogin;

    public LoginDialog(JFrame parent) {
        super(parent, "Sign In — Accurix Financial", true);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        GuiUtils.setWindowIcons(this);

        // Seamless pure white executive background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(ThemeManager.CARD_BACKGROUND);
        root.setBorder(new EmptyBorder(32, 44, 32, 44));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // 1. Logo Badge (Centered)
        JLabel lblLogo = new JLabel(GuiUtils.createAccountingLogo(48));
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 12, 0);
        root.add(lblLogo, gbc);

        // 2. Title (Centered)
        JLabel lblTitle = new JLabel("Accurix Financial");
        lblTitle.setFont(ThemeManager.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        root.add(lblTitle, gbc);

        // 3. Subtitle (Centered)
        JLabel lblSubtitle = new JLabel("Enterprise Double-Entry Bookkeeping Suite");
        lblSubtitle.setFont(ThemeManager.FONT_SMALL);
        lblSubtitle.setForeground(ThemeManager.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        root.add(lblSubtitle, gbc);

        // 4. Error Message (Centered)
        lblError = new JLabel(" ");
        lblError.setFont(ThemeManager.FONT_TINY);
        lblError.setForeground(ThemeManager.COLOR_ERROR);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        root.add(lblError, gbc);

        // 5. Username Tag (Left aligned with input)
        JLabel lblUserTag = new JLabel("Username");
        lblUserTag.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblUserTag.setForeground(ThemeManager.TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 4, 0);
        root.add(lblUserTag, gbc);

        // 6. Username Input (310px width)
        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        txtUsername.setFont(ThemeManager.FONT_REGULAR);
        txtUsername.setPreferredSize(new Dimension(310, 38));
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        root.add(txtUsername, gbc);

        // 7. Password Tag (Left aligned with input)
        JLabel lblPassTag = new JLabel("Password");
        lblPassTag.setFont(ThemeManager.FONT_REGULAR_BOLD);
        lblPassTag.setForeground(ThemeManager.TEXT_PRIMARY);
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 4, 0);
        root.add(lblPassTag, gbc);

        // 8. Password Input (310px width matching username)
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        txtPassword.setFont(ThemeManager.FONT_REGULAR);
        txtPassword.setPreferredSize(new Dimension(310, 38));
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 22, 0);
        root.add(txtPassword, gbc);

        // 9. Sign In Button (310px width matching inputs)
        btnLogin = GuiUtils.createPrimaryButton("Sign In to Accurix");
        btnLogin.setPreferredSize(new Dimension(310, 40));
        btnLogin.addActionListener(e -> attemptLogin());
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 8, 0);
        root.add(btnLogin, gbc);

        getRootPane().setDefaultButton(btnLogin);

        getContentPane().add(root);
        pack();
        setSize(410, 480);
        setLocationRelativeTo(null);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        btnLogin.setEnabled(false);
        lblError.setText("Authenticating credentials...");

        if (!com.accounting.util.DBConnection.isConnected()) {
            lblError.setText("Database offline: Please start MySQL in XAMPP (port 3306).");
            btnLogin.setEnabled(true);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.authenticatedUser = user;
            dispose();
        } else {
            lblError.setText("Invalid username or password.");
            btnLogin.setEnabled(true);
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
