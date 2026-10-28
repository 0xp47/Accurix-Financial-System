package com.accounting.gui;

import com.accounting.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window for Accurix Financial.
 * Faithfully follows Vercel Geist design guidelines:
 * - 56px Executive Top Bar with dynamic breadcrumbs and live status pill.
 * - 240px Inset Rounded Sidebar Navigation matching web layout.
 * - Seamless page switching and synchronized report printing.
 */
public class MainFrame extends JFrame {

    private final User currentUser;

    private CardLayout cardLayout;
    private JPanel contentContainer;

    private DashboardPanel dashboardPanel;
    private JournalListPanel journalListPanel;
    private LedgerPanel ledgerPanel;
    private TrialBalancePanel trialBalancePanel;
    private IncomeStatementPanel incomeStatementPanel;
    private BalanceSheetPanel balanceSheetPanel;
    private ChartOfAccountsPanel chartOfAccountsPanel;
    private SettingsPanel settingsPanel;

    private JPanel topBar;
    private JLabel lblBreadcrumbPrefix;
    private JLabel lblBreadcrumbCurrent;
    private JButton btnPrintTopBar;
    private JLabel lblUserPill;

    private JLabel userNameLabel;
    private JLabel userRoleLabel;

    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activeNav = "DASHBOARD";

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Accurix Financial — Enterprise Double-Entry Ledger [PHP ₱]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 860);
        setMinimumSize(new Dimension(1150, 720));
        setLocationRelativeTo(null);
        GuiUtils.setWindowIcons(this);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeManager.APP_BACKGROUND);
        setContentPane(root);

        // Sidebar Navigation (West)
        root.add(buildSidebar(), BorderLayout.WEST);

        // Content Wrapper (Top Bar North + Card Container Center)
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(ThemeManager.APP_BACKGROUND);

        // 1. Executive Top Bar
        contentWrapper.add(buildTopBar(), BorderLayout.NORTH);

        // 2. Center Content with CardLayout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(ThemeManager.APP_BACKGROUND);

        dashboardPanel = new DashboardPanel(this);
        journalListPanel = new JournalListPanel(this);
        ledgerPanel = new LedgerPanel();
        trialBalancePanel = new TrialBalancePanel();
        incomeStatementPanel = new IncomeStatementPanel();
        balanceSheetPanel = new BalanceSheetPanel();
        chartOfAccountsPanel = new ChartOfAccountsPanel();
        settingsPanel = new SettingsPanel(currentUser, this);

        contentContainer.add(dashboardPanel, "DASHBOARD");
        contentContainer.add(journalListPanel, "JOURNALS");
        contentContainer.add(ledgerPanel, "LEDGER");
        contentContainer.add(trialBalancePanel, "TRIAL_BALANCE");
        contentContainer.add(incomeStatementPanel, "INCOME_STATEMENT");
        contentContainer.add(balanceSheetPanel, "BALANCE_SHEET");
        contentContainer.add(chartOfAccountsPanel, "ACCOUNTS");
        contentContainer.add(settingsPanel, "SETTINGS");

        contentWrapper.add(contentContainer, BorderLayout.CENTER);
        root.add(contentWrapper, BorderLayout.CENTER);

        // Set default selection
        selectNav("DASHBOARD");
    }

    private JPanel buildTopBar() {
        topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBackground(ThemeManager.CARD_BACKGROUND);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.BORDER_COLOR));

        // Left Breadcrumb: Accurix / [Page Name]
        JPanel breadcrumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 17));
        breadcrumbPanel.setOpaque(false);
        breadcrumbPanel.setBorder(new EmptyBorder(0, 24, 0, 0));

        lblBreadcrumbPrefix = new JLabel("Accurix /");
        lblBreadcrumbPrefix.setFont(ThemeManager.FONT_REGULAR);
        lblBreadcrumbPrefix.setForeground(ThemeManager.MUTED_FOREGROUND);

        lblBreadcrumbCurrent = new JLabel("Overview");
        lblBreadcrumbCurrent.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBreadcrumbCurrent.setForeground(ThemeManager.TEXT_PRIMARY);

        breadcrumbPanel.add(lblBreadcrumbPrefix);
        breadcrumbPanel.add(lblBreadcrumbCurrent);
        topBar.add(breadcrumbPanel, BorderLayout.CENTER);

        // Right Actions: Optional Print Report Button + User Status Pill
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        rightActions.setOpaque(false);
        rightActions.setBorder(new EmptyBorder(0, 0, 0, 24));

        btnPrintTopBar = GuiUtils.createSecondaryButton("Print Report");
        btnPrintTopBar.setIcon(GuiUtils.createNavIcon("PRINT", false));
        btnPrintTopBar.setIconTextGap(8);
        btnPrintTopBar.setVisible(false);
        btnPrintTopBar.addActionListener(e -> printActiveReport());
        rightActions.add(btnPrintTopBar);

        lblUserPill = GuiUtils.createUserStatusPill(currentUser.getUsername(), currentUser.getRole());
        rightActions.add(lblUserPill);

        topBar.add(rightActions, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(ThemeManager.CARD_BACKGROUND);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, ThemeManager.BORDER_COLOR));

        // 1. BRAND HEADER (56px)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        brandPanel.setPreferredSize(new Dimension(240, 56));
        brandPanel.setBackground(ThemeManager.CARD_BACKGROUND);
        brandPanel.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.BORDER_COLOR));

        JLabel logo = new JLabel(GuiUtils.createAccountingLogo(26));
        brandPanel.add(logo);

        JLabel brandName = new JLabel("Accurix Financial");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brandName.setForeground(ThemeManager.TEXT_PRIMARY);
        brandPanel.add(brandName);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // 2. NAVIGATION MENU ITEMS
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(12, 10, 12, 10));

        // Group 1: OVERVIEW
        addSectionHeader(menuPanel, "OVERVIEW");
        addNavItem(menuPanel, "Dashboard", "DASHBOARD");

        // Group 2: ACCOUNTING
        menuPanel.add(Box.createVerticalStrut(12));
        addSectionHeader(menuPanel, "ACCOUNTING");
        addNavItem(menuPanel, "Journal Entries", "JOURNALS");
        addNavItem(menuPanel, "General Ledger", "LEDGER");
        addNavItem(menuPanel, "Chart of Accounts", "ACCOUNTS");

        // Group 3: FINANCIAL REPORTS
        menuPanel.add(Box.createVerticalStrut(12));
        addSectionHeader(menuPanel, "FINANCIAL REPORTS");
        addNavItem(menuPanel, "Trial Balance", "TRIAL_BALANCE");
        addNavItem(menuPanel, "Income Statement", "INCOME_STATEMENT");
        addNavItem(menuPanel, "Balance Sheet", "BALANCE_SHEET");

        // Group 4: SYSTEM
        menuPanel.add(Box.createVerticalStrut(12));
        addSectionHeader(menuPanel, "SYSTEM");
        addNavItem(menuPanel, "Settings", "SETTINGS");
        addSignOutNavItem(menuPanel);

        menuPanel.add(Box.createVerticalGlue());

        JScrollPane scrollNav = new JScrollPane(menuPanel);
        scrollNav.setBorder(BorderFactory.createEmptyBorder());
        scrollNav.setOpaque(false);
        scrollNav.getViewport().setOpaque(false);
        scrollNav.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollNav.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollNav.getVerticalScrollBar().setUnitIncrement(12);
        sidebar.add(scrollNav, BorderLayout.CENTER);

        // 3. FOOTER USER PROFILE (56px)
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setPreferredSize(new Dimension(240, 56));
        footerPanel.setBackground(ThemeManager.CARD_BACKGROUND);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, ThemeManager.BORDER_COLOR),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // Avatar Initials
        String initials = getInitials(currentUser.getFullName());
        JLabel avatarLabel = new JLabel(initials, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.PRIMARY_ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarLabel.setPreferredSize(new Dimension(30, 30));
        avatarLabel.setFont(ThemeManager.FONT_TINY);
        avatarLabel.setForeground(Color.WHITE);
        footerPanel.add(avatarLabel, BorderLayout.WEST);

        userNameLabel = new JLabel(currentUser.getFullName());
        userNameLabel.setFont(ThemeManager.FONT_REGULAR_BOLD);
        userNameLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        userRoleLabel = new JLabel(currentUser.getRole());
        userRoleLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        userRoleLabel.setForeground(ThemeManager.MUTED_FOREGROUND);

        JPanel userText = new JPanel();
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.setOpaque(false);
        userText.add(userNameLabel);
        userText.add(Box.createVerticalStrut(2));
        userText.add(userRoleLabel);
        footerPanel.add(userText, BorderLayout.CENTER);

        // Settings gear button on right
        JButton btnSettingsIcon = new JButton(GuiUtils.createNavIcon("SETTINGS", false));
        btnSettingsIcon.setPreferredSize(new Dimension(28, 28));
        btnSettingsIcon.setBorderPainted(false);
        btnSettingsIcon.setContentAreaFilled(false);
        btnSettingsIcon.setFocusPainted(false);
        btnSettingsIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettingsIcon.setToolTipText("Account Settings");
        btnSettingsIcon.addActionListener(e -> selectNav("SETTINGS"));
        footerPanel.add(btnSettingsIcon, BorderLayout.EAST);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addSectionHeader(JPanel container, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(136, 136, 136)); // Geist Muted #888888
        label.setBorder(new EmptyBorder(6, 10, 4, 10));
        container.add(label);
    }

    private void addNavItem(JPanel container, String text, String key) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isSelected = key.equals(activeNav);
                boolean isHovered = getModel().isRollover();

                Color bg = isSelected ? ThemeManager.SUBTLE_BACKGROUND : (isHovered ? ThemeManager.SUBTLE_BACKGROUND : ThemeManager.CARD_BACKGROUND);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(ThemeManager.FONT_REGULAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(220, 36));
        btn.setIcon(GuiUtils.createNavIcon(key, false));
        btn.setIconTextGap(10);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        btn.setRolloverEnabled(true);

        btn.addActionListener(e -> selectNav(key));

        navButtons.put(key, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(2));
    }

    private void addSignOutNavItem(JPanel container) {
        JButton btn = new JButton("Sign Out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHovered = getModel().isRollover();
                if (isHovered) {
                    g2.setColor(ThemeManager.DANGER_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(ThemeManager.FONT_REGULAR);
        btn.setForeground(new Color(239, 68, 68)); // Red 500
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(220, 36));
        btn.setIcon(GuiUtils.createNavIcon("SIGN_OUT", false));
        btn.setIconTextGap(10);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        btn.setRolloverEnabled(true);

        btn.addActionListener(e -> signOut());

        container.add(btn);
        container.add(Box.createVerticalStrut(2));
    }

    public void selectNav(String key) {
        this.activeNav = key;
        cardLayout.show(contentContainer, key);

        // Update button visual styles
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton b = entry.getValue();
            boolean isSelected = entry.getKey().equals(key);
            b.setIcon(GuiUtils.createNavIcon(entry.getKey(), isSelected));
            if (isSelected) {
                b.setFont(ThemeManager.FONT_REGULAR_BOLD);
                b.setForeground(ThemeManager.TEXT_PRIMARY);
            } else {
                b.setFont(ThemeManager.FONT_REGULAR);
                b.setForeground(ThemeManager.TEXT_SECONDARY);
            }
            b.repaint();
        }

        // Update Top Bar Breadcrumb and Print button
        String pageTitle = "Overview";
        boolean isReport = false;
        switch (key) {
            case "DASHBOARD":
                pageTitle = "Overview";
                dashboardPanel.refreshData();
                break;
            case "JOURNALS":
                pageTitle = "Journal Entries";
                journalListPanel.refreshData();
                break;
            case "LEDGER":
                pageTitle = "General Ledger";
                isReport = true;
                ledgerPanel.refreshData();
                break;
            case "ACCOUNTS":
                pageTitle = "Chart of Accounts";
                chartOfAccountsPanel.refreshData();
                break;
            case "TRIAL_BALANCE":
                pageTitle = "Trial Balance";
                isReport = true;
                trialBalancePanel.refreshData();
                break;
            case "INCOME_STATEMENT":
                pageTitle = "Income Statement";
                isReport = true;
                incomeStatementPanel.refreshData();
                break;
            case "BALANCE_SHEET":
                pageTitle = "Balance Sheet";
                isReport = true;
                balanceSheetPanel.refreshData();
                break;
            case "SETTINGS":
                pageTitle = "Account Settings";
                break;
        }

        lblBreadcrumbCurrent.setText(pageTitle);
        lblBreadcrumbCurrent.setPreferredSize(null);
        if (topBar != null) {
            topBar.doLayout();
            topBar.revalidate();
            topBar.repaint();
        }
        btnPrintTopBar.setVisible(isReport);
    }

    public void printActiveReport() {
        switch (activeNav) {
            case "LEDGER":
                ledgerPanel.printLedger();
                break;
            case "TRIAL_BALANCE":
                trialBalancePanel.printReport();
                break;
            case "INCOME_STATEMENT":
                incomeStatementPanel.printReport();
                break;
            case "BALANCE_SHEET":
                balanceSheetPanel.printReport();
                break;
        }
    }

    public void updateUserHeader(User user) {
        userNameLabel.setText(user.getFullName());
        userRoleLabel.setText(user.getRole());
        lblUserPill.setText(user.getUsername() + " (" + user.getRole() + ")");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showNewJournalDialog() {
        JournalEntryDialog dialog = new JournalEntryDialog(this, currentUser, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            selectNav("JOURNALS");
        }
    }

    private void signOut() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?", "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            AccountingDesktopApp.launchLogin();
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "US";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
