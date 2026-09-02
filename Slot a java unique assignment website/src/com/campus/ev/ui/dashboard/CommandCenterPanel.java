package com.campus.ev.ui.dashboard;

import com.campus.ev.dao.ActivityLogDAO;
import com.campus.ev.model.ActivityLog;
import com.campus.ev.model.OperationalSummaryDTO;
import com.campus.ev.service.AnalyticsService;
import com.campus.ev.service.ChargingService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.*;
import com.campus.ev.util.CustomComponents.StatTelemetryCard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CommandCenterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AnalyticsService analyticsService = new AnalyticsService();
    private final ChargingService chargingService = new ChargingService();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    // KPI Telemetry Cards
    private StatTelemetryCard cardAvailable;
    private StatTelemetryCard cardOccupied;
    private StatTelemetryCard cardReserved;
    private StatTelemetryCard cardMaintenance;
    private StatTelemetryCard cardTodayEnergy;
    private StatTelemetryCard cardTodayRevenue;

    // Tables & Models
    private DefaultTableModel activityTableModel;
    private JTable activityTable;

    public CommandCenterPanel(MainFrame mainFrame) {
        super(new BorderLayout(16, 16));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // TOP: Header & Quick Actions
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("CAMPUS COMMAND CENTER");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Real-time telemetry, charging network status & activity stream");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickActions.setOpaque(false);

        JButton btnOpenGrid = UIHelper.createPrimaryButton("🗺 Open Charging Grid");
        btnOpenGrid.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_CHARGING_GRID));

        JButton btnNewRes = UIHelper.createSuccessButton("+ New Reservation");
        btnNewRes.addActionListener(e -> mainFrame.showNewReservationDialog(null));

        JButton btnStartCharge = UIHelper.createButton("⚡ Start Charge Session", UITheme.ACCENT_PURPLE, Color.WHITE);
        btnStartCharge.addActionListener(e -> mainFrame.showStartSessionDialog(null));

        quickActions.add(btnOpenGrid);
        quickActions.add(btnNewRes);
        quickActions.add(btnStartCharge);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(quickActions, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // CENTER WORKSPACE: Grid of KPI cards + Activity feed & Quick Operations
        JPanel centerGrid = new JPanel(new BorderLayout(16, 16));
        centerGrid.setOpaque(false);

        // 1. KPI Telemetry Bar (6 Cards)
        JPanel kpiPanel = new JPanel(new GridLayout(1, 6, 12, 0));
        kpiPanel.setOpaque(false);

        cardAvailable = new StatTelemetryCard("Available Nodes", "0", "Ready to charge", UITheme.ACCENT_EMERALD);
        cardOccupied = new StatTelemetryCard("Charging Active", "0", "In live session", UITheme.ACCENT_CYAN);
        cardReserved = new StatTelemetryCard("Reserved Slots", "0", "Booked by users", UITheme.ACCENT_AMBER);
        cardMaintenance = new StatTelemetryCard("Maintenance", "0", "Service required", UITheme.ACCENT_ROSE);
        cardTodayEnergy = new StatTelemetryCard("Today's Energy", "0.0 kWh", "Delivered from grid", UITheme.ACCENT_CYAN);
        cardTodayRevenue = new StatTelemetryCard("Today's Revenue", "₹0.00", "Settled & billed", UITheme.ACCENT_EMERALD);

        kpiPanel.add(cardAvailable);
        kpiPanel.add(cardOccupied);
        kpiPanel.add(cardReserved);
        kpiPanel.add(cardMaintenance);
        kpiPanel.add(cardTodayEnergy);
        kpiPanel.add(cardTodayRevenue);

        centerGrid.add(kpiPanel, BorderLayout.NORTH);

        // 2. Middle Split: Left = Live Activity Stream; Right = Operational Quick Hub & Sustainability
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerLocation(640);
        split.setResizeWeight(0.6);

        // LEFT: Live Activity Stream
        JPanel activityPanel = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lblActTitle = new JLabel("LIVE CAMPUS ACTIVITY STREAM (DATABASE)");
        lblActTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblActTitle.setForeground(UITheme.TEXT_CYAN);
        activityPanel.add(lblActTitle, BorderLayout.NORTH);

        String[] actCols = {"TIME", "EVENT", "DETAILS", "NODE", "USER", "SEVERITY"};
        activityTableModel = new DefaultTableModel(actCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        activityTable = new JTable(activityTableModel);
        UIHelper.styleTable(activityTable);
        activityTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        activityTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        activityTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        activityTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        activityTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        activityTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        activityPanel.add(UIHelper.createScrollPane(activityTable), BorderLayout.CENTER);
        split.setLeftComponent(activityPanel);

        // RIGHT: Quick Operational Shortcuts & SDG Impact Card
        JPanel rightOpsPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        rightOpsPanel.setOpaque(false);

        // Card 1: Fast Operations Hub
        JPanel opsHub = UIHelper.createCardPanel(new GridLayout(4, 1, 0, 8));
        JLabel lblOpsTitle = new JLabel("OPERATIONAL SHORTCUTS");
        lblOpsTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblOpsTitle.setForeground(UITheme.TEXT_CYAN);
        opsHub.add(lblOpsTitle);

        JButton btnGoLive = UIHelper.createSecondaryButton("⚡ Open Live Charging Cockpit");
        btnGoLive.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_LIVE_SESSIONS));
        opsHub.add(btnGoLive);

        JButton btnGoVehicles = UIHelper.createSecondaryButton("🚗 Register / Manage Campus Vehicles");
        btnGoVehicles.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_VEHICLES));
        opsHub.add(btnGoVehicles);

        JButton btnGoReports = UIHelper.createSecondaryButton("📊 Run Station Utilization (Stored Procedure)");
        btnGoReports.addActionListener(e -> {
            mainFrame.navigateTo(MainFrame.VIEW_REPORTS);
            mainFrame.selectReportTab(0);
        });
        opsHub.add(btnGoReports);
        rightOpsPanel.add(opsHub);

        // Card 2: Smart Campus & SDG Clean Energy Impact
        JPanel sdgCard = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lblSdgTitle = new JLabel("SUSTAINABILITY & GREEN ENERGY IMPACT");
        lblSdgTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblSdgTitle.setForeground(UITheme.ACCENT_EMERALD);
        sdgCard.add(lblSdgTitle, BorderLayout.NORTH);

        JTextArea txtSdg = new JTextArea(
            "• SDG 7 (Clean Energy): Powered by 150kW Campus Solar Rooftops.\n" +
            "• SDG 9 (Innovation): Smart dynamic tariff balancing & fast DC charging.\n" +
            "• SDG 11 (Smart Campus): 0% direct tailpipe emissions in university quad.\n" +
            "• Est. Carbon Offset: ~ 0.82 kg CO2 avoided per kWh delivered."
        );
        txtSdg.setEditable(false);
        txtSdg.setFont(UITheme.FONT_REGULAR);
        txtSdg.setForeground(UITheme.TEXT_SECONDARY);
        txtSdg.setBackground(UITheme.BG_CARD);
        txtSdg.setLineWrap(true);
        txtSdg.setWrapStyleWord(true);
        sdgCard.add(txtSdg, BorderLayout.CENTER);

        rightOpsPanel.add(sdgCard);
        split.setRightComponent(rightOpsPanel);

        centerGrid.add(split, BorderLayout.CENTER);
        add(centerGrid, BorderLayout.CENTER);
    }

    public void refreshData() {
        try {
            OperationalSummaryDTO summary = analyticsService.getOperationalSummary();
            if (summary != null) {
                cardAvailable.setValue(String.valueOf(summary.getAvailablePoints()));
                cardOccupied.setValue(String.valueOf(summary.getOccupiedPoints()));
                cardReserved.setValue(String.valueOf(summary.getReservedPoints()));
                cardMaintenance.setValue(String.valueOf(summary.getMaintenancePoints()));
                cardTodayEnergy.setValue(CurrencyUtil.formatKwh(summary.getTodayEnergyKwh()));
                cardTodayRevenue.setValue(CurrencyUtil.formatINR(summary.getTodayRevenue()));

                mainFrame.updateTopBarTelemetry(summary);
            }

            // Refresh Activity Table
            List<ActivityLog> logs = logDAO.getRecentLogs(25);
            activityTableModel.setRowCount(0);
            for (ActivityLog l : logs) {
                activityTableModel.addRow(new Object[]{
                    DateTimeUtil.formatTime(l.getLoggedAt()),
                    l.getEventType(),
                    l.getDescription(),
                    l.getPointCode() != null ? l.getPointCode() : "-",
                    l.getUserCode() != null ? l.getUserCode() : "-",
                    l.getSeverity()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice updating command center data: " + e.getMessage());
        }
    }
}
