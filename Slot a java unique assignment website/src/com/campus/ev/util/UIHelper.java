package com.campus.ev.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_REGULAR_BOLD);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor.brighter(), 1, true),
            new EmptyBorder(9, 18, 9, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(blend(bgColor, Color.WHITE, 0.15f));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, UITheme.ACCENT_CYAN, UITheme.BG_DARKEST);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, UITheme.ACCENT_EMERALD, UITheme.BG_DARKEST);
    }

    public static JButton createWarningButton(String text) {
        return createButton(text, UITheme.ACCENT_AMBER, UITheme.BG_DARKEST);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, UITheme.ACCENT_ROSE, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, UITheme.BG_INPUT, UITheme.TEXT_PRIMARY);
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(UITheme.FONT_REGULAR);
        tf.setBackground(UITheme.BG_INPUT);
        tf.setForeground(UITheme.TEXT_PRIMARY);
        tf.setCaretColor(UITheme.ACCENT_CYAN);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(UITheme.FONT_REGULAR);
        cb.setBackground(UITheme.BG_INPUT);
        cb.setForeground(UITheme.TEXT_PRIMARY);
        cb.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        return cb;
    }

    public static <T> JComboBox<T> createComboBox(DefaultComboBoxModel<T> model) {
        JComboBox<T> cb = new JComboBox<>(model);
        cb.setFont(UITheme.FONT_REGULAR);
        cb.setBackground(UITheme.BG_INPUT);
        cb.setForeground(UITheme.TEXT_PRIMARY);
        cb.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        return cb;
    }

    public static JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_REGULAR_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_HEADER_LARGE);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));
        return panel;
    }

    public static JPanel createDarkPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.BG_PANEL);
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setGridColor(UITheme.BORDER);
        table.setFont(UITheme.FONT_REGULAR);
        table.setRowHeight(38);
        table.setSelectionBackground(UITheme.BG_HOVER);
        table.setSelectionForeground(UITheme.TEXT_CYAN);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.BG_DARKEST);
        header.setForeground(UITheme.TEXT_CYAN);
        header.setFont(UITheme.FONT_HEADER_SMALL);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(new LineBorder(UITheme.BORDER));
        header.setReorderingAllowed(false);

        // Center / custom align renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Status renderer for status columns
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(UITheme.BG_CARD);
                    } else {
                        c.setBackground(new Color(18, 27, 44));
                    }
                    c.setForeground(UITheme.TEXT_PRIMARY);
                }

                if (val != null) {
                    String str = val.toString();
                    if (str.equals("AVAILABLE") || str.equals("ACTIVE") || str.equals("OPERATIONAL") || str.equals("CONFIRMED") || str.equals("PAID")) {
                        c.setForeground(UITheme.ACCENT_EMERALD);
                        setFont(UITheme.FONT_REGULAR_BOLD);
                    } else if (str.equals("OCCUPIED") || str.equals("CHARGING") || str.equals("CHECKED_IN")) {
                        c.setForeground(UITheme.ACCENT_CYAN);
                        setFont(UITheme.FONT_REGULAR_BOLD);
                    } else if (str.equals("RESERVED") || str.equals("PENDING") || str.equals("PAUSED")) {
                        c.setForeground(UITheme.ACCENT_AMBER);
                        setFont(UITheme.FONT_REGULAR_BOLD);
                    } else if (str.equals("MAINTENANCE") || str.equals("OFFLINE") || str.equals("SUSPENDED") || str.equals("CANCELLED")) {
                        c.setForeground(UITheme.ACCENT_ROSE);
                        setFont(UITheme.FONT_REGULAR_BOLD);
                    }
                }
                return c;
            }
        });
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(UITheme.BG_PANEL);
        sp.getViewport().setBackground(UITheme.BG_CARD);
        sp.setBorder(new LineBorder(UITheme.BORDER, 1));
        return sp;
    }

    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Operation Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message, String title) {
        int opt = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opt == JOptionPane.YES_OPTION;
    }

    private static Color blend(Color c1, Color c2, float ratio) {
        float iRatio = 1.0f - ratio;
        int r = (int) (c1.getRed() * iRatio + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * iRatio + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * iRatio + c2.getBlue() * ratio);
        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
}
