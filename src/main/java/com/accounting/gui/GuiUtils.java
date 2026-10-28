package com.accounting.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common GUI helper utilities for formatting, executive styling,
 * vector icon rendering, and table styling.
 */
public class GuiUtils {

    private static final DecimalFormat PESO_FORMAT = new DecimalFormat("₱#,##0.00");

    /**
     * Formats a BigDecimal amount as Philippine Pesos (e.g. ₱150,000.00).
     */
    public static String formatPeso(BigDecimal amount) {
        if (amount == null) {
            return "₱0.00";
        }
        return PESO_FORMAT.format(amount);
    }

    /**
     * Formats a double amount as Philippine Pesos.
     */
    public static String formatPeso(double amount) {
        return PESO_FORMAT.format(amount);
    }

    private static List<Image> cachedAppIcons = null;

    /**
     * Generates a sleek Vercel monochrome application icon at the specified dimension
     * featuring Refined Executive Balance Scales with smooth curved dishes.
     */
    public static BufferedImage createAppIcon(int size) {
        return createAppIcon(size, new Color(0, 0, 0), Color.WHITE);
    }

    /**
     * Generates the application icon with custom background and foreground colors.
     */
    public static BufferedImage createAppIcon(int size, Color bgColor, Color fgColor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (bgColor != null) {
            int arc = Math.max(4, (int) Math.round(size * 0.24));
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, size, size, arc, arc);

            // Subtle 1px translucent highlight border so it pops cleanly on dark backgrounds
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, size - 1, size - 1, arc, arc);
        }

        g2.setColor(fgColor != null ? fgColor : Color.WHITE);

        // Vector Balance Scales — Exactly matching Web SVG (viewBox 0 0 24 24)
        // In Web: 15px SVG centered inside 26px badge (padding ratio ~0.20 on each side)
        float innerSize = size * 0.60f;
        float ox = (size - innerSize) / 2.0f;
        float oy = (size - innerSize) / 2.0f;
        float s = innerSize / 24.0f;

        float strokeW = Math.max(1.1f, 1.8f * s);
        g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Central vertical mast: M12 4v16
        g2.draw(new Line2D.Float(ox + 12f * s, oy + 4f * s, ox + 12f * s, oy + 20f * s));
        // Grounded base: M7 20h10
        g2.draw(new Line2D.Float(ox + 7f * s, oy + 20f * s, ox + 17f * s, oy + 20f * s));
        // Balance beam: M4 7h16
        g2.draw(new Line2D.Float(ox + 4f * s, oy + 7f * s, ox + 20f * s, oy + 7f * s));
        // Fulcrum pivot: circle cx="12" cy="4" r="1.5"
        float r = 1.5f * s;
        g2.fill(new Ellipse2D.Float(ox + (12f - r) * s, oy + (4f - r) * s, r * 2f, r * 2f));

        // Left Pan suspension cords: M6 7l-2 5 and M6 7l2 5
        g2.draw(new Line2D.Float(ox + 6f * s, oy + 7f * s, ox + 4f * s, oy + 12f * s));
        g2.draw(new Line2D.Float(ox + 6f * s, oy + 7f * s, ox + 8f * s, oy + 12f * s));
        // Left Pan dish: M3.5 12c0 2 5 2 5 0z
        Path2D.Float leftDish = new Path2D.Float();
        leftDish.moveTo(ox + 3.5f * s, oy + 12f * s);
        leftDish.curveTo(ox + 3.5f * s, oy + 14.5f * s, ox + 8.5f * s, oy + 14.5f * s, ox + 8.5f * s, oy + 12f * s);
        leftDish.closePath();
        g2.fill(leftDish);

        // Right Pan suspension cords: M18 7l-2 5 and M18 7l2 5
        g2.draw(new Line2D.Float(ox + 18f * s, oy + 7f * s, ox + 16f * s, oy + 12f * s));
        g2.draw(new Line2D.Float(ox + 18f * s, oy + 7f * s, ox + 20f * s, oy + 12f * s));
        // Right Pan dish: M15.5 12c0 2 5 2 5 0z
        Path2D.Float rightDish = new Path2D.Float();
        rightDish.moveTo(ox + 15.5f * s, oy + 12f * s);
        rightDish.curveTo(ox + 15.5f * s, oy + 14.5f * s, ox + 20.5f * s, oy + 14.5f * s, ox + 20.5f * s, oy + 12f * s);
        rightDish.closePath();
        g2.fill(rightDish);

        g2.dispose();
        return image;
    }

    /**
     * Returns an unmodifiable multi-resolution list of icons for Windows taskbar and titlebars.
     */
    public static synchronized List<Image> getAppIcons() {
        if (cachedAppIcons == null) {
            int[] sizes = {16, 20, 24, 32, 40, 48, 64, 128, 256};
            List<Image> icons = new ArrayList<>();
            for (int sz : sizes) {
                icons.add(createAppIcon(sz));
            }
            cachedAppIcons = Collections.unmodifiableList(icons);
        }
        return cachedAppIcons;
    }

    /**
     * Applies the multi-resolution Vercel app icons to any window (JFrame, JDialog).
     */
    public static void setWindowIcons(Window window) {
        if (window != null) {
            window.setIconImages(getAppIcons());
        }
    }

    /**
     * Renders a vector-drawn Accounting Balance Scales Emblem in sleek Vercel black.
     */
    public static ImageIcon createAccountingLogo(int size) {
        return new ImageIcon(createAppIcon(size, ThemeManager.PRIMARY_ACCENT, Color.WHITE));
    }

    /**
     * Renders a vector-drawn Accounting Balance Scales Emblem as an ImageIcon.
     */
    public static ImageIcon createAccountingLogo(int size, Color bgColor, Color fgColor) {
        return new ImageIcon(createAppIcon(size, bgColor, fgColor));
    }

    /**
     * Creates a Status Dot Icon (e.g. for banners and indicators).
     */
    public static ImageIcon createStatusDotIcon(boolean isSuccess) {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isSuccess ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR);
        g2.fillOval(1, 1, 8, 8);
        g2.dispose();
        return new ImageIcon(img);
    }

    /**
     * Creates a crisp 16x16 minimalist vector icon for sidebar navigation.
     */
    public static ImageIcon createNavIcon(String key, boolean active) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        Color color = active ? ThemeManager.PRIMARY_ACCENT : ThemeManager.MUTED_FOREGROUND;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (key) {
            case "DASHBOARD":
                g2.drawRoundRect(2, 2, 5, 5, 1, 1);
                g2.drawRoundRect(9, 2, 5, 5, 1, 1);
                g2.drawRoundRect(2, 9, 5, 5, 1, 1);
                g2.drawRoundRect(9, 9, 5, 5, 1, 1);
                break;
            case "JOURNALS":
                Path2D.Float doc = new Path2D.Float();
                doc.moveTo(3, 14);
                doc.lineTo(3, 2);
                doc.lineTo(10, 2);
                doc.lineTo(13, 5);
                doc.lineTo(13, 14);
                doc.closePath();
                g2.draw(doc);
                g2.drawLine(10, 2, 10, 5);
                g2.drawLine(10, 5, 13, 5);
                g2.drawLine(5, 8, 11, 8);
                g2.drawLine(5, 11, 9, 11);
                break;
            case "LEDGER":
                g2.drawRoundRect(2, 2, 12, 12, 2, 2);
                g2.drawLine(8, 2, 8, 14);
                g2.drawLine(4, 5, 6, 5);
                g2.drawLine(10, 5, 12, 5);
                g2.drawLine(4, 8, 6, 8);
                g2.drawLine(10, 8, 12, 8);
                g2.drawLine(4, 11, 6, 11);
                g2.drawLine(10, 11, 12, 11);
                break;
            case "ACCOUNTS":
                g2.fillOval(2, 3, 2, 2);
                g2.fillOval(2, 7, 2, 2);
                g2.fillOval(2, 11, 2, 2);
                g2.drawLine(6, 4, 14, 4);
                g2.drawLine(6, 8, 14, 8);
                g2.drawLine(6, 12, 14, 12);
                break;
            case "TRIAL_BALANCE":
                g2.drawLine(8, 2, 8, 14);
                g2.drawLine(5, 14, 11, 14);
                g2.drawLine(3, 4, 13, 4);
                g2.fillOval(7, 2, 2, 2);
                Path2D.Float lp = new Path2D.Float();
                lp.moveTo(4, 4);
                lp.lineTo(2, 8);
                lp.lineTo(6, 8);
                lp.closePath();
                g2.draw(lp);
                Path2D.Float rp = new Path2D.Float();
                rp.moveTo(12, 4);
                rp.lineTo(10, 8);
                rp.lineTo(14, 8);
                rp.closePath();
                g2.draw(rp);
                break;
            case "INCOME_STATEMENT":
                Path2D.Float trend = new Path2D.Float();
                trend.moveTo(2, 12);
                trend.lineTo(6, 8);
                trend.lineTo(9, 10);
                trend.lineTo(14, 4);
                g2.draw(trend);
                g2.drawLine(10, 4, 14, 4);
                g2.drawLine(14, 4, 14, 8);
                break;
            case "BALANCE_SHEET":
                g2.drawRoundRect(2, 2, 12, 12, 2, 2);
                g2.drawLine(2, 6, 14, 6);
                g2.drawLine(8, 6, 8, 14);
                break;
            case "SETTINGS":
                g2.drawOval(6, 6, 4, 4);
                g2.drawOval(4, 4, 8, 8);
                g2.drawLine(8, 2, 8, 4);
                g2.drawLine(8, 12, 8, 14);
                g2.drawLine(2, 8, 4, 8);
                g2.drawLine(12, 8, 14, 8);
                break;
            case "SIGN_OUT":
                g2.setColor(new Color(239, 68, 68)); // Red 500
                g2.drawLine(6, 2, 2, 2);
                g2.drawLine(2, 2, 2, 14);
                g2.drawLine(2, 14, 6, 14);
                g2.drawLine(6, 8, 14, 8);
                g2.drawLine(11, 5, 14, 8);
                g2.drawLine(11, 11, 14, 8);
                break;
            case "PRINT":
                g2.drawRect(5, 2, 6, 3);
                g2.drawRoundRect(2, 5, 12, 6, 2, 2);
                g2.drawRect(4, 9, 8, 5);
                break;
            default:
                g2.drawOval(3, 3, 10, 10);
                break;
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    private static void autoScaleLine(Graphics2D g2, float pad, float sX, float sY, float x1, float y1, float x2, float y2) {
        g2.drawLine((int) (pad + (x1 * sX)), (int) (pad + (y1 * sY)), (int) (pad + (x2 * sX)), (int) (pad + (y2 * sY)));
    }

    private static void autoScaleTriangle(Graphics2D g2, float pad, float sX, float sY, float topX, float topY, float leftX, float leftY, float rightX, float rightY) {
        int[] xPoints = { (int) (pad + (topX * sX)), (int) (pad + (leftX * sX)), (int) (pad + (rightX * sX)) };
        int[] yPoints = { (int) (pad + (topY * sY)), (int) (pad + (leftY * sY)), (int) (pad + (rightY * sY)) };
        g2.drawPolygon(xPoints, yPoints, 3);
    }

    /**
     * Creates an Executive Card panel with pure white background and subtle slate border.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(ThemeManager.CARD_BACKGROUND);
        card.setBorder(createCardBorder());
        return card;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        );
    }

    /**
     * Creates an authentic Vercel Geist Section Card with Header, Body, and Footer Bar.
     */
    public static JPanel createVercelSectionCard(String title, String subtitle, JComponent body, String footerText, JComponent rightComponent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeManager.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

        // 1. Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(ThemeManager.CARD_BACKGROUND);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(ThemeManager.FONT_SMALL);
        lblSub.setForeground(ThemeManager.MUTED_FOREGROUND);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSub);

        card.add(header, BorderLayout.NORTH);

        // 2. Body
        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.setBackground(ThemeManager.CARD_BACKGROUND);
        bodyWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.BORDER_COLOR),
                new EmptyBorder(18, 20, 18, 20)
        ));
        bodyWrapper.add(body, BorderLayout.CENTER);
        card.add(bodyWrapper, BorderLayout.CENTER);

        // 3. Footer Bar
        if (footerText != null || rightComponent != null) {
            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(ThemeManager.SUBTLE_BACKGROUND);
            footer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.BORDER_COLOR),
                    new EmptyBorder(12, 20, 12, 20)
            ));

            if (footerText != null) {
                JLabel lblFoot = new JLabel(footerText);
                lblFoot.setFont(ThemeManager.FONT_TINY);
                lblFoot.setForeground(ThemeManager.MUTED_FOREGROUND);
                footer.add(lblFoot, BorderLayout.WEST);
            }

            if (rightComponent != null) {
                footer.add(rightComponent, BorderLayout.EAST);
            }

            card.add(footer, BorderLayout.SOUTH);
        }

        return card;
    }

    public static JPanel createVercelSectionCard(String title, String subtitle, JComponent body, String footerText, JButton actionButton) {
        return createVercelSectionCard(title, subtitle, body, footerText, (JComponent) actionButton);
    }

    /**
     * Creates an authentic Vercel Geist Primary Button (Pure Black #000000 -> Charcoal #262626 on hover).
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                Color bg = pressed ? new Color(17, 17, 17) : (hovered ? ThemeManager.PRIMARY_HOVER : ThemeManager.PRIMARY_ACCENT);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(ThemeManager.FONT_REGULAR_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setRolloverEnabled(true);
        return btn;
    }

    /**
     * Creates an authentic Vercel Geist Secondary Button (Pure White #FFFFFF -> Crisp #000000 border on hover).
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                Color bg = pressed ? new Color(245, 245, 245) : (hovered ? new Color(250, 250, 250) : Color.WHITE);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                Color border = (hovered || pressed) ? ThemeManager.PRIMARY_ACCENT : ThemeManager.BORDER_COLOR;
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setForeground(ThemeManager.TEXT_PRIMARY);
        btn.setFont(ThemeManager.FONT_REGULAR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setRolloverEnabled(true);
        return btn;
    }

    /**
     * Creates an authentic Vercel Geist Danger Button (White -> Light Red on hover).
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                Color bg = pressed ? new Color(254, 226, 226) : (hovered ? ThemeManager.DANGER_BG : Color.WHITE);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                Color border = (hovered || pressed) ? ThemeManager.DANGER_COLOR : ThemeManager.DANGER_BORDER;
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setForeground(ThemeManager.DANGER_COLOR);
        btn.setFont(ThemeManager.FONT_REGULAR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setRolloverEnabled(true);
        return btn;
    }

    /**
     * Creates a Status Badge Pill label (e.g. "Active", "Balanced", "Imbalance").
     * Draws an authentic Vercel-style status dot and rounded background.
     * Uses a fixed intrinsic size so every pill renders at exactly the same dimensions.
     */
    public static JLabel createBadgePill(String text, boolean isSuccess) {
        String cleanText = text.replace("\u2713", "").replace("\u26A0", "").trim();
        final int pillH = 22;
        final int dotSize = 6;
        final int padLeft = 22;   // space for dot + gap
        final int padRight = 10;
        final int hPad = 4;       // outer horizontal margin each side

        JLabel pill = new JLabel(cleanText, SwingConstants.CENTER) {
            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                int textW = fm.stringWidth(getText());
                int w = hPad + padLeft + textW + padRight + hPad;
                int h = pillH + 6; // 3px vertical margin top+bottom
                return new Dimension(w, h);
            }
            @Override
            public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override
            public Dimension getMaximumSize() { return getPreferredSize(); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Dimension pref = getPreferredSize();
                int pw = pref.width - hPad * 2;
                int px = (getWidth() - pw) / 2;
                int py = (getHeight() - pillH) / 2;

                // Background
                g2.setColor(getBackground());
                g2.fillRoundRect(px, py, pw, pillH, pillH, pillH);

                // 1px Border
                g2.setColor(isSuccess ? ThemeManager.SUCCESS_BORDER : ThemeManager.DANGER_BORDER);
                g2.drawRoundRect(px, py, pw - 1, pillH - 1, pillH, pillH);

                // Status Dot Indicator (Vercel Style)
                g2.setColor(isSuccess ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR);
                int dotY = (getHeight() - dotSize) / 2;
                g2.fillOval(px + 8, dotY, dotSize, dotSize);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(ThemeManager.FONT_TINY);
        pill.setBorder(new EmptyBorder(0, padLeft, 0, padRight));

        if (isSuccess) {
            pill.setBackground(ThemeManager.SUCCESS_BG);
            pill.setForeground(ThemeManager.SUCCESS_COLOR);
        } else {
            pill.setBackground(ThemeManager.DANGER_BG);
            pill.setForeground(ThemeManager.DANGER_COLOR);
        }
        return pill;
    }

    /**
     * Creates an authentic Vercel pill badge with customizable classification styling.
     * Uses a fixed intrinsic size so every pill renders at exactly the same dimensions.
     */
    public static JLabel createPill(String text, String type) {
        Color bg, border, fg;
        switch (type != null ? type.toLowerCase() : "neutral") {
            case "success":
            case "revenue":
                bg = ThemeManager.SUCCESS_BG; border = ThemeManager.SUCCESS_BORDER; fg = ThemeManager.SUCCESS_COLOR; break;
            case "error":
            case "danger":
            case "expense":
                bg = ThemeManager.DANGER_BG; border = ThemeManager.DANGER_BORDER; fg = ThemeManager.DANGER_COLOR; break;
            case "asset":
                bg = new Color(239, 246, 255); border = new Color(191, 219, 254); fg = new Color(30, 64, 175); break; // Blue
            case "liability":
                bg = new Color(254, 243, 199); border = new Color(253, 230, 138); fg = new Color(146, 64, 14); break; // Amber
            case "equity":
                bg = new Color(243, 232, 255); border = new Color(221, 214, 254); fg = new Color(107, 33, 168); break; // Purple
            default:
                bg = ThemeManager.SUBTLE_BACKGROUND; border = ThemeManager.BORDER_COLOR; fg = ThemeManager.TEXT_SECONDARY; break;
        }

        final Color fBg = bg, fBorder = border, fFg = fg;
        final int pillH = 20;
        final int hTextPad = 10;
        final int hMargin = 2;

        JLabel pill = new JLabel(text, SwingConstants.CENTER) {
            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                int textW = fm.stringWidth(getText());
                int w = hMargin * 2 + hTextPad * 2 + textW;
                int h = pillH + 6;
                return new Dimension(w, h);
            }
            @Override
            public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override
            public Dimension getMaximumSize() { return getPreferredSize(); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Dimension pref = getPreferredSize();
                int pw = pref.width - hMargin * 2;
                int px = (getWidth() - pw) / 2;
                int py = (getHeight() - pillH) / 2;

                g2.setColor(fBg);
                g2.fillRoundRect(px, py, pw, pillH, pillH, pillH);
                g2.setColor(fBorder);
                g2.drawRoundRect(px, py, pw - 1, pillH - 1, pillH, pillH);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(ThemeManager.FONT_TINY);
        pill.setForeground(fFg);
        pill.setBorder(new EmptyBorder(2, hTextPad, 2, hTextPad));
        return pill;
    }

    /**
     * Creates the top-bar User Status Pill matching header.jsp (glowing emerald dot + username + role).
     */
    public static JLabel createUserStatusPill(String username, String role) {
        String text = (username != null ? username : "User") + " (" + (role != null ? role : "OPERATOR") + ")";
        JLabel pill = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.SUBTLE_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 999, 999);
                g2.setColor(ThemeManager.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 999, 999);

                // Emerald status dot
                g2.setColor(new Color(16, 185, 129)); // #10b981
                int dotSize = 6;
                int dotY = (getHeight() - dotSize) / 2;
                g2.fillOval(10, dotY, dotSize, dotSize);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(ThemeManager.FONT_SMALL);
        pill.setForeground(ThemeManager.TEXT_PRIMARY);
        pill.setBorder(new EmptyBorder(4, 22, 4, 12));
        return pill;
    }

    /**
     * Creates an authentic Vercel Alert Banner matching .alert.alert-success / .alert.alert-error.
     */
    public static JPanel createAlertBanner(boolean isSuccess, String strongText, String normalText) {
        JPanel alert = new JPanel(new BorderLayout(10, 0));
        Color bg = isSuccess ? ThemeManager.SUCCESS_BG : ThemeManager.DANGER_BG;
        Color border = isSuccess ? ThemeManager.SUCCESS_BORDER : ThemeManager.DANGER_BORDER;
        Color fg = isSuccess ? new Color(6, 95, 70) : new Color(153, 27, 27); // #065F46 vs #991B1B

        alert.setBackground(bg);
        alert.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(fg);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (isSuccess) {
                    g2.drawOval(1, 1, 14, 14);
                    g2.drawLine(5, 8, 7, 10);
                    g2.drawLine(7, 10, 11, 6);
                } else {
                    g2.drawOval(1, 1, 14, 14);
                    g2.drawLine(8, 5, 8, 9);
                    g2.fillOval(7, 11, 2, 2);
                }
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(16, 16));
        alert.add(iconLabel, BorderLayout.WEST);

        JLabel textLabel = new JLabel("<html><strong>" + strongText + "</strong> " + normalText + "</html>");
        textLabel.setFont(ThemeManager.FONT_REGULAR);
        textLabel.setForeground(fg);
        alert.add(textLabel, BorderLayout.CENTER);

        return alert;
    }

    /**
     * Creates an authentic Vercel Stat Card with optional left border accent (e.g. for Net Profit / Loss).
     */
    public static JPanel createStatCard(String label, JComponent valueComp, JComponent subtextComp, Color leftBorderAccent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeManager.CARD_BACKGROUND);

        Border baseBorder = BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true);
        if (leftBorderAccent != null) {
            baseBorder = BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 3, 0, 0, leftBorderAccent),
                    BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true)
            );
        }
        card.setBorder(BorderFactory.createCompoundBorder(
                baseBorder,
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(ThemeManager.FONT_TINY);
        lbl.setForeground(ThemeManager.MUTED_FOREGROUND);
        content.add(lbl);
        content.add(Box.createVerticalStrut(6));

        content.add(valueComp);
        content.add(Box.createVerticalStrut(4));

        if (subtextComp != null) {
            content.add(subtextComp);
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static JPanel createStatCard(String label, JLabel valLabel, String subtext) {
        JLabel sub = new JLabel(subtext);
        sub.setFont(ThemeManager.FONT_SMALL);
        sub.setForeground(ThemeManager.TEXT_SECONDARY);
        return createStatCard(label, valLabel, sub, null);
    }

    /**
     * Creates a Table Card matching .geist-card with crisp header and borderless viewport.
     */
    public static JPanel createTableCard(String title, String subtitle, JComponent tableComp, JComponent rightHeaderComp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ThemeManager.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(ThemeManager.CARD_BACKGROUND);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, ThemeManager.BORDER_COLOR),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        titleBox.add(lblTitle);

        if (subtitle != null && !subtitle.isEmpty()) {
            titleBox.add(Box.createVerticalStrut(3));
            JLabel lblSub = new JLabel(subtitle);
            lblSub.setFont(ThemeManager.FONT_SMALL);
            lblSub.setForeground(ThemeManager.MUTED_FOREGROUND);
            titleBox.add(lblSub);
        }
        header.add(titleBox, BorderLayout.WEST);

        if (rightHeaderComp != null) {
            header.add(rightHeaderComp, BorderLayout.EAST);
        }
        card.add(header, BorderLayout.NORTH);

        // Body / Table
        JScrollPane scrollPane = (tableComp instanceof JScrollPane) ? (JScrollPane) tableComp : new JScrollPane(tableComp);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.CARD_BACKGROUND);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Styles a JTable to match executive modern standards with alternating row backgrounds.
     */
    public static void styleTable(JTable table) {
        table.setFont(ThemeManager.FONT_REGULAR);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249)); // Very subtle line
        table.setSelectionBackground(new Color(241, 245, 249));
        table.setSelectionForeground(ThemeManager.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        // Header renderer
        table.getTableHeader().setFont(ThemeManager.FONT_TABLE_HEADER);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFoc, row, col);
                l.setFont(ThemeManager.FONT_TABLE_HEADER);
                l.setBackground(ThemeManager.APP_BACKGROUND);
                l.setForeground(ThemeManager.TEXT_SECONDARY);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER_COLOR),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
                return l;
            }
        });
    }

    /**
     * Returns a right-aligned numeric cell renderer for currency columns.
     */
    public static TableCellRenderer createNumberTableCellRenderer() {
        return new NumericRenderer();
    }

    /**
     * Standard Right-Aligned Numeric Table Cell Renderer with Mono font.
     */
    public static class NumericRenderer extends DefaultTableCellRenderer {
        public NumericRenderer() {
            setHorizontalAlignment(RIGHT);
            setFont(ThemeManager.FONT_MONO);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));
            if (value instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) value;
                setText(bd.compareTo(BigDecimal.ZERO) == 0 ? "—" : formatPeso(bd));
            } else if (value instanceof Number) {
                double d = ((Number) value).doubleValue();
                setText(d == 0 ? "—" : formatPeso(d));
            }
            return c;
        }
    }
}
