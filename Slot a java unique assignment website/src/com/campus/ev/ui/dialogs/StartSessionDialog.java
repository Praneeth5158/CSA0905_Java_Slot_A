package com.campus.ev.ui.dialogs;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.VehicleDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.Vehicle;
import com.campus.ev.service.ChargingService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StartSessionDialog extends JDialog {

    private final MainFrame mainFrame;
    private final ChargingService chargingService = new ChargingService();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();

    private JComboBox<Vehicle> cmbVehicles;
    private JComboBox<ChargingPoint> cmbPoints;
    private JTextField txtInitialSoc;
    private JComboBox<String> cmbPaymentMethod;

    public StartSessionDialog(MainFrame mainFrame, ChargingPoint preselectedPoint) {
        super(mainFrame, "Initiate Charging Session", true);
        this.mainFrame = mainFrame;

        setSize(440, 360);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 6, 20));

        JLabel lblTitle = new JLabel("Start Live EV Charging");
        lblTitle.setFont(UITheme.FONT_HEADER_MED);
        lblTitle.setForeground(UITheme.ACCENT_CYAN);

        JLabel lblSub = new JLabel("Plug in connector, initialize battery telemetry, and start grid flow");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        header.add(lblTitle);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // FORM FIELDS
        JPanel fields = new JPanel(new GridLayout(4, 2, 8, 10));
        fields.setOpaque(false);
        fields.setBorder(new EmptyBorder(10, 20, 10, 20));

        fields.add(UIHelper.createFormLabel("Select EV Vehicle:"));
        cmbVehicles = new JComboBox<>();
        cmbVehicles.setFont(UITheme.FONT_REGULAR);
        cmbVehicles.setBackground(UITheme.BG_INPUT);
        cmbVehicles.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbVehicles);

        fields.add(UIHelper.createFormLabel("Charging Node:"));
        cmbPoints = new JComboBox<>();
        cmbPoints.setFont(UITheme.FONT_REGULAR);
        cmbPoints.setBackground(UITheme.BG_INPUT);
        cmbPoints.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbPoints);

        fields.add(UIHelper.createFormLabel("Current Battery SOC (%):"));
        txtInitialSoc = UIHelper.createTextField(6);
        txtInitialSoc.setText("25");
        fields.add(txtInitialSoc);

        fields.add(UIHelper.createFormLabel("Billing Payment Method:"));
        cmbPaymentMethod = new JComboBox<>(new String[]{"CAMPUS_WALLET", "UPI_QR", "SMART_ID_CARD", "STUDENT_PORTAL", "WAIVED_FLEET"});
        cmbPaymentMethod.setFont(UITheme.FONT_REGULAR);
        cmbPaymentMethod.setBackground(UITheme.BG_INPUT);
        cmbPaymentMethod.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbPaymentMethod);

        add(fields, BorderLayout.CENTER);

        // ACTIONS
        JPanel footer = new JPanel(new GridLayout(1, 2, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 20, 16, 20));

        JButton btnStart = UIHelper.createPrimaryButton("⚡ Begin Charging");
        btnStart.addActionListener(e -> onStartSession());

        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnStart);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);

        loadData(preselectedPoint);
    }

    private void loadData(ChargingPoint preselected) {
        try {
            List<Vehicle> vList = vehicleDAO.getAllVehicles();
            cmbVehicles.removeAllItems();
            for (Vehicle v : vList) cmbVehicles.addItem(v);

            List<ChargingPoint> pList = pointDAO.getAllPointsWithDetails();
            cmbPoints.removeAllItems();
            int selectedIndex = 0;
            for (int i = 0; i < pList.size(); i++) {
                ChargingPoint cp = pList.get(i);
                cmbPoints.addItem(cp);
                if (preselected != null && cp.getPointId() == preselected.getPointId()) {
                    selectedIndex = i;
                }
            }
            if (!pList.isEmpty()) cmbPoints.setSelectedIndex(selectedIndex);
        } catch (Exception ignored) {}
    }

    private void onStartSession() {
        Vehicle v = (Vehicle) cmbVehicles.getSelectedItem();
        ChargingPoint cp = (ChargingPoint) cmbPoints.getSelectedItem();

        if (v == null || cp == null) {
            UIHelper.showError(this, "Please select a valid vehicle and charging point.");
            return;
        }

        int soc = 20;
        try {
            soc = Integer.parseInt(txtInitialSoc.getText().trim());
            if (soc < 0 || soc > 100) throw new Exception();
        } catch (Exception e) {
            UIHelper.showError(this, "State of Charge (SOC) must be an integer percentage between 0 and 100.");
            return;
        }

        try {
            int sessionId = chargingService.startChargingSession(
                cp.getPointId(), v.getVehicleId(), v.getUserId(), null, soc, (String) cmbPaymentMethod.getSelectedItem()
            );

            UIHelper.showSuccess(this, "Charging session initiated successfully!\nSession ID: #" + sessionId);
            mainFrame.refreshAllViews();
            mainFrame.navigateTo(MainFrame.VIEW_LIVE_SESSIONS);
            dispose();
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to start charging session: " + ex.getMessage());
        }
    }
}
