package com.campus.ev.ui.management;

import com.campus.ev.dao.ChargingStationDAO;
import com.campus.ev.model.ChargingStation;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import com.campus.ev.validation.InputValidator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StationManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ChargingStationDAO stationDAO = new ChargingStationDAO();

    // Form inputs
    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtZone;
    private JTextField txtDesc;
    private JTextField txtPointsCount;
    private JTextField txtMaxGridKw;
    private JComboBox<String> cmbStatus;
    private JCheckBox chkSolar;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Table
    private DefaultTableModel tableModel;
    private JTable table;
    private int selectedStationId = 0;

    public StationManagementPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        refreshStations();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("CAMPUS CHARGING STATIONS & HUBS");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Configure university geographic zones, power grid capacity, and solar integration");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        topHeader.add(titleBlock, BorderLayout.WEST);
        add(topHeader, BorderLayout.NORTH);

        // MAIN SPLIT
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.35);

        // FORM CARD
        JPanel formCard = UIHelper.createCardPanel(new BorderLayout(10, 10));
        JLabel lblFormTitle = new JLabel("STATION LOCATION & GRID PROFILE");
        lblFormTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormTitle.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(8, 2, 6, 8));
        fields.setOpaque(false);

        fields.add(UIHelper.createFormLabel("Station Code:"));
        txtCode = UIHelper.createTextField(10);
        fields.add(txtCode);

        fields.add(UIHelper.createFormLabel("Station Name:"));
        txtName = UIHelper.createTextField(10);
        fields.add(txtName);

        fields.add(UIHelper.createFormLabel("Campus Zone:"));
        txtZone = UIHelper.createTextField(10);
        fields.add(txtZone);

        fields.add(UIHelper.createFormLabel("Description:"));
        txtDesc = UIHelper.createTextField(10);
        fields.add(txtDesc);

        fields.add(UIHelper.createFormLabel("Total Points:"));
        txtPointsCount = UIHelper.createTextField(10);
        fields.add(txtPointsCount);

        fields.add(UIHelper.createFormLabel("Grid Capacity (kW):"));
        txtMaxGridKw = UIHelper.createTextField(10);
        fields.add(txtMaxGridKw);

        fields.add(UIHelper.createFormLabel("Operating Status:"));
        cmbStatus = new JComboBox<>(new String[]{"OPERATIONAL", "DEGRADED", "OFFLINE", "MAINTENANCE"});
        cmbStatus.setFont(UITheme.FONT_REGULAR);
        cmbStatus.setBackground(UITheme.BG_INPUT);
        cmbStatus.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStatus);

        fields.add(UIHelper.createFormLabel("Solar Powered?"));
        chkSolar = new JCheckBox("Integrated Solar Canopy");
        chkSolar.setOpaque(false);
        chkSolar.setForeground(UITheme.ACCENT_PURPLE);
        fields.add(chkSolar);

        formCard.add(fields, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 6, 0));
        actionRow.setOpaque(false);

        btnAdd = UIHelper.createSuccessButton("Add");
        btnAdd.addActionListener(e -> onAddStation());

        btnUpdate = UIHelper.createPrimaryButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdateStation());

        btnDelete = UIHelper.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteStation());

        btnClear = UIHelper.createSecondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        actionRow.add(btnAdd);
        actionRow.add(btnUpdate);
        actionRow.add(btnDelete);
        actionRow.add(btnClear);
        formCard.add(actionRow, BorderLayout.SOUTH);

        splitPane.setLeftComponent(formCard);

        // TABLE CARD
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout(8, 8));
        String[] cols = {"ID", "CODE", "NAME", "ZONE", "POINTS", "GRID CAP (kW)", "SOLAR", "STATUS"};
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

    private void onAddStation() {
        try {
            InputValidator.validateNotEmpty(txtCode.getText(), "Station Code");
            InputValidator.validateNotEmpty(txtName.getText(), "Station Name");
            InputValidator.validateNotEmpty(txtZone.getText(), "Campus Zone");
            int pts = InputValidator.parsePositiveInt(txtPointsCount.getText(), "Total Points");
            double kw = InputValidator.parsePositiveDouble(txtMaxGridKw.getText(), "Grid Capacity");

            String code = txtCode.getText().trim().toUpperCase();
            if (stationDAO.isStationCodeExists(code, 0)) {
                UIHelper.showError(this, "Station code " + code + " already exists.");
                return;
            }

            ChargingStation s = new ChargingStation();
            s.setStationCode(code);
            s.setStationName(txtName.getText().trim());
            s.setCampusZone(txtZone.getText().trim());
            s.setLocationDescription(txtDesc.getText().trim());
            s.setTotalPoints(pts);
            s.setMaxGridCapacityKw(kw);
            s.setOperatingStatus((String) cmbStatus.getSelectedItem());
            s.setSolarPowered(chkSolar.isSelected());

            int id = stationDAO.insertStation(s);
            UIHelper.showSuccess(this, "Station created successfully! ID: #" + id);
            clearForm();
            refreshStations();
            mainFrame.refreshAllViews();
        } catch (Exception ex) {
            UIHelper.showError(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void onUpdateStation() {
        if (selectedStationId <= 0) return;
        try {
            InputValidator.validateNotEmpty(txtCode.getText(), "Station Code");
            InputValidator.validateNotEmpty(txtName.getText(), "Station Name");
            InputValidator.validateNotEmpty(txtZone.getText(), "Campus Zone");
            int pts = InputValidator.parsePositiveInt(txtPointsCount.getText(), "Total Points");
            double kw = InputValidator.parsePositiveDouble(txtMaxGridKw.getText(), "Grid Capacity");

            String code = txtCode.getText().trim().toUpperCase();
            if (stationDAO.isStationCodeExists(code, selectedStationId)) {
                UIHelper.showError(this, "Station code " + code + " already exists on another station.");
                return;
            }

            ChargingStation s = new ChargingStation();
            s.setStationId(selectedStationId);
            s.setStationCode(code);
            s.setStationName(txtName.getText().trim());
            s.setCampusZone(txtZone.getText().trim());
            s.setLocationDescription(txtDesc.getText().trim());
            s.setTotalPoints(pts);
            s.setMaxGridCapacityKw(kw);
            s.setOperatingStatus((String) cmbStatus.getSelectedItem());
            s.setSolarPowered(chkSolar.isSelected());

            stationDAO.updateStation(s);
            UIHelper.showSuccess(this, "Station updated successfully!");
            clearForm();
            refreshStations();
            mainFrame.refreshAllViews();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update Error: " + ex.getMessage());
        }
    }

    private void onDeleteStation() {
        if (selectedStationId <= 0) return;
        boolean confirm = UIHelper.showConfirm(this, "Delete station #" + selectedStationId + "?", "Confirm Delete");
        if (confirm) {
            try {
                stationDAO.deleteStation(selectedStationId);
                UIHelper.showSuccess(this, "Station deleted.");
                clearForm();
                refreshStations();
                mainFrame.refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Cannot delete station with linked charging points: " + ex.getMessage());
            }
        }
    }

    private void onTableRowSelected(int row) {
        selectedStationId = (int) tableModel.getValueAt(row, 0);
        try {
            ChargingStation s = stationDAO.getStationById(selectedStationId);
            if (s != null) {
                txtCode.setText(s.getStationCode());
                txtName.setText(s.getStationName());
                txtZone.setText(s.getCampusZone());
                txtDesc.setText(s.getLocationDescription());
                txtPointsCount.setText(String.valueOf(s.getTotalPoints()));
                txtMaxGridKw.setText(String.valueOf(s.getMaxGridCapacityKw()));
                cmbStatus.setSelectedItem(s.getOperatingStatus());
                chkSolar.setSelected(s.isSolarPowered());

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }

    private void clearForm() {
        selectedStationId = 0;
        txtCode.setText("");
        txtName.setText("");
        txtZone.setText("");
        txtDesc.setText("");
        txtPointsCount.setText("");
        txtMaxGridKw.setText("");
        chkSolar.setSelected(false);
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    public void refreshStations() {
        try {
            List<ChargingStation> list = stationDAO.getAllStations();
            tableModel.setRowCount(0);
            for (ChargingStation s : list) {
                tableModel.addRow(new Object[]{
                    s.getStationId(),
                    s.getStationCode(),
                    s.getStationName(),
                    s.getCampusZone(),
                    s.getTotalPoints(),
                    s.getMaxGridCapacityKw() + " kW",
                    s.isSolarPowered() ? "YES (Solar)" : "NO",
                    s.getOperatingStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing stations: " + e.getMessage());
        }
    }
}
