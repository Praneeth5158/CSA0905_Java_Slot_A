package com.campus.ev.ui.reservation;

import com.campus.ev.model.Reservation;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class TimeSlotGridComponent extends JPanel {

    private final String[] slotStatuses = new String[24]; // 0 to 23 hours
    private int selectedStartHour = 10;
    private int selectedEndHour = 11;
    private Consumer<Integer> onSlotClicked;

    private final Rectangle[] slotBounds = new Rectangle[24];

    public TimeSlotGridComponent() {
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        setPreferredSize(new Dimension(0, 160));
        Arrays.fill(slotStatuses, "AVAILABLE");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int h = 0; h < 24; h++) {
                    if (slotBounds[h] != null && slotBounds[h].contains(e.getPoint())) {
                        selectedStartHour = h;
                        selectedEndHour = Math.min(23, h + 1);
                        repaint();
                        if (onSlotClicked != null) {
                            onSlotClicked.accept(h);
                        }
                        return;
                    }
                }
            }
        });
    }

    public void setOnSlotClicked(Consumer<Integer> callback) {
        this.onSlotClicked = callback;
    }

    public void setReservations(List<Reservation> reservations, String pointStatus) {
        Arrays.fill(slotStatuses, "AVAILABLE");

        if ("MAINTENANCE".equalsIgnoreCase(pointStatus)) {
            Arrays.fill(slotStatuses, "MAINTENANCE");
        } else {
            if (reservations != null) {
                SimpleDateFormat sdfHour = new SimpleDateFormat("H");
                for (Reservation r : reservations) {
                    if ("CONFIRMED".equalsIgnoreCase(r.getStatus()) || "CHECKED_IN".equalsIgnoreCase(r.getStatus())) {
                        try {
                            int startH = Integer.parseInt(sdfHour.format(r.getStartTime()));
                            int endH = Integer.parseInt(sdfHour.format(r.getEndTime()));
                            if (endH <= startH) endH = startH + 1;
                            for (int h = startH; h < Math.min(24, endH); h++) {
                                slotStatuses[h] = "CHECKED_IN".equalsIgnoreCase(r.getStatus()) ? "OCCUPIED" : "RESERVED";
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        repaint();
    }

    public void setSelectedHours(int startH, int endH) {
        this.selectedStartHour = startH;
        this.selectedEndHour = endH;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 28;
        int height = getHeight() - 24;

        // Draw Title
        g2.setFont(UITheme.FONT_HEADER_SMALL);
        g2.setColor(UITheme.TEXT_CYAN);
        g2.drawString("24-HOUR INTERACTIVE TIMELINE MATRIX (00:00 - 23:59)", 14, 22);

        // Draw 24 Slot Blocks in 2 Rows of 12
        int cols = 12;
        int rows = 2;
        int blockW = (width - (cols - 1) * 6) / cols;
        int blockH = 42;
        int startY = 36;

        for (int h = 0; h < 24; h++) {
            int row = h / cols;
            int col = h % cols;
            int bx = 14 + col * (blockW + 6);
            int by = startY + row * (blockH + 8);

            slotBounds[h] = new Rectangle(bx, by, blockW, blockH);

            String status = slotStatuses[h];
            Color statusColor = UITheme.getStatusColor(status);
            boolean isSelected = (h >= selectedStartHour && h < selectedEndHour);

            // Fill Slot Block
            g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), isSelected ? 80 : 30));
            g2.fill(new RoundRectangle2D.Float(bx, by, blockW, blockH, 6, 6));

            // Border
            g2.setColor(isSelected ? Color.WHITE : statusColor);
            g2.setStroke(new BasicStroke(isSelected ? 2.0f : 1.0f));
            g2.draw(new RoundRectangle2D.Float(bx, by, blockW, blockH, 6, 6));

            // Hour text e.g. "09:00"
            g2.setFont(UITheme.FONT_REGULAR_BOLD);
            g2.setColor(isSelected ? Color.WHITE : UITheme.TEXT_PRIMARY);
            String timeStr = String.format("%02d:00", h);
            FontMetrics fm = g2.getFontMetrics();
            int tx = bx + (blockW - fm.stringWidth(timeStr)) / 2;
            g2.drawString(timeStr, tx, by + 18);

            // Status sub-label e.g. "AVL" / "RES" / "OCC"
            g2.setFont(UITheme.FONT_SMALL_BOLD);
            g2.setColor(statusColor);
            String codeStr = status.substring(0, Math.min(3, status.length()));
            FontMetrics fmCode = g2.getFontMetrics();
            int cx = bx + (blockW - fmCode.stringWidth(codeStr)) / 2;
            g2.drawString(codeStr, cx, by + 34);
        }

        g2.dispose();
    }
}
