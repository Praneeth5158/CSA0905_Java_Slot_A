package com.campus.ev.ui.session;

import com.campus.ev.model.ChargingSession;
import com.campus.ev.model.Payment;
import com.campus.ev.service.ChargingService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.*;
import com.campus.ev.util.CustomComponents.CircularPowerMeter;
import com.campus.ev.util.CustomComponents.StatTelemetryCard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LiveSessionCockpitPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ChargingService chargingService = new ChargingService();

    private JComboBox<ChargingSession> cmbActiveSessions;
    private ChargingSession currentSession = null;

    // Cockpit Gauges & Cards
    private CircularPowerMeter powerMeter;
    private StatTelemetryCard cardDuration;
    private StatTelemetryCard cardEnergyKwh;
    private StatTelemetryCard cardCurrentCost;
    private StatTelemetryCard cardSocProgress;

    // Telemetry Specs
    private JLabel lblVehicleDetail;
    private JLabel lblDriverDetail;
    private JLabel lblStationDetail;
    private JLabel lblTariffDetail;

    // Actions
    private JButton btnStopSession;
    private JButton btnPauseSession;

    // Table
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;

    public LiveSessionCockpitPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        refreshSessions();

        // Register background telemetry update listener for real-time meters
        ChargingService.addTelemetryListener(() -> {
            SwingUtilities.invokeLater(this::updateLiveCockpitTelemetry);
        });
    }

    private void initComponents() {
        // TOP: Header & Session Switcher
        JPanel topHeader = new JPanel(new BorderLayout(12, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("LIVE CHARGING COCKPIT");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Real-time EV telemetry flow, power grid load, and live transactional billing");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel selectorGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        selectorGroup.setOpaque(false);
        selectorGroup.add(new JLabel("Active Charging Stream:"));

        cmbActiveSessions = new JComboBox<>();
        cmbActiveSessions.setFont(UITheme.FONT_REGULAR);
        cmbActiveSessions.setBackground(UITheme.BG_INPUT);
        cmbActiveSessions.setForeground(UITheme.TEXT_PRIMARY);
        cmbActiveSessions.setPreferredSize(new Dimension(300, 32));
        cmbActiveSessions.addActionListener(e -> onSessionSelected());
        selectorGroup.add(cmbActiveSessions);

        JButton btnStartNew = UIHelper.createSuccessButton("+ Launch New Session");
        btnStartNew.addActionListener(e -> mainFrame.showStartSessionDialog(null));
        selectorGroup.add(btnStartNew);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(selectorGroup, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // MAIN COCKPIT SECTION: Top Gauges & Action Hub + Bottom Session History
        JPanel mainContent = new JPanel(new BorderLayout(12, 12));
        mainContent.setOpaque(false);

        // 1. Cockpit Central Workspace Card
        JPanel cockpitCard = UIHelper.createCardPanel(new BorderLayout(14, 14));
        cockpitCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_GLOW, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        // West: Circular Power Flow Meter
        JPanel meterPanel = new JPanel(new BorderLayout(6, 6));
        meterPanel.setOpaque(false);
        meterPanel.setPreferredSize(new Dimension(180, 0));
        powerMeter = new CircularPowerMeter();
        meterPanel.add(powerMeter, BorderLayout.CENTER);
        cockpitCard.add(meterPanel, BorderLayout.WEST);

        // Center: 4 KPI Telemetry Cards
        JPanel gaugesGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        gaugesGrid.setOpaque(false);

        cardDuration = new StatTelemetryCard("Elapsed Charging Time", "00:00:00", "Connected duration", UITheme.ACCENT_CYAN);
        cardEnergyKwh = new StatTelemetryCard("Energy Delivered", "0.000 kWh", "Cumulative grid flow", UITheme.ACCENT_PURPLE);
        cardCurrentCost = new StatTelemetryCard("Running Bill Amount", "₹0.00", "Computed tariff total", UITheme.ACCENT_EMERALD);
        cardSocProgress = new StatTelemetryCard("State of Charge (SOC)", "0%", "Estimated battery level", UITheme.ACCENT_AMBER);

        gaugesGrid.add(cardDuration);
        gaugesGrid.add(cardEnergyKwh);
        gaugesGrid.add(cardCurrentCost);
        gaugesGrid.add(cardSocProgress);
        cockpitCard.add(gaugesGrid, BorderLayout.CENTER);

        // East: Identity & Actions
        JPanel eastPanel = new JPanel(new BorderLayout(10, 10));
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(280, 0));

        JPanel specsPanel = UIHelper.createDarkPanel(new GridLayout(4, 1, 0, 4));
        specsPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        lblVehicleDetail = new JLabel("Vehicle: -");
        lblVehicleDetail.setFont(UITheme.FONT_SMALL);
        lblVehicleDetail.setForeground(UITheme.TEXT_PRIMARY);

        lblDriverDetail = new JLabel("Driver: -");
        lblDriverDetail.setFont(UITheme.FONT_SMALL);
        lblDriverDetail.setForeground(UITheme.TEXT_SECONDARY);

        lblStationDetail = new JLabel("Node: -");
        lblStationDetail.setFont(UITheme.FONT_SMALL);
        lblStationDetail.setForeground(UITheme.TEXT_SECONDARY);

        lblTariffDetail = new JLabel("Tariff: -");
        lblTariffDetail.setFont(UITheme.FONT_SMALL);
        lblTariffDetail.setForeground(UITheme.TEXT_CYAN);

        specsPanel.add(lblVehicleDetail);
        specsPanel.add(lblDriverDetail);
        specsPanel.add(lblStationDetail);
        specsPanel.add(lblTariffDetail);
        eastPanel.add(specsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionButtons = new JPanel(new GridLayout(2, 1, 0, 6));
        actionButtons.setOpaque(false);

        btnPauseSession = UIHelper.createSecondaryButton("⏸ Pause Session");
        btnPauseSession.addActionListener(e -> {
            UIHelper.showInfo(this, "Session state paused. Power delivery throttled.", "Cockpit Control");
        });

        btnStopSession = UIHelper.createDangerButton("⏹ STOP CHARGING & SETTLE BILL");
        btnStopSession.addActionListener(e -> onStopCurrentSession());

        actionButtons.add(btnPauseSession);
        actionButtons.add(btnStopSession);
        eastPanel.add(actionButtons, BorderLayout.SOUTH);

        cockpitCard.add(eastPanel, BorderLayout.EAST);
        mainContent.add(cockpitCard, BorderLayout.NORTH);

        // 2. Bottom: All Sessions History Table
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lblHistory = new JLabel("CAMPUS CHARGING SESSIONS LOG");
        lblHistory.setFont(UITheme.FONT_HEADER_SMALL);
        lblHistory.setForeground(UITheme.TEXT_CYAN);
        tableCard.add(lblHistory, BorderLayout.NORTH);

        String[] cols = {"SESSION ID", "CODE", "NODE", "STATION", "VEHICLE", "USER", "START TIME", "ENERGY (kWh)", "RATE (₹/kWh)", "TOTAL (₹)", "STATUS"};
        sessionTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sessionTable = new JTable(sessionTableModel);
        UIHelper.styleTable(sessionTable);
        tableCard.add(UIHelper.createScrollPane(sessionTable), BorderLayout.CENTER);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void onSessionSelected() {
        currentSession = (ChargingSession) cmbActiveSessions.getSelectedItem();
        updateLiveCockpitTelemetry();
    }

    private void updateLiveCockpitTelemetry() {
        if (currentSession == null) {
            powerMeter.updateMeter(0, "0.0 kW", UITheme.TEXT_MUTED);
            cardDuration.setValue("00:00:00");
            cardEnergyKwh.setValue("0.000 kWh");
            cardCurrentCost.setValue("₹0.00");
            cardSocProgress.setValue("0%");
            lblVehicleDetail.setText("Vehicle: None Active");
            lblDriverDetail.setText("Driver: -");
            lblStationDetail.setText("Node: -");
            lblTariffDetail.setText("Tariff: -");

            btnPauseSession.setEnabled(false);
            btnStopSession.setEnabled(false);
            return;
        }

        btnPauseSession.setEnabled(true);
        btnStopSession.setEnabled(true);

        long startMs = currentSession.getStartTime().getTime();
        long nowMs = System.currentTimeMillis();
        long elapsedSecs = Math.max(0, (nowMs - startMs) / 1000);

        cardDuration.setValue(DateTimeUtil.formatDurationSeconds(elapsedSecs));
        cardEnergyKwh.setValue(CurrencyUtil.formatKwh(currentSession.getTotalEnergyKwh()));
        cardCurrentCost.setValue(CurrencyUtil.formatINR(currentSession.getTotalAmount()));

        int estSoc = Math.min(100, currentSession.getInitialSocPercent() + (int)(currentSession.getTotalEnergyKwh() * 1.8));
        cardSocProgress.setValue(estSoc + "%");

        double powerKw = currentSession.getPeakPowerKw() > 0 ? currentSession.getPeakPowerKw() : 45.0;
        powerMeter.updateMeter((powerKw / 120.0) * 100.0, String.format("%.1f kW", powerKw), UITheme.ACCENT_CYAN);

        lblVehicleDetail.setText("Vehicle: " + (currentSession.getVehicleNumber() != null ? currentSession.getVehicleNumber() : "EV-Fleet"));
        lblDriverDetail.setText("Driver: " + (currentSession.getUserName() != null ? currentSession.getUserName() : "Campus User"));
        lblStationDetail.setText("Node: " + (currentSession.getPointCode() != null ? currentSession.getPointCode() : "CP-Node") + " (" + currentSession.getStationName() + ")");
        lblTariffDetail.setText("Tariff: " + (currentSession.getTariffName() != null ? currentSession.getTariffName() : "Standard Rate"));
    }

    private void onStopCurrentSession() {
        if (currentSession == null) return;

        boolean confirm = UIHelper.showConfirm(this,
            "Stop charging session #" + currentSession.getSessionCode() + "?\n" +
            "This will calculate the final bill, update database records transactionally, and generate an invoice.",
            "Confirm Session Completion");

        if (confirm) {
            try {
                Payment payment = chargingService.stopChargingSession(currentSession.getSessionId(), "CAMPUS_WALLET");
                UIHelper.showSuccess(this,
                    "Charging session successfully stopped!\n\n" +
                    "Invoice ID: " + payment.getInvoiceNumber() + "\n" +
                    "Total Energy: " + CurrencyUtil.formatKwh(currentSession.getTotalEnergyKwh()) + "\n" +
                    "Final Settled Amount: " + CurrencyUtil.formatINR(payment.getAmount()) + "\n" +
                    "Transaction Ref: " + payment.getTransactionRef() + "\n" +
                    "Payment Method: " + payment.getPaymentMethod() + " (" + payment.getPaymentStatus() + ")");
                
                mainFrame.refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Failed to complete session: " + ex.getMessage());
            }
        }
    }

    public void refreshSessions() {
        try {
            List<ChargingSession> activeList = chargingService.getActiveSessions();
            cmbActiveSessions.removeAllItems();
            for (ChargingSession s : activeList) {
                cmbActiveSessions.addItem(s);
            }

            if (!activeList.isEmpty()) {
                currentSession = activeList.get(0);
            } else {
                currentSession = null;
            }
            updateLiveCockpitTelemetry();

            // Refresh All Sessions History Table
            List<ChargingSession> allSessions = chargingService.getAllSessions();
            sessionTableModel.setRowCount(0);
            for (ChargingSession s : allSessions) {
                sessionTableModel.addRow(new Object[]{
                    s.getSessionId(),
                    s.getSessionCode(),
                    s.getPointCode(),
                    s.getStationName(),
                    s.getVehicleNumber(),
                    s.getUserName(),
                    DateTimeUtil.formatDateTime(s.getStartTime()),
                    CurrencyUtil.formatKwh(s.getTotalEnergyKwh()),
                    "₹" + String.format("%.2f", s.getTariffRate()),
                    CurrencyUtil.formatINR(s.getTotalAmount()),
                    s.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing live cockpit: " + e.getMessage());
        }
    }
}
