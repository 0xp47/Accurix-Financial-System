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

        if (size <= 20) {
            float sw = size == 16 ? 1.5f : 1.8f;
            g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int midX = size / 2;
            g2.drawLine(midX, 3, midX, size - 4);
            g2.drawLine(3, 5, size - 4, 5);
            g2.drawLine(midX - 3, size - 4, midX + 3, size - 4);

            Path2D.Float leftPan = new Path2D.Float();
            leftPan.moveTo(3, 8.5f);
            leftPan.quadTo(5, 11f, 7, 8.5f);
            leftPan.closePath();
            g2.fill(leftPan);

            Path2D.Float rightPan = new Path2D.Float();
            rightPan.moveTo(size - 7, 8.5f);
            rightPan.quadTo(size - 5, 11f, size - 3, 8.5f);
            rightPan.closePath();
            g2.fill(rightPan);

            g2.fillOval(midX - 1, 2, 2, 2);
        } else {
            float strokeW = Math.max(1.8f, size / 24.0f);
            g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float pad = size * 0.20f;
            float w = size - (pad * 2);
            float h = size - (pad * 2);
            float sX = w / 24f;
            float sY = h / 24f;

            // Center spine
            g2.draw(new Line2D.Float(pad + (12 * sX), pad + (5 * sY), pad + (12 * sX), pad + (19 * sY)));
            // Sturdy grounded base
            g2.draw(new Line2D.Float(pad + (7 * sX), pad + (19 * sY), pad + (17 * sX), pad + (19 * sY)));
            // Sleek horizontal balance beam
            g2.draw(new Line2D.Float(pad + (4.5f * sX), pad + (7.5f * sY), pad + (19.5f * sX), pad + (7.5f * sY)));
            // Fulcrum pivot
            float r = Math.max(2.5f, size / 16f);
            g2.fill(new Ellipse2D.Float(pad + (12 * sX) - r / 2, pad + (5 * sY) - r / 2, r, r));

            // Left Pan Suspension
            g2.draw(new Line2D.Float(pad + (6 * sX), pad + (7.5f * sY), pad + (3.8f * sX), pad + (12.5f * sY)));
            g2.draw(new Line2D.Float(pad + (6 * sX), pad + (7.5f * sY), pad + (8.2f * sX), pad + (12.5f * sY)));
            // Left Pan dish (smooth arc/bowl)
            Path2D.Float leftDish = new Path2D.Float();
            leftDish.moveTo(pad + (3.2f * sX), pad + (12.5f * sY));
            leftDish.quadTo(pad + (6f * sX), pad + (15.5f * sY), pad + (8.8f * sX), pad + (12.5f * sY));
            leftDish.closePath();
            g2.fill(leftDish);

            // Right Pan Suspension
            g2.draw(new Line2D.Float(pad + (18 * sX), pad + (7.5f * sY), pad + (15.8f * sX), pad + (12.5f * sY)));
            g2.draw(new Line2D.Float(pad + (18 * sX), pad + (7.5f * sY), pad + (20.2f * sX), pad + (12.5f * sY)));
            // Right Pan dish (smooth arc/bowl)
            Path2D.Float rightDish = new Path2D.Float();
            rightDish.moveTo(pad + (15.2f * sX), pad + (12.5f * sY));
            rightDish.quadTo(pad + (18f * sX), pad + (15.5f * sY), pad + (20.8f * sX), pad + (12.5f * sY));
            rightDish.closePath();
            g2.fill(rightDish);
        }

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
                g2.drawRoundRect(1, 1, 5, 5, 2, 2);
                g2.drawRoundRect(9, 1, 5, 5, 2, 2);
                g2.drawRoundRect(1, 9, 5, 5, 2, 2);
                g2.drawRoundRect(9, 9, 5, 5, 2, 2);
                break;
            case "JOURNALS":
                g2.drawRoundRect(2, 1, 11, 13, 2, 2);
                g2.drawLine(5, 5, 10, 5);
                g2.drawLine(5, 8, 10, 8);
                g2.drawLine(5, 11, 8, 11);
                break;
            case "LEDGER":
                g2.drawRoundRect(1, 2, 13, 11, 2, 2);
                g2.drawLine(7, 2, 7, 13);
                g2.drawLine(3, 5, 5, 5);
                g2.drawLine(9, 5, 12, 5);
                g2.drawLine(3, 8, 5, 8);
                g2.drawLine(9, 8, 12, 8);
                break;
            case "TRIAL_BALANCE":
                g2.drawLine(8, 2, 8, 14);
                g2.drawLine(3, 4, 13, 4);
                g2.drawLine(2, 14, 14, 14);
                g2.drawLine(3, 4, 1, 8); g2.drawLine(3, 4, 5, 8); g2.drawLine(1, 8, 5, 8);
                g2.drawLine(13, 4, 11, 8); g2.drawLine(13, 4, 15, 8); g2.drawLine(11, 8, 15, 8);
                break;
            case "INCOME_STATEMENT":
                g2.drawLine(2, 13, 14, 13);
                g2.drawLine(3, 10, 6, 6);
                g2.drawLine(6, 6, 9, 8);
                g2.drawLine(9, 8, 13, 3);
                g2.drawLine(10, 3, 13, 3);
                g2.drawLine(13, 3, 13, 6);
                break;
            case "BALANCE_SHEET":
                g2.drawRect(2, 5, 3, 8);
                g2.drawRect(6, 2, 4, 11);
                g2.drawRect(11, 7, 3, 6);
                break;
            case "ACCOUNTS":
                g2.drawRoundRect(2, 4, 12, 10, 2, 2);
                g2.drawLine(2, 7, 14, 7);
                g2.drawLine(4, 2, 8, 2);
                break;
            case "SETTINGS":
                g2.drawLine(2, 4, 14, 4);
                g2.fillOval(5, 2, 4, 4);
                g2.drawLine(2, 8, 14, 8);
                g2.fillOval(9, 6, 4, 4);
                g2.drawLine(2, 12, 14, 12);
                g2.fillOval(4, 10, 4, 4);
                break;
            case "SIGN_OUT":
                g2.setColor(new Color(239, 68, 68)); // Red 500
                g2.drawLine(6, 2, 2, 2);
                g2.drawLine(2, 2, 2, 14);
                g2.drawLine(2, 14, 6, 14);
                g2.drawLine(6, 8, 14, 8);
                g2.drawLine(10, 4, 14, 8);
                g2.drawLine(10, 12, 14, 8);
                break;
            case "PRINT":
                g2.drawRect(5, 2, 6, 4);
                g2.drawRoundRect(2, 6, 12, 6, 2, 2);
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
     */
    public static JLabel createBadgePill(String text, boolean isSuccess) {
        String cleanText = text.replace("✓", "").replace("⚠", "").trim();
        JLabel pill = new JLabel(cleanText, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int pillH = 22;
                int pillY = (getHeight() - pillH) / 2;
                int pillW = getWidth() - 10;
                int pillX = 5;

                // Background
                g2.setColor(getBackground());
                g2.fillRoundRect(pillX, pillY, pillW, pillH, pillH, pillH);

                // 1px Border
                g2.setColor(isSuccess ? ThemeManager.SUCCESS_BORDER : ThemeManager.DANGER_BORDER);
                g2.drawRoundRect(pillX, pillY, pillW - 1, pillH - 1, pillH, pillH);

                // Status Dot Indicator (Vercel Style)
                g2.setColor(isSuccess ? ThemeManager.SUCCESS_COLOR : ThemeManager.DANGER_COLOR);
                int dotSize = 6;
                int dotY = (getHeight() - dotSize) / 2;
                g2.fillOval(pillX + 10, dotY, dotSize, dotSize);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(ThemeManager.FONT_TINY);
        pill.setBorder(new EmptyBorder(0, 20, 0, 10));

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
        JLabel pill = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int pillH = 20;
                int pillY = (getHeight() - pillH) / 2;
                int pillW = getWidth() - 4;
                int pillX = 2;

                g2.setColor(fBg);
                g2.fillRoundRect(pillX, pillY, pillW, pillH, pillH, pillH);
                g2.setColor(fBorder);
                g2.drawRoundRect(pillX, pillY, pillW - 1, pillH - 1, pillH, pillH);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(ThemeManager.FONT_TINY);
        pill.setForeground(fFg);
        pill.setBorder(new EmptyBorder(2, 10, 2, 10));
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
