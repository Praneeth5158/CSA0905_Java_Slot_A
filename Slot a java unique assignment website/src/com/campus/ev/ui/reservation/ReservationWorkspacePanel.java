package com.campus.ev.ui.reservation;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.ChargingStationDAO;
import com.campus.ev.dao.VehicleDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.ChargingStation;
import com.campus.ev.model.Reservation;
import com.campus.ev.model.Vehicle;
import com.campus.ev.service.ReservationService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.DateTimeUtil;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationWorkspacePanel extends JPanel {

    private final MainFrame mainFrame;
    private final ReservationService reservationService = new ReservationService();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ChargingStationDAO stationDAO = new ChargingStationDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();

    // Form Controls
    private JComboBox<Vehicle> cmbVehicles;
    private JComboBox<ChargingStation> cmbStations;
    private JComboBox<ChargingPoint> cmbPoints;
    private JComboBox<String> cmbDate;
    private JComboBox<String> cmbStartTime;
    private JComboBox<String> cmbEndTime;
    private JTextField txtEstKwh;
    private JLabel lblConflictStatus;
    private JButton btnBook;

    // Timeline & Table
    private TimeSlotGridComponent timeSlotGrid;
    private DefaultTableModel reservationTableModel;
    private JTable reservationTable;
    private JTextField txtSearch;

    private List<ChargingPoint> cachedPoints;

    public ReservationWorkspacePanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        loadFormData();
        refreshReservations();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("SMART RESERVATION WORKSPACE");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Schedule EV charging slots with automated conflict detection and timeline matrix");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search Bookings:"));
        txtSearch = UIHelper.createTextField(14);
        JButton btnSearch = UIHelper.createSecondaryButton("🔍 Filter");
        btnSearch.addActionListener(e -> refreshReservations());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(searchPanel, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // MAIN SPLIT: Left Booking Form (400px), Right Timeline Matrix & Bookings Table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.35);

        // --- LEFT PANE: Booking Form ---
        JPanel formCard = UIHelper.createCardPanel(new BorderLayout(10, 10));
        JLabel lblFormHeader = new JLabel("BOOK CHARGING SLOT");
        lblFormHeader.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormHeader.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormHeader, BorderLayout.NORTH);

        JPanel formFields = new JPanel(new GridLayout(7, 2, 8, 10));
        formFields.setOpaque(false);

        formFields.add(UIHelper.createFormLabel("Select EV Vehicle:"));
        cmbVehicles = new JComboBox<>();
        cmbVehicles.setFont(UITheme.FONT_REGULAR);
        cmbVehicles.setBackground(UITheme.BG_INPUT);
        cmbVehicles.setForeground(UITheme.TEXT_PRIMARY);
        formFields.add(cmbVehicles);

        formFields.add(UIHelper.createFormLabel("Campus Station:"));
        cmbStations = new JComboBox<>();
        cmbStations.setFont(UITheme.FONT_REGULAR);
        cmbStations.setBackground(UITheme.BG_INPUT);
        cmbStations.setForeground(UITheme.TEXT_PRIMARY);
        cmbStations.addActionListener(e -> onStationChanged());
        formFields.add(cmbStations);

        formFields.add(UIHelper.createFormLabel("Charging Node:"));
        cmbPoints = new JComboBox<>();
        cmbPoints.setFont(UITheme.FONT_REGULAR);
        cmbPoints.setBackground(UITheme.BG_INPUT);
        cmbPoints.setForeground(UITheme.TEXT_PRIMARY);
        cmbPoints.addActionListener(e -> onPointChanged());
        formFields.add(cmbPoints);

        formFields.add(UIHelper.createFormLabel("Reservation Date:"));
        cmbDate = new JComboBox<>(new String[]{"Today", "Tomorrow", "In 2 Days"});
        cmbDate.setFont(UITheme.FONT_REGULAR);
        cmbDate.setBackground(UITheme.BG_INPUT);
        cmbDate.setForeground(UITheme.TEXT_PRIMARY);
        cmbDate.addActionListener(e -> onPointChanged());
        formFields.add(cmbDate);

        formFields.add(UIHelper.createFormLabel("Start Time:"));
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) hours[i] = String.format("%02d:00", i);
        cmbStartTime = new JComboBox<>(hours);
        cmbStartTime.setSelectedIndex(10);
        cmbStartTime.setFont(UITheme.FONT_REGULAR);
        cmbStartTime.setBackground(UITheme.BG_INPUT);
        cmbStartTime.setForeground(UITheme.TEXT_PRIMARY);
        cmbStartTime.addActionListener(e -> checkConflictPreview());
        formFields.add(cmbStartTime);

        formFields.add(UIHelper.createFormLabel("End Time:"));
        cmbEndTime = new JComboBox<>(hours);
        cmbEndTime.setSelectedIndex(11);
        cmbEndTime.setFont(UITheme.FONT_REGULAR);
        cmbEndTime.setBackground(UITheme.BG_INPUT);
        cmbEndTime.setForeground(UITheme.TEXT_PRIMARY);
        cmbEndTime.addActionListener(e -> checkConflictPreview());
        formFields.add(cmbEndTime);

        formFields.add(UIHelper.createFormLabel("Est. Energy (kWh):"));
        txtEstKwh = UIHelper.createTextField(6);
        txtEstKwh.setText("20.0");
        formFields.add(txtEstKwh);

        formCard.add(formFields, BorderLayout.CENTER);

        // Form Footer: Conflict Banner & Book Button
        JPanel formFooter = new JPanel(new GridLayout(2, 1, 0, 8));
        formFooter.setOpaque(false);

        lblConflictStatus = new JLabel("● Slot Available - No conflicts detected", SwingConstants.CENTER);
        lblConflictStatus.setFont(UITheme.FONT_SMALL);
        lblConflictStatus.setForeground(UITheme.ACCENT_EMERALD);
        formFooter.add(lblConflictStatus);

        btnBook = UIHelper.createSuccessButton("✔ Confirm & Lock Reservation");
        btnBook.addActionListener(e -> onConfirmBooking());
        formFooter.add(btnBook);

        formCard.add(formFooter, BorderLayout.SOUTH);
        splitPane.setLeftComponent(formCard);

        // --- RIGHT PANE: Timeline Matrix & Existing Reservations Table ---
        JPanel rightPane = new JPanel(new BorderLayout(10, 12));
        rightPane.setOpaque(false);

        // Timeline Matrix Component
        timeSlotGrid = new TimeSlotGridComponent();
        timeSlotGrid.setOnSlotClicked(hour -> {
            cmbStartTime.setSelectedIndex(hour);
            cmbEndTime.setSelectedIndex(Math.min(23, hour + 1));
            checkConflictPreview();
        });
        rightPane.add(timeSlotGrid, BorderLayout.NORTH);

        // Table Panel
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);

        JLabel lblTblTitle = new JLabel("ACTIVE & SCHEDULED CAMPUS RESERVATIONS");
        lblTblTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblTblTitle.setForeground(UITheme.TEXT_CYAN);
        tableHeader.add(lblTblTitle, BorderLayout.WEST);

        JButton btnCancelRes = UIHelper.createDangerButton("✖ Cancel Selected Reservation");
        btnCancelRes.setFont(UITheme.FONT_SMALL);
        btnCancelRes.addActionListener(e -> onCancelSelectedReservation());
        tableHeader.add(btnCancelRes, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        String[] resCols = {"ID", "CODE", "USER", "VEHICLE", "NODE", "STATION", "START TIME", "END TIME", "EST. kWh", "STATUS"};
        reservationTableModel = new DefaultTableModel(resCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        UIHelper.styleTable(reservationTable);
        tableCard.add(UIHelper.createScrollPane(reservationTable), BorderLayout.CENTER);

        rightPane.add(tableCard, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPane);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadFormData() {
        try {
            // Load Vehicles
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            cmbVehicles.removeAllItems();
            for (Vehicle v : vehicles) {
                cmbVehicles.addItem(v);
            }

            // Load Stations
            List<ChargingStation> stations = stationDAO.getAllStations();
            cmbStations.removeAllItems();
            for (ChargingStation s : stations) {
                cmbStations.addItem(s);
            }
            onStationChanged();
        } catch (Exception e) {
            System.err.println("Notice loading reservation form data: " + e.getMessage());
        }
    }

    private void onStationChanged() {
        ChargingStation selectedStn = (ChargingStation) cmbStations.getSelectedItem();
        if (selectedStn == null) return;
        try {
            cachedPoints = pointDAO.getPointsByStation(selectedStn.getStationId());
            cmbPoints.removeAllItems();
            for (ChargingPoint cp : cachedPoints) {
                cmbPoints.addItem(cp);
            }
            onPointChanged();
        } catch (Exception e) {
            System.err.println("Notice updating points: " + e.getMessage());
        }
    }

    private void onPointChanged() {
        ChargingPoint selectedPoint = (ChargingPoint) cmbPoints.getSelectedItem();
        if (selectedPoint == null) return;
        try {
            Date targetDate = getSelectedDate();
            List<Reservation> resList = reservationService.getReservationsForPoint(selectedPoint.getPointId(), targetDate);
            timeSlotGrid.setReservations(resList, selectedPoint.getStatus());
            checkConflictPreview();
        } catch (Exception e) {
            System.err.println("Notice updating timeline: " + e.getMessage());
        }
    }

    private Date getSelectedDate() {
        Calendar cal = Calendar.getInstance();
        int offset = cmbDate.getSelectedIndex();
        cal.add(Calendar.DAY_OF_YEAR, offset);
        return cal.getTime();
    }

    private void checkConflictPreview() {
        int startH = cmbStartTime.getSelectedIndex();
        int endH = cmbEndTime.getSelectedIndex();
        timeSlotGrid.setSelectedHours(startH, endH);

        if (endH <= startH) {
            lblConflictStatus.setText("⚠ Invalid window: End time must be after start time");
            lblConflictStatus.setForeground(UITheme.ACCENT_ROSE);
            btnBook.setEnabled(false);
            return;
        }

        ChargingPoint cp = (ChargingPoint) cmbPoints.getSelectedItem();
        if (cp != null && "MAINTENANCE".equalsIgnoreCase(cp.getStatus())) {
            lblConflictStatus.setText("⚠ Node " + cp.getPointCode() + " is under MAINTENANCE");
            lblConflictStatus.setForeground(UITheme.ACCENT_ROSE);
            btnBook.setEnabled(false);
            return;
        }

        lblConflictStatus.setText("● Slot Available - Ready to Lock");
        lblConflictStatus.setForeground(UITheme.ACCENT_EMERALD);
        btnBook.setEnabled(true);
    }

    private void onConfirmBooking() {
        Vehicle v = (Vehicle) cmbVehicles.getSelectedItem();
        ChargingStation s = (ChargingStation) cmbStations.getSelectedItem();
        ChargingPoint cp = (ChargingPoint) cmbPoints.getSelectedItem();

        if (v == null || s == null || cp == null) {
            UIHelper.showError(this, "Please select valid vehicle, station, and charging node.");
            return;
        }

        int startH = cmbStartTime.getSelectedIndex();
        int endH = cmbEndTime.getSelectedIndex();
        if (endH <= startH) {
            UIHelper.showError(this, "End time must be strictly after start time.");
            return;
        }

        double estKwh;
        try {
            estKwh = Double.parseDouble(txtEstKwh.getText().trim());
        } catch (Exception e) {
            UIHelper.showError(this, "Please enter a valid numeric value for estimated kWh.");
            return;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(getSelectedDate());
        calStart.set(Calendar.HOUR_OF_DAY, startH);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(getSelectedDate());
        calEnd.set(Calendar.HOUR_OF_DAY, endH);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);

        Timestamp startTs = new Timestamp(calStart.getTimeInMillis());
        Timestamp endTs = new Timestamp(calEnd.getTimeInMillis());

        try {
            int resId = reservationService.createReservation(
                v.getUserId(), v.getVehicleId(), cp.getPointId(), s.getStationId(),
                startTs, endTs, estKwh, v.getOwnerName(), v.getOwnerCode(), v.getVehicleNumber(), cp.getPointCode()
            );

            UIHelper.showSuccess(this, "Reservation locked successfully! Confirmation ID: #" + resId);
            mainFrame.refreshAllViews();
        } catch (Exception ex) {
            UIHelper.showError(this, "Booking Failed: " + ex.getMessage());
        }
    }

    private void onCancelSelectedReservation() {
        int row = reservationTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a reservation row from the table to cancel.");
            return;
        }

        int resId = (int) reservationTableModel.getValueAt(row, 0);
        String code = (String) reservationTableModel.getValueAt(row, 1);
        String status = (String) reservationTableModel.getValueAt(row, 9);

        if (!"CONFIRMED".equalsIgnoreCase(status)) {
            UIHelper.showError(this, "Only CONFIRMED bookings can be cancelled.");
            return;
        }

        boolean confirm = UIHelper.showConfirm(this, "Cancel reservation #" + code + "?", "Cancel Booking");
        if (confirm) {
            try {
                reservationService.cancelReservation(resId, "", "", "");
                UIHelper.showSuccess(this, "Reservation #" + code + " has been cancelled.");
                mainFrame.refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Failed to cancel: " + ex.getMessage());
            }
        }
    }

    public void refreshReservations() {
        try {
            List<Reservation> list = reservationService.getAllReservations();
            String filter = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";

            reservationTableModel.setRowCount(0);
            for (Reservation r : list) {
                if (!filter.isEmpty()) {
                    boolean match = (r.getReservationCode() != null && r.getReservationCode().toLowerCase().contains(filter)) ||
                                    (r.getUserName() != null && r.getUserName().toLowerCase().contains(filter)) ||
                                    (r.getVehicleNumber() != null && r.getVehicleNumber().toLowerCase().contains(filter)) ||
                                    (r.getPointCode() != null && r.getPointCode().toLowerCase().contains(filter));
                    if (!match) continue;
                }

                reservationTableModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getReservationCode(),
                    r.getUserName(),
                    r.getVehicleNumber(),
                    r.getPointCode(),
                    r.getStationName(),
                    DateTimeUtil.formatDateTime(r.getStartTime()),
                    DateTimeUtil.formatDateTime(r.getEndTime()),
                    r.getEstimatedKwh() + " kWh",
                    r.getStatus()
                });
            }
            onPointChanged();
        } catch (Exception e) {
            System.err.println("Notice refreshing reservations: " + e.getMessage());
        }
    }
}
