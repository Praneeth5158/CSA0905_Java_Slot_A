package com.campus.ev.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CustomComponents {

    /**
     * Rounded Badge component for Status rendering with glowing pill background.
     */
    public static class StatusBadge extends JLabel {
        private String statusText;
        private Color statusColor;

        public StatusBadge(String status) {
            super(status != null ? status : "UNKNOWN", SwingConstants.CENTER);
            this.statusText = status != null ? status : "UNKNOWN";
            this.statusColor = UITheme.getStatusColor(status);
            setFont(UITheme.FONT_SMALL);
            setForeground(statusColor);
            setOpaque(false);
            setBorder(new EmptyBorder(4, 10, 4, 10));
        }

        public void setStatus(String status) {
            this.statusText = status != null ? status : "UNKNOWN";
            this.statusColor = UITheme.getStatusColor(status);
            setText(this.statusText);
            setForeground(this.statusColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill pill
            Color bg = new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 40);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            // Border
            g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 160));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Real-time Control Room Stat Telemetry Card.
     */
    public static class StatTelemetryCard extends JPanel {
        private final JLabel lblTitle;
        private final JLabel lblValue;
        private final JLabel lblSubtitle;
        private final Color accentColor;

        public StatTelemetryCard(String title, String value, String subtitle, Color accent) {
            super(new BorderLayout(4, 4));
            this.accentColor = accent;
            setBackground(UITheme.BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
            ));

            lblTitle = new JLabel(title.toUpperCase());
            lblTitle.setFont(UITheme.FONT_SMALL);
            lblTitle.setForeground(UITheme.TEXT_MUTED);

            lblValue = new JLabel(value);
            lblValue.setFont(UITheme.FONT_TELEMETRY);
            lblValue.setForeground(accent != null ? accent : UITheme.TEXT_PRIMARY);

            lblSubtitle = new JLabel(subtitle);
            lblSubtitle.setFont(UITheme.FONT_SMALL);
            lblSubtitle.setForeground(UITheme.TEXT_SECONDARY);

            add(lblTitle, BorderLayout.NORTH);
            add(lblValue, BorderLayout.CENTER);
            add(lblSubtitle, BorderLayout.SOUTH);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }

        public void setSubtitle(String subtitle) {
            lblSubtitle.setText(subtitle);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (accentColor != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 2, 2);
                g2.dispose();
            }
        }
    }

    /**
     * Circular Telemetry Power & Energy Meter.
     */
    public static class CircularPowerMeter extends JPanel {
        private double percentage = 0.0; // 0.0 to 100.0
        private String centerText = "0.0 kW";
        private String labelText = "POWER FLOW";
        private Color meterColor = UITheme.ACCENT_CYAN;

        public CircularPowerMeter() {
            setPreferredSize(new Dimension(130, 130));
            setOpaque(false);
        }

        public void updateMeter(double percent, String text, Color color) {
            this.percentage = Math.max(0.0, Math.min(100.0, percent));
            this.centerText = text;
            if (color != null) this.meterColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            int strokeWidth = 8;

            // Background track
            g2.setColor(UITheme.BG_INPUT);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x + strokeWidth / 2, y + strokeWidth / 2, size - strokeWidth, size - strokeWidth, -90, 360);

            // Active Arc
            int arcAngle = (int) -(percentage * 360.0 / 100.0);
            g2.setColor(meterColor);
            g2.drawArc(x + strokeWidth / 2, y + strokeWidth / 2, size - strokeWidth, size - strokeWidth, 90, arcAngle);

            // Center Text
            g2.setFont(UITheme.FONT_TELEMETRY_SM);
            g2.setColor(UITheme.TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(centerText)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 4;
            g2.drawString(centerText, tx, ty);

            // Subtitle
            g2.setFont(UITheme.FONT_SMALL);
            g2.setColor(UITheme.TEXT_MUTED);
            FontMetrics fmSub = g2.getFontMetrics();
            int sx = (getWidth() - fmSub.stringWidth(labelText)) / 2;
            g2.drawString(labelText, sx, ty + 16);

            g2.dispose();
        }
    }
}
