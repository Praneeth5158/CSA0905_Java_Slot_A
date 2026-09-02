package com.campus.ev.ui.dialogs;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.VehicleDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.Vehicle;
import com.campus.ev.service.ReservationService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class NewReservationDialog extends JDialog {

    private final MainFrame mainFrame;
    private final ReservationService reservationService = new ReservationService();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();

    private JComboBox<Vehicle> cmbVehicles;
    private JComboBox<ChargingPoint> cmbPoints;
    private JComboBox<String> cmbDate;
    private JComboBox<String> cmbStartTime;
    private JComboBox<String> cmbEndTime;
    private JTextField txtEstKwh;

    public NewReservationDialog(MainFrame mainFrame, ChargingPoint preselectedPoint) {
        super(mainFrame, "Quick Slot Reservation", true);
        this.mainFrame = mainFrame;

        setSize(440, 390);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 6, 20));

        JLabel lblTitle = new JLabel("Reserve Charging Point");
        lblTitle.setFont(UITheme.FONT_HEADER_MED);
        lblTitle.setForeground(UITheme.TEXT_CYAN);

        JLabel lblSub = new JLabel("Select vehicle, charging point, and 1-4 hour schedule");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        header.add(lblTitle);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // FORM FIELDS
        JPanel fields = new JPanel(new GridLayout(6, 2, 8, 10));
        fields.setOpaque(false);
        fields.setBorder(new EmptyBorder(10, 20, 10, 20));

        fields.add(UIHelper.createFormLabel("Vehicle:"));
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

        fields.add(UIHelper.createFormLabel("Date:"));
        cmbDate = new JComboBox<>(new String[]{"Today", "Tomorrow", "In 2 Days"});
        cmbDate.setFont(UITheme.FONT_REGULAR);
        cmbDate.setBackground(UITheme.BG_INPUT);
        cmbDate.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbDate);

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) hours[i] = String.format("%02d:00", i);

        fields.add(UIHelper.createFormLabel("Start Time:"));
        cmbStartTime = new JComboBox<>(hours);
        cmbStartTime.setSelectedIndex(10);
        cmbStartTime.setFont(UITheme.FONT_REGULAR);
        cmbStartTime.setBackground(UITheme.BG_INPUT);
        cmbStartTime.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStartTime);

        fields.add(UIHelper.createFormLabel("End Time:"));
        cmbEndTime = new JComboBox<>(hours);
        cmbEndTime.setSelectedIndex(11);
        cmbEndTime.setFont(UITheme.FONT_REGULAR);
        cmbEndTime.setBackground(UITheme.BG_INPUT);
        cmbEndTime.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbEndTime);

        fields.add(UIHelper.createFormLabel("Est. Energy (kWh):"));
        txtEstKwh = UIHelper.createTextField(6);
        txtEstKwh.setText("20.0");
        fields.add(txtEstKwh);

        add(fields, BorderLayout.CENTER);

        // ACTIONS
        JPanel footer = new JPanel(new GridLayout(1, 2, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 20, 16, 20));

        JButton btnBook = UIHelper.createSuccessButton("Confirm Reservation");
        btnBook.addActionListener(e -> onConfirmBooking());

        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnBook);
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

    private void onConfirmBooking() {
        Vehicle v = (Vehicle) cmbVehicles.getSelectedItem();
        ChargingPoint cp = (ChargingPoint) cmbPoints.getSelectedItem();

        if (v == null || cp == null) {
            UIHelper.showError(this, "Please select a valid vehicle and charging point.");
            return;
        }

        int startH = cmbStartTime.getSelectedIndex();
        int endH = cmbEndTime.getSelectedIndex();
        if (endH <= startH) {
            UIHelper.showError(this, "End time must be after start time.");
            return;
        }

        double estKwh;
        try {
            estKwh = Double.parseDouble(txtEstKwh.getText().trim());
        } catch (Exception e) {
            UIHelper.showError(this, "Invalid estimated kWh value.");
            return;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.add(Calendar.DAY_OF_YEAR, cmbDate.getSelectedIndex());
        calStart.set(Calendar.HOUR_OF_DAY, startH);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);

        Calendar calEnd = Calendar.getInstance();
        calEnd.add(Calendar.DAY_OF_YEAR, cmbDate.getSelectedIndex());
        calEnd.set(Calendar.HOUR_OF_DAY, endH);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);

        Timestamp startTs = new Timestamp(calStart.getTimeInMillis());
        Timestamp endTs = new Timestamp(calEnd.getTimeInMillis());

        try {
            int resId = reservationService.createReservation(
                v.getUserId(), v.getVehicleId(), cp.getPointId(), cp.getStationId(),
                startTs, endTs, estKwh, v.getOwnerName(), v.getOwnerCode(), v.getVehicleNumber(), cp.getPointCode()
            );

            UIHelper.showSuccess(this, "Reservation confirmed successfully! ID: #" + resId);
            mainFrame.refreshAllViews();
            dispose();
        } catch (Exception ex) {
            UIHelper.showError(this, "Reservation Failed: " + ex.getMessage());
        }
    }
}
