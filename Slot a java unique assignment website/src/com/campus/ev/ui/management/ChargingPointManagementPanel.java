package com.campus.ev.ui.management;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.ChargingStationDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.ChargingStation;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import com.campus.ev.validation.InputValidator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class ChargingPointManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();
    private final ChargingStationDAO stationDAO = new ChargingStationDAO();

    // Form inputs
    private JTextField txtPointCode;
    private JComboBox<ChargingStation> cmbStation;
    private JTextField txtPointNumber;
    private JComboBox<String> cmbConnector;
    private JTextField txtPowerKw;
    private JComboBox<String> cmbStatus;
    private JCheckBox chkFastCharger;
    private JTextField txtHardwareModel;
    private JTextField txtLastService;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Table
    private DefaultTableModel tableModel;
    private JTable table;
    private int selectedPointId = 0;

    public ChargingPointManagementPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        loadStations();
        refreshPoints();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("CHARGING POINTS & DISPENSER NODES");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Configure charging dispensers, connector ratings, DC fast modes, and maintenance status");
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
        JLabel lblFormTitle = new JLabel("DISPENSER SPECIFICATION");
        lblFormTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormTitle.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(9, 2, 6, 8));
        fields.setOpaque(false);

        fields.add(UIHelper.createFormLabel("Point Code (e.g. CP-19):"));
        txtPointCode = UIHelper.createTextField(10);
        fields.add(txtPointCode);

        fields.add(UIHelper.createFormLabel("Parent Station:"));
        cmbStation = new JComboBox<>();
        cmbStation.setFont(UITheme.FONT_REGULAR);
        cmbStation.setBackground(UITheme.BG_INPUT);
        cmbStation.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStation);

        fields.add(UIHelper.createFormLabel("Point Slot Number:"));
        txtPointNumber = UIHelper.createTextField(10);
        fields.add(txtPointNumber);

        fields.add(UIHelper.createFormLabel("Connector Type:"));
        cmbConnector = new JComboBox<>(new String[]{"TYPE_2_AC", "CCS_2_DC", "CHADEMO", "GB_T_DC", "BHARAT_AC_001"});
        cmbConnector.setFont(UITheme.FONT_REGULAR);
        cmbConnector.setBackground(UITheme.BG_INPUT);
        cmbConnector.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbConnector);

        fields.add(UIHelper.createFormLabel("Power Rating (kW):"));
        txtPowerKw = UIHelper.createTextField(10);
        fields.add(txtPowerKw);

        fields.add(UIHelper.createFormLabel("Current Status:"));
        cmbStatus = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "RESERVED", "MAINTENANCE"});
        cmbStatus.setFont(UITheme.FONT_REGULAR);
        cmbStatus.setBackground(UITheme.BG_INPUT);
        cmbStatus.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStatus);

        fields.add(UIHelper.createFormLabel("Fast Charger:"));
        chkFastCharger = new JCheckBox("DC High Power Mode");
        chkFastCharger.setOpaque(false);
        chkFastCharger.setForeground(UITheme.ACCENT_CYAN);
        fields.add(chkFastCharger);

        fields.add(UIHelper.createFormLabel("Hardware Model:"));
        txtHardwareModel = UIHelper.createTextField(10);
        fields.add(txtHardwareModel);

        fields.add(UIHelper.createFormLabel("Last Service (YYYY-MM-DD):"));
        txtLastService = UIHelper.createTextField(10);
        txtLastService.setText("2026-08-25");
        fields.add(txtLastService);

        formCard.add(fields, BorderLayout.CENTER);

        // Actions
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 6, 0));
        actionRow.setOpaque(false);

        btnAdd = UIHelper.createSuccessButton("Add");
        btnAdd.addActionListener(e -> onAddPoint());

        btnUpdate = UIHelper.createPrimaryButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdatePoint());

        btnDelete = UIHelper.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeletePoint());

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
        String[] cols = {"ID", "CODE", "STATION", "ZONE", "SLOT #", "CONNECTOR", "POWER (kW)", "FAST DC", "STATUS"};
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

    private void loadStations() {
        try {
            List<ChargingStation> list = stationDAO.getAllStations();
            cmbStation.removeAllItems();
            for (ChargingStation s : list) {
                cmbStation.addItem(s);
            }
        } catch (Exception ignored) {}
    }

    private void onAddPoint() {
        try {
            InputValidator.validateNotEmpty(txtPointCode.getText(), "Point Code");
            int slotNum = InputValidator.parsePositiveInt(txtPointNumber.getText(), "Slot Number");
            double kw = InputValidator.parsePositiveDouble(txtPowerKw.getText(), "Power Rating");

            String code = txtPointCode.getText().trim().toUpperCase();
            if (pointDAO.isPointCodeExists(code, 0)) {
                UIHelper.showError(this, "Point code " + code + " already exists.");
                return;
            }

            ChargingStation stn = (ChargingStation) cmbStation.getSelectedItem();
            if (stn == null) {
                UIHelper.showError(this, "Please select a parent station.");
                return;
            }

            ChargingPoint cp = new ChargingPoint();
            cp.setPointCode(code);
            cp.setStationId(stn.getStationId());
            cp.setPointNumber(slotNum);
            cp.setConnectorType((String) cmbConnector.getSelectedItem());
            cp.setPowerRatingKw(kw);
            cp.setStatus((String) cmbStatus.getSelectedItem());
            cp.setFastCharger(chkFastCharger.isSelected());
            cp.setHardwareModel(txtHardwareModel.getText().trim());

            try {
                cp.setLastServiceDate(Date.valueOf(txtLastService.getText().trim()));
            } catch (Exception e) {
                cp.setLastServiceDate(new Date(System.currentTimeMillis()));
            }

            int id = pointDAO.insertPoint(cp);
            UIHelper.showSuccess(this, "Charging point dispenser added! ID: #" + id);
            clearForm();
            refreshPoints();
            mainFrame.refreshAllViews();
        } catch (Exception ex) {
            UIHelper.showError(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void onUpdatePoint() {
        if (selectedPointId <= 0) return;
        try {
            InputValidator.validateNotEmpty(txtPointCode.getText(), "Point Code");
            int slotNum = InputValidator.parsePositiveInt(txtPointNumber.getText(), "Slot Number");
            double kw = InputValidator.parsePositiveDouble(txtPowerKw.getText(), "Power Rating");

            String code = txtPointCode.getText().trim().toUpperCase();
            if (pointDAO.isPointCodeExists(code, selectedPointId)) {
                UIHelper.showError(this, "Point code " + code + " already belongs to another node.");
                return;
            }

            ChargingStation stn = (ChargingStation) cmbStation.getSelectedItem();
            ChargingPoint cp = new ChargingPoint();
            cp.setPointId(selectedPointId);
            cp.setPointCode(code);
            cp.setStationId(stn.getStationId());
            cp.setPointNumber(slotNum);
            cp.setConnectorType((String) cmbConnector.getSelectedItem());
            cp.setPowerRatingKw(kw);
            cp.setStatus((String) cmbStatus.getSelectedItem());
            cp.setFastCharger(chkFastCharger.isSelected());
            cp.setHardwareModel(txtHardwareModel.getText().trim());
            try {
                cp.setLastServiceDate(Date.valueOf(txtLastService.getText().trim()));
            } catch (Exception e) {
                cp.setLastServiceDate(new Date(System.currentTimeMillis()));
            }

            pointDAO.updatePoint(cp);
            UIHelper.showSuccess(this, "Charging point updated successfully!");
            clearForm();
            refreshPoints();
            mainFrame.refreshAllViews();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update Error: " + ex.getMessage());
        }
    }

    private void onDeletePoint() {
        if (selectedPointId <= 0) return;
        boolean confirm = UIHelper.showConfirm(this, "Delete charging point #" + selectedPointId + "?", "Confirm Delete");
        if (confirm) {
            try {
                pointDAO.deletePoint(selectedPointId);
                UIHelper.showSuccess(this, "Charging point removed.");
                clearForm();
                refreshPoints();
                mainFrame.refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Cannot delete point with existing sessions/reservations: " + ex.getMessage());
            }
        }
    }

    private void onTableRowSelected(int row) {
        selectedPointId = (int) tableModel.getValueAt(row, 0);
        try {
            ChargingPoint cp = pointDAO.getPointById(selectedPointId);
            if (cp != null) {
                txtPointCode.setText(cp.getPointCode());
                txtPointNumber.setText(String.valueOf(cp.getPointNumber()));
                txtPowerKw.setText(String.valueOf(cp.getPowerRatingKw()));
                cmbConnector.setSelectedItem(cp.getConnectorType());
                cmbStatus.setSelectedItem(cp.getStatus());
                chkFastCharger.setSelected(cp.isFastCharger());
                txtHardwareModel.setText(cp.getHardwareModel() != null ? cp.getHardwareModel() : "");
                txtLastService.setText(cp.getLastServiceDate() != null ? cp.getLastServiceDate().toString() : "2026-08-25");

                for (int i = 0; i < cmbStation.getItemCount(); i++) {
                    if (cmbStation.getItemAt(i).getStationId() == cp.getStationId()) {
                        cmbStation.setSelectedIndex(i);
                        break;
                    }
                }

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }

    private void clearForm() {
        selectedPointId = 0;
        txtPointCode.setText("");
        txtPointNumber.setText("");
        txtPowerKw.setText("");
        txtHardwareModel.setText("");
        chkFastCharger.setSelected(false);
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    public void refreshPoints() {
        try {
            List<ChargingPoint> list = pointDAO.getAllPointsWithDetails();
            tableModel.setRowCount(0);
            for (ChargingPoint cp : list) {
                tableModel.addRow(new Object[]{
                    cp.getPointId(),
                    cp.getPointCode(),
                    cp.getStationName(),
                    cp.getCampusZone(),
                    cp.getPointNumber(),
                    cp.getConnectorType(),
                    cp.getPowerRatingKw() + " kW",
                    cp.isFastCharger() ? "YES" : "NO",
                    cp.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing charging points: " + e.getMessage());
        }
    }
}
