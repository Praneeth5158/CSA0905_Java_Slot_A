package com.campus.ev.ui.grid;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.Payment;
import com.campus.ev.service.ChargingService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.*;
import com.campus.ev.util.CustomComponents.StatusBadge;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class NodeInspectorPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ChargingService chargingService = new ChargingService();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();

    private ChargingPoint currentPoint = null;

    // UI Fields
    private final JLabel lblPointCode;
    private final StatusBadge badgeStatus;
    private final JLabel lblStationZone;
    private final JLabel lblConnectorType;
    private final JLabel lblPowerRating;
    private final JLabel lblHardwareModel;
    private final JLabel lblFastCharger;

    // Live Telemetry Box
    private final JPanel activeSessionBox;
    private final JLabel lblActiveVehicle;
    private final JLabel lblActiveDriver;
    private final JLabel lblActiveStartTime;
    private final JLabel lblLiveKwh;
    private final JLabel lblLiveCost;

    // Actions
    private final JButton btnReserve;
    private final JButton btnStartSession;
    private final JButton btnStopSession;
    private final JButton btnToggleMaintenance;

    public NodeInspectorPanel(MainFrame mainFrame) {
        super(new BorderLayout(12, 12));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        setPreferredSize(new Dimension(320, 0));

        // HEADER: Point ID & Status Badge
        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setOpaque(false);

        lblPointCode = new JLabel("SELECT A NODE");
        lblPointCode.setFont(UITheme.FONT_HEADER_LARGE);
        lblPointCode.setForeground(UITheme.TEXT_PRIMARY);

        badgeStatus = new StatusBadge("IDLE");

        lblStationZone = new JLabel("Click any charging point on the map to inspect");
        lblStationZone.setFont(UITheme.FONT_SMALL);
        lblStationZone.setForeground(UITheme.TEXT_MUTED);

        JPanel codeGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        codeGroup.setOpaque(false);
        codeGroup.add(lblPointCode);
        codeGroup.add(badgeStatus);

        headerPanel.add(codeGroup, BorderLayout.NORTH);
        headerPanel.add(lblStationZone, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // BODY: Technical Specs & Active Session Telemetry
        JPanel bodyPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        bodyPanel.setOpaque(false);

        // 1. Technical Specs Table
        JPanel specsCard = UIHelper.createDarkPanel(new GridLayout(4, 2, 8, 6));
        specsCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        specsCard.add(createPropLabel("Connector:"));
        lblConnectorType = createValueLabel("-");
        specsCard.add(lblConnectorType);

        specsCard.add(createPropLabel("Power Capacity:"));
        lblPowerRating = createValueLabel("-");
        specsCard.add(lblPowerRating);

        specsCard.add(createPropLabel("Charger Mode:"));
        lblFastCharger = createValueLabel("-");
        specsCard.add(lblFastCharger);

        specsCard.add(createPropLabel("Hardware Model:"));
        lblHardwareModel = createValueLabel("-");
        specsCard.add(lblHardwareModel);

        bodyPanel.add(specsCard);

        // 2. Active Session Telemetry Box
        activeSessionBox = UIHelper.createCardPanel(new GridLayout(5, 2, 6, 6));
        activeSessionBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_GLOW, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));

        activeSessionBox.add(createPropLabel("Active Vehicle:"));
        lblActiveVehicle = createValueLabel("-");
        activeSessionBox.add(lblActiveVehicle);

        activeSessionBox.add(createPropLabel("Driver / User:"));
        lblActiveDriver = createValueLabel("-");
        activeSessionBox.add(lblActiveDriver);

        activeSessionBox.add(createPropLabel("Start Time:"));
        lblActiveStartTime = createValueLabel("-");
        activeSessionBox.add(lblActiveStartTime);

        activeSessionBox.add(createPropLabel("Energy Flow:"));
        lblLiveKwh = createValueLabel("-");
        lblLiveKwh.setForeground(UITheme.ACCENT_CYAN);
        activeSessionBox.add(lblLiveKwh);

        activeSessionBox.add(createPropLabel("Current Bill:"));
        lblLiveCost = createValueLabel("-");
        lblLiveCost.setForeground(UITheme.ACCENT_EMERALD);
        activeSessionBox.add(lblLiveCost);

        bodyPanel.add(activeSessionBox);
        add(bodyPanel, BorderLayout.CENTER);

        // FOOTER: Contextual Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        actionPanel.setOpaque(false);

        btnReserve = UIHelper.createWarningButton("📅 Reserve This Node");
        btnReserve.addActionListener(e -> onReserveClicked());

        btnStartSession = UIHelper.createPrimaryButton("⚡ Start Live Charging");
        btnStartSession.addActionListener(e -> onStartSessionClicked());

        btnStopSession = UIHelper.createDangerButton("⏹ Stop Charging & Bill");
        btnStopSession.addActionListener(e -> onStopSessionClicked());

        btnToggleMaintenance = UIHelper.createSecondaryButton("🛠 Toggle Maintenance");
        btnToggleMaintenance.addActionListener(e -> onToggleMaintenanceClicked());

        actionPanel.add(btnReserve);
        actionPanel.add(btnStartSession);
        actionPanel.add(btnStopSession);
        actionPanel.add(btnToggleMaintenance);

        add(actionPanel, BorderLayout.SOUTH);
        setInspectPoint(null);
    }

    public void setInspectPoint(ChargingPoint cp) {
        this.currentPoint = cp;
        if (cp == null) {
            lblPointCode.setText("SELECT NODE");
            badgeStatus.setStatus("IDLE");
            lblStationZone.setText("Click any charging point on the map to inspect");
            lblConnectorType.setText("-");
            lblPowerRating.setText("-");
            lblFastCharger.setText("-");
            lblHardwareModel.setText("-");
            lblActiveVehicle.setText("-");
            lblActiveDriver.setText("-");
            lblActiveStartTime.setText("-");
            lblLiveKwh.setText("-");
            lblLiveCost.setText("-");

            btnReserve.setEnabled(false);
            btnStartSession.setEnabled(false);
            btnStopSession.setEnabled(false);
            btnToggleMaintenance.setEnabled(false);
            return;
        }

        lblPointCode.setText(cp.getPointCode());
        badgeStatus.setStatus(cp.getStatus());
        lblStationZone.setText(cp.getStationName() + " (" + cp.getCampusZone() + ")");
        lblConnectorType.setText(cp.getConnectorType());
        lblPowerRating.setText(CurrencyUtil.formatKw(cp.getPowerRatingKw()));
        lblFastCharger.setText(cp.isFastCharger() ? "DC Fast Charger" : "Standard AC");
        lblHardwareModel.setText(cp.getHardwareModel() != null ? cp.getHardwareModel() : "Generic EVSE");

        boolean isOccupied = "OCCUPIED".equalsIgnoreCase(cp.getStatus()) && cp.getActiveSessionId() != null;
        if (isOccupied) {
            lblActiveVehicle.setText(cp.getActiveVehicleNumber() != null ? cp.getActiveVehicleNumber() : "Active EV");
            lblActiveDriver.setText(cp.getActiveUserName() != null ? cp.getActiveUserName() : "Campus User");
            lblActiveStartTime.setText(DateTimeUtil.formatTimeShort(cp.getActiveSessionStartTime()));
            lblLiveKwh.setText(CurrencyUtil.formatKwh(cp.getActiveEnergyKwh()));
            lblLiveCost.setText(CurrencyUtil.formatINR(cp.getActiveCost()));
        } else {
            lblActiveVehicle.setText("No Active Session");
            lblActiveDriver.setText("-");
            lblActiveStartTime.setText("-");
            lblLiveKwh.setText("0.000 kWh");
            lblLiveCost.setText("₹0.00");
        }

        // Enable / Disable buttons contextually
        btnReserve.setEnabled("AVAILABLE".equalsIgnoreCase(cp.getStatus()));
        btnStartSession.setEnabled("AVAILABLE".equalsIgnoreCase(cp.getStatus()) || "RESERVED".equalsIgnoreCase(cp.getStatus()));
        btnStopSession.setEnabled(isOccupied);
        btnToggleMaintenance.setEnabled(true);
        btnToggleMaintenance.setText("MAINTENANCE".equalsIgnoreCase(cp.getStatus()) ? "✅ Restore Node" : "🛠 Set Maintenance");
    }

    private void onReserveClicked() {
        if (currentPoint != null) {
            mainFrame.showNewReservationDialog(currentPoint);
        }
    }

    private void onStartSessionClicked() {
        if (currentPoint != null) {
            mainFrame.showStartSessionDialog(currentPoint);
        }
    }

    private void onStopSessionClicked() {
        if (currentPoint == null || currentPoint.getActiveSessionId() == null) return;
        boolean confirm = UIHelper.showConfirm(this, 
            "Are you sure you want to stop charging on " + currentPoint.getPointCode() + "?\n" +
            "This will calculate the final bill and settle the invoice transactionally.", 
            "Stop Charging Session");
        if (confirm) {
            try {
                Payment payment = chargingService.stopChargingSession(currentPoint.getActiveSessionId(), "CAMPUS_WALLET");
                UIHelper.showSuccess(this, 
                    "Session completed successfully!\n" +
                    "Invoice: " + payment.getInvoiceNumber() + "\n" +
                    "Total Amount: " + CurrencyUtil.formatINR(payment.getAmount()) + "\n" +
                    "Payment Status: " + payment.getPaymentStatus());
                mainFrame.refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Failed to stop charging session: " + ex.getMessage());
            }
        }
    }

    private void onToggleMaintenanceClicked() {
        if (currentPoint == null) return;
        String newStatus = "MAINTENANCE".equalsIgnoreCase(currentPoint.getStatus()) ? "AVAILABLE" : "MAINTENANCE";
        try {
            pointDAO.updatePointStatus(currentPoint.getPointId(), newStatus);
            mainFrame.refreshAllViews();
            UIHelper.showSuccess(this, "Charging point " + currentPoint.getPointCode() + " status updated to " + newStatus);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to update status: " + ex.getMessage());
        }
    }

    private JLabel createPropLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_REGULAR_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }
}
