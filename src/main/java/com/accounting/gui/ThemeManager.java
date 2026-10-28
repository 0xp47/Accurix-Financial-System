package com.accounting.gui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Manages the desktop application visual theme, typography, and color tokens.
 * Configured permanently for a modern, crisp, executive Light theme
 * inspired by modern corporate financial and enterprise design systems.
 */
public class ThemeManager {

    // Executive Typography (Segoe UI / Modern Sans)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_MONO_BOLD = new Font("Consolas", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);

    // Official Vercel Geist Light Palette (Monochrome & Neutral Accents)
    public static final Color APP_BACKGROUND = new Color(250, 250, 250);     // #FAFAFA (Geist Page)
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);    // Pure White
    public static final Color SUBTLE_BACKGROUND = new Color(245, 245, 245);  // #F5F5F5 (Geist Subtle)
    public static final Color BORDER_COLOR = new Color(234, 234, 234);       // #EAEAEA (Geist Border)
    public static final Color BORDER_STRONG = new Color(0, 0, 0);            // #000000

    public static final Color TEXT_PRIMARY = new Color(0, 0, 0);             // #000000 (Geist Foreground)
    public static final Color TEXT_SECONDARY = new Color(102, 102, 102);     // #666666 (Geist Secondary)
    public static final Color MUTED_FOREGROUND = new Color(136, 136, 136);   // #888888 (Geist Muted)

    public static final Color PRIMARY_ACCENT = new Color(0, 0, 0);           // #000000 (Vercel Black)
    public static final Color PRIMARY_HOVER = new Color(38, 38, 38);         // #262626 (Vercel Charcoal Hover)

    // Semantic Status Colors
    public static final Color SUCCESS_COLOR = new Color(5, 150, 105);        // Emerald 600
    public static final Color SUCCESS_BG = new Color(236, 253, 245);         // Emerald 50
    public static final Color SUCCESS_BORDER = new Color(167, 243, 208);     // Emerald 200

    public static final Color DANGER_COLOR = new Color(220, 38, 38);         // Red 600
    public static final Color DANGER_BG = new Color(254, 242, 242);          // Red 50
    public static final Color DANGER_BORDER = new Color(254, 202, 202);      // Red 200

    public static final Color WARNING_COLOR = new Color(217, 119, 6);        // Amber 600
    public static final Color WARNING_BG = new Color(254, 243, 199);         // Amber 50

    public static final Color INFO_COLOR = new Color(37, 99, 235);           // Blue 600
    public static final Color INFO_BG = new Color(239, 246, 255);            // Blue 50

    public static final Color COLOR_SUCCESS = SUCCESS_COLOR;
    public static final Color COLOR_ERROR = DANGER_COLOR;
    public static final Color COLOR_WARNING = WARNING_COLOR;
    public static final Color COLOR_INFO = INFO_COLOR;

    /**
     * Initializes the FlatLightLaf theme with refined enterprise UI properties.
     */
    public static void initialize() {
        initTheme();
    }

    public static void initTheme() {
        // FlatLaf Global UI Configuration
        UIManager.put("Button.arc", 6);
        UIManager.put("Component.arc", 6);
        UIManager.put("TextComponent.arc", 6);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.track", APP_BACKGROUND);
        UIManager.put("ScrollBar.thumb", new Color(218, 218, 218));

        // Table Global Defaults
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 0));
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("TableHeader.height", 34);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("Table.selectionBackground", SUBTLE_BACKGROUND);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("Table.background", CARD_BACKGROUND);
        UIManager.put("TableHeader.background", APP_BACKGROUND);
        UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
        UIManager.put("TableHeader.separatorColor", BORDER_COLOR);

        // General Colors
        UIManager.put("@background", "#fafafa");
        UIManager.put("@foreground", "#000000");
        UIManager.put("Panel.background", APP_BACKGROUND);

        FlatLightLaf.setup();
    }

    public static boolean isDark() {
        return false;
    }

    public static Color getCardBackground() {
        return CARD_BACKGROUND;
    }

    public static Color getSubtleBackground() {
        return SUBTLE_BACKGROUND;
    }

    public static Color getBorderColor() {
        return BORDER_COLOR;
    }

    public static Color getTextPrimary() {
        return TEXT_PRIMARY;
    }

    public static Color getTextSecondary() {
        return TEXT_SECONDARY;
    }
}
