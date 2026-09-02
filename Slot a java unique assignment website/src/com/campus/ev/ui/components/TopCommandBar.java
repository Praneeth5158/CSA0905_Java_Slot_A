package com.campus.ev.ui.components;

import com.campus.ev.model.OperationalSummaryDTO;
import com.campus.ev.util.DateTimeUtil;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Date;

public class TopCommandBar extends JPanel {

    private final JLabel lblClock;
    private final JLabel lblActiveSessions;
    private final JLabel lblAvailableCount;
    private final JLabel lblOccupiedCount;
    private final JLabel lblReservedCount;
    private final JLabel lblMaintenanceCount;
    private final JButton btnDbSettings;
    private final JButton btnRefresh;

    public TopCommandBar(Runnable onRefreshClick, Runnable onDbSettingsClick) {
        super(new BorderLayout(16, 0));
        setBackground(UITheme.BG_DARKEST);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1),
            new EmptyBorder(10, 18, 10, 18)
        ));

        // LEFT: Branding & Operational Status
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("⚡ SMART CAMPUS EV");
        lblLogo.setFont(UITheme.FONT_HEADER_LARGE);
        lblLogo.setForeground(UITheme.ACCENT_CYAN);

        JLabel lblSub = new JLabel("ENERGY CONTROL CENTER");
        lblSub.setFont(UITheme.FONT_SMALL_BOLD);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel brandGroup = new JPanel(new GridLayout(2, 1, 0, 0));
        brandGroup.setOpaque(false);
        brandGroup.add(lblLogo);
        brandGroup.add(lblSub);
        leftPanel.add(brandGroup);

        JLabel lblStatusPill = new JLabel(" ● GRID OPERATIONAL ");
        lblStatusPill.setFont(UITheme.FONT_REGULAR_BOLD);
        lblStatusPill.setForeground(UITheme.ACCENT_EMERALD);
        lblStatusPill.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.ACCENT_EMERALD, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        leftPanel.add(lblStatusPill);

        // CENTER: Live Telemetry Status Chips
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        centerPanel.setOpaque(false);

        lblAvailableCount = createChip("AVAILABLE", "0", UITheme.ACCENT_EMERALD);
        lblOccupiedCount = createChip("OCCUPIED", "0", UITheme.ACCENT_CYAN);
        lblReservedCount = createChip("RESERVED", "0", UITheme.ACCENT_AMBER);
        lblMaintenanceCount = createChip("MAINT", "0", UITheme.ACCENT_ROSE);
        lblActiveSessions = createChip("CHARGING NOW", "0", UITheme.ACCENT_PURPLE);

        centerPanel.add(lblAvailableCount);
        centerPanel.add(lblOccupiedCount);
        centerPanel.add(lblReservedCount);
        centerPanel.add(lblMaintenanceCount);
        centerPanel.add(lblActiveSessions);

        // RIGHT: Live Clock & Action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        lblClock = new JLabel(DateTimeUtil.formatTime(new Date()));
        lblClock.setFont(UITheme.FONT_TELEMETRY_SM);
        lblClock.setForeground(UITheme.TEXT_PRIMARY);
        rightPanel.add(lblClock);

        btnRefresh = UIHelper.createSecondaryButton("🔄 Refresh");
        btnRefresh.setFont(UITheme.FONT_REGULAR_BOLD);
        btnRefresh.addActionListener(e -> { if (onRefreshClick != null) onRefreshClick.run(); });
        rightPanel.add(btnRefresh);

        btnDbSettings = UIHelper.createSecondaryButton("⚙ DB");
        btnDbSettings.setFont(UITheme.FONT_REGULAR_BOLD);
        btnDbSettings.addActionListener(e -> { if (onDbSettingsClick != null) onDbSettingsClick.run(); });
        rightPanel.add(btnDbSettings);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Live Clock Ticker
        Timer timer = new Timer(1000, e -> lblClock.setText(DateTimeUtil.formatTime(new Date())));
        timer.start();
    }

    private JLabel createChip(String label, String value, Color color) {
        JLabel lbl = new JLabel(label + ": " + value);
        lbl.setFont(UITheme.FONT_SMALL_BOLD);
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return lbl;
    }

    public void updateTelemetry(OperationalSummaryDTO dto) {
        if (dto == null) return;
        lblAvailableCount.setText("AVAILABLE: " + dto.getAvailablePoints());
        lblOccupiedCount.setText("OCCUPIED: " + dto.getOccupiedPoints());
        lblReservedCount.setText("RESERVED: " + dto.getReservedPoints());
        lblMaintenanceCount.setText("MAINT: " + dto.getMaintenancePoints());
        lblActiveSessions.setText("CHARGING NOW: " + dto.getActiveSessionsCount());
        repaint();
    }
}
