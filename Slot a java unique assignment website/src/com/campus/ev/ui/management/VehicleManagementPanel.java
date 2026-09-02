package com.campus.ev.ui.management;

import com.campus.ev.dao.UserDAO;
import com.campus.ev.dao.VehicleDAO;
import com.campus.ev.model.User;
import com.campus.ev.model.Vehicle;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import com.campus.ev.validation.InputValidator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehicleManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final UserDAO userDAO = new UserDAO();

    // 4-Quadrant Form Inputs
    private JTextField txtVehicleNum;
    private JComboBox<String> cmbVehicleType;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtBatteryKwh;
    private JTextField txtMaxChargeKw;
    private JComboBox<User> cmbOwner;
    private JComboBox<String> cmbConnectorType;
    private JComboBox<String> cmbStatus;

    private JButton btnRegister;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Search & Table
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterType;
    private DefaultTableModel tableModel;
    private JTable table;

    private int selectedVehicleId = 0;

    public VehicleManagementPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        loadOwners();
        refreshVehicles();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("CAMPUS EV FLEET & VEHICLE REGISTRY");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Register and configure electric vehicles, battery profiles, and charging standards");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Type:"));
        cmbFilterType = new JComboBox<>(new String[]{"ALL", "2-WHEELER_SCOOTER", "4-WHEELER_SEDAN", "4-WHEELER_SUV", "CAMPUS_BUS_SHUTTLE", "FACILITY_UTILITY_VAN"});
        cmbFilterType.setFont(UITheme.FONT_SMALL);
        cmbFilterType.setBackground(UITheme.BG_INPUT);
        cmbFilterType.setForeground(UITheme.TEXT_PRIMARY);
        cmbFilterType.addActionListener(e -> refreshVehicles());
        searchPanel.add(cmbFilterType);

        txtSearch = UIHelper.createTextField(14);
        JButton btnSearch = UIHelper.createSecondaryButton("🔍 Search");
        btnSearch.addActionListener(e -> refreshVehicles());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(searchPanel, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // MAIN SPLIT: Left 4-Quadrant Form (420px), Right Table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.38);

        // LEFT: 4-Quadrant Form Card
        JPanel formCard = UIHelper.createCardPanel(new BorderLayout(10, 10));
        JLabel lblFormTitle = new JLabel("VEHICLE PROFILE & TECHNICAL SPECIFICATION");
        lblFormTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormTitle.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel quadGrid = new JPanel(new GridLayout(4, 1, 0, 10));
        quadGrid.setOpaque(false);

        // Quadrant 1: Identity
        JPanel q1 = createQuadrant("1. VEHICLE IDENTITY", 2, 2);
        q1.add(UIHelper.createFormLabel("Vehicle Number:"));
        txtVehicleNum = UIHelper.createTextField(10);
        q1.add(txtVehicleNum);
        q1.add(UIHelper.createFormLabel("Category:"));
        cmbVehicleType = new JComboBox<>(new String[]{"2-WHEELER_SCOOTER", "4-WHEELER_SEDAN", "4-WHEELER_SUV", "CAMPUS_BUS_SHUTTLE", "FACILITY_UTILITY_VAN"});
        cmbVehicleType.setFont(UITheme.FONT_REGULAR);
        cmbVehicleType.setBackground(UITheme.BG_INPUT);
        cmbVehicleType.setForeground(UITheme.TEXT_PRIMARY);
        q1.add(cmbVehicleType);
        quadGrid.add(q1);

        // Quadrant 2: Brand & Model
        JPanel q2 = createQuadrant("2. MAKE & MODEL", 2, 2);
        q2.add(UIHelper.createFormLabel("Manufacturer Brand:"));
        txtBrand = UIHelper.createTextField(10);
        q2.add(txtBrand);
        q2.add(UIHelper.createFormLabel("Model Name:"));
        txtModel = UIHelper.createTextField(10);
        q2.add(txtModel);
        quadGrid.add(q2);

        // Quadrant 3: Technical Battery Specs
        JPanel q3 = createQuadrant("3. BATTERY & CHARGING RATING", 2, 2);
        q3.add(UIHelper.createFormLabel("Battery Capacity (kWh):"));
        txtBatteryKwh = UIHelper.createTextField(10);
        q3.add(txtBatteryKwh);
        q3.add(UIHelper.createFormLabel("Max Charge Rate (kW):"));
        txtMaxChargeKw = UIHelper.createTextField(10);
        q3.add(txtMaxChargeKw);
        quadGrid.add(q3);

        // Quadrant 4: Owner & Compatibility
        JPanel q4 = createQuadrant("4. OWNER & CONNECTOR COMPATIBILITY", 3, 2);
        q4.add(UIHelper.createFormLabel("Linked User/Driver:"));
        cmbOwner = new JComboBox<>();
        cmbOwner.setFont(UITheme.FONT_REGULAR);
        cmbOwner.setBackground(UITheme.BG_INPUT);
        cmbOwner.setForeground(UITheme.TEXT_PRIMARY);
        q4.add(cmbOwner);

        q4.add(UIHelper.createFormLabel("Connector Standard:"));
        cmbConnectorType = new JComboBox<>(new String[]{"TYPE_2_AC", "CCS_2_DC", "CHADEMO", "GB_T_DC", "BHARAT_AC_001"});
        cmbConnectorType.setFont(UITheme.FONT_REGULAR);
        cmbConnectorType.setBackground(UITheme.BG_INPUT);
        cmbConnectorType.setForeground(UITheme.TEXT_PRIMARY);
        q4.add(cmbConnectorType);

        q4.add(UIHelper.createFormLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cmbStatus.setFont(UITheme.FONT_REGULAR);
        cmbStatus.setBackground(UITheme.BG_INPUT);
        cmbStatus.setForeground(UITheme.TEXT_PRIMARY);
        q4.add(cmbStatus);
        quadGrid.add(q4);

        formCard.add(quadGrid, BorderLayout.CENTER);

        // Form Actions
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 6, 0));
        actionRow.setOpaque(false);

        btnRegister = UIHelper.createSuccessButton("Register");
        btnRegister.addActionListener(e -> onRegisterVehicle());

        btnUpdate = UIHelper.createPrimaryButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdateVehicle());

        btnDelete = UIHelper.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteVehicle());

        btnClear = UIHelper.createSecondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        actionRow.add(btnRegister);
        actionRow.add(btnUpdate);
        actionRow.add(btnDelete);
        actionRow.add(btnClear);
        formCard.add(actionRow, BorderLayout.SOUTH);

        splitPane.setLeftComponent(formCard);

        // RIGHT: Vehicles Table
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout(8, 8));
        String[] cols = {"ID", "NUMBER", "BRAND & MODEL", "TYPE", "BATTERY (kWh)", "MAX RATE (kW)", "CONNECTOR", "OWNER", "STATUS"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                onTableRowSelected(table.getSelectedRow());
            }
        });

        tableCard.add(UIHelper.createScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(tableCard);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createQuadrant(String title, int rows, int cols) {
        JPanel p = UIHelper.createDarkPanel(new GridLayout(rows, cols, 6, 6));
        p.setBorder(BorderFactory.createTitledBorder(
            new EmptyBorder(0, 0, 0, 0), title, 0, 0, UITheme.FONT_SMALL, UITheme.TEXT_CYAN
        ));
        return p;
    }

    private void loadOwners() {
        try {
            List<User> users = userDAO.getAllUsers();
            cmbOwner.removeAllItems();
            for (User u : users) {
                cmbOwner.addItem(u);
            }
        } catch (Exception ignored) {}
    }

    private void onRegisterVehicle() {
        try {
            String vNum = txtVehicleNum.getText();
            InputValidator.validateVehicleNumber(vNum);
            InputValidator.validateNotEmpty(txtBrand.getText(), "Brand");
            InputValidator.validateNotEmpty(txtModel.getText(), "Model");
            double battery = InputValidator.parsePositiveDouble(txtBatteryKwh.getText(), "Battery capacity");
            double maxCharge = InputValidator.parsePositiveDouble(txtMaxChargeKw.getText(), "Max charge rate");

            if (vehicleDAO.isVehicleNumberExists(vNum, 0)) {
                UIHelper.showError(this, "A vehicle with number " + vNum + " is already registered.");
                return;
            }

            User owner = (User) cmbOwner.getSelectedItem();
            if (owner == null) {
                UIHelper.showError(this, "Please select an owner for this vehicle.");
                return;
            }

            Vehicle v = new Vehicle();
            v.setVehicleNumber(vNum.trim().toUpperCase());
            v.setUserId(owner.getUserId());
            v.setVehicleType((String) cmbVehicleType.getSelectedItem());
            v.setBrand(txtBrand.getText().trim());
            v.setModel(txtModel.getText().trim());
            v.setBatteryCapacityKwh(battery);
            v.setMaxChargeRateKw(maxCharge);
            v.setConnectorType((String) cmbConnectorType.getSelectedItem());
            v.setStatus((String) cmbStatus.getSelectedItem());

            int id = vehicleDAO.insertVehicle(v);
            UIHelper.showSuccess(this, "Vehicle registered successfully! ID: #" + id);
            clearForm();
            refreshVehicles();
        } catch (Exception ex) {
            UIHelper.showError(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void onUpdateVehicle() {
        if (selectedVehicleId <= 0) return;
        try {
            String vNum = txtVehicleNum.getText();
            InputValidator.validateVehicleNumber(vNum);
            InputValidator.validateNotEmpty(txtBrand.getText(), "Brand");
            InputValidator.validateNotEmpty(txtModel.getText(), "Model");
            double battery = InputValidator.parsePositiveDouble(txtBatteryKwh.getText(), "Battery capacity");
            double maxCharge = InputValidator.parsePositiveDouble(txtMaxChargeKw.getText(), "Max charge rate");

            if (vehicleDAO.isVehicleNumberExists(vNum, selectedVehicleId)) {
                UIHelper.showError(this, "Another vehicle with number " + vNum + " already exists.");
                return;
            }

            User owner = (User) cmbOwner.getSelectedItem();
            Vehicle v = new Vehicle();
            v.setVehicleId(selectedVehicleId);
            v.setVehicleNumber(vNum.trim().toUpperCase());
            v.setUserId(owner.getUserId());
            v.setVehicleType((String) cmbVehicleType.getSelectedItem());
            v.setBrand(txtBrand.getText().trim());
            v.setModel(txtModel.getText().trim());
            v.setBatteryCapacityKwh(battery);
            v.setMaxChargeRateKw(maxCharge);
            v.setConnectorType((String) cmbConnectorType.getSelectedItem());
            v.setStatus((String) cmbStatus.getSelectedItem());

            vehicleDAO.updateVehicle(v);
            UIHelper.showSuccess(this, "Vehicle updated successfully!");
            clearForm();
            refreshVehicles();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update Error: " + ex.getMessage());
        }
    }

    private void onDeleteVehicle() {
        if (selectedVehicleId <= 0) return;
        boolean confirm = UIHelper.showConfirm(this, "Delete vehicle record #" + selectedVehicleId + "?", "Confirm Delete");
        if (confirm) {
            try {
                vehicleDAO.deleteVehicle(selectedVehicleId);
                UIHelper.showSuccess(this, "Vehicle deleted.");
                clearForm();
                refreshVehicles();
            } catch (Exception ex) {
                UIHelper.showError(this, "Delete Error (Check if active sessions exist): " + ex.getMessage());
            }
        }
    }

    private void onTableRowSelected(int row) {
        selectedVehicleId = (int) tableModel.getValueAt(row, 0);
        try {
            Vehicle v = vehicleDAO.getVehicleById(selectedVehicleId);
            if (v != null) {
                txtVehicleNum.setText(v.getVehicleNumber());
                cmbVehicleType.setSelectedItem(v.getVehicleType());
                txtBrand.setText(v.getBrand());
                txtModel.setText(v.getModel());
                txtBatteryKwh.setText(String.valueOf(v.getBatteryCapacityKwh()));
                txtMaxChargeKw.setText(String.valueOf(v.getMaxChargeRateKw()));
                cmbConnectorType.setSelectedItem(v.getConnectorType());
                cmbStatus.setSelectedItem(v.getStatus());

                for (int i = 0; i < cmbOwner.getItemCount(); i++) {
                    if (cmbOwner.getItemAt(i).getUserId() == v.getUserId()) {
                        cmbOwner.setSelectedIndex(i);
                        break;
                    }
                }

                btnRegister.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }

    private void clearForm() {
        selectedVehicleId = 0;
        txtVehicleNum.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtBatteryKwh.setText("");
        txtMaxChargeKw.setText("");
        btnRegister.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    public void refreshVehicles() {
        try {
            String query = txtSearch != null ? txtSearch.getText().trim() : "";
            String typeFilter = cmbFilterType != null ? (String) cmbFilterType.getSelectedItem() : "ALL";

            List<Vehicle> list = vehicleDAO.searchVehicles(query, typeFilter, "ALL");
            tableModel.setRowCount(0);
            for (Vehicle v : list) {
                tableModel.addRow(new Object[]{
                    v.getVehicleId(),
                    v.getVehicleNumber(),
                    v.getBrand() + " " + v.getModel(),
                    v.getVehicleType(),
                    v.getBatteryCapacityKwh() + " kWh",
                    v.getMaxChargeRateKw() + " kW",
                    v.getConnectorType(),
                    v.getOwnerName(),
                    v.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing vehicles: " + e.getMessage());
        }
    }
}
