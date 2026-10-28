package com.accounting.gui;

import com.accounting.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for the Accounting Desktop GUI application.
 */
public class AccountingDesktopApp {

    public static void main(String[] args) {
        // High-DPI support on Windows
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("flatlaf.useWindowDecorations", "true");

        // Set taskbar icon on OS-level where supported
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    taskbar.setIconImage(GuiUtils.createAppIcon(256));
                }
            }
        } catch (Throwable ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            // 1. Initialize Theme (Vercel Geist Light)
            ThemeManager.initTheme();

            // 2. Launch Login Modal
            launchLogin();
        });
    }

    /**
     * Shows the login dialog and transitions to the main application window upon success.
     */
    public static void launchLogin() {
        LoginDialog loginDialog = new LoginDialog(null);
        loginDialog.setVisible(true);

        User authenticated = loginDialog.getAuthenticatedUser();
        if (authenticated != null) {
            MainFrame mainFrame = new MainFrame(authenticated);
            mainFrame.setVisible(true);
        } else {
            System.exit(0);
        }
    }
}
