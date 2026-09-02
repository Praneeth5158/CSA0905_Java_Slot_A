package com.campus.ev.ui.management;

import com.campus.ev.dao.TariffDAO;
import com.campus.ev.model.Tariff;
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

public class TariffManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final TariffDAO tariffDAO = new TariffDAO();

    // Form inputs
    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtRateKwh;
    private JTextField txtParkingFee;
    private JTextField txtMultiplier;
    private JTextField txtEffective;
    private JComboBox<String> cmbStatus;
    private JTextField txtDesc;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Table
    private DefaultTableModel tableModel;
    private JTable table;
    private int selectedTariffId = 0;

    public TariffManagementPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        refreshTariffs();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("DYNAMIC CAMPUS TARIFF & BILLING ENGINE");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Configure dynamic per-kWh energy tariffs, solar green subsidies, and overstay parking fees");
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
        JLabel lblFormTitle = new JLabel("TARIFF RATE CONFIGURATION");
        lblFormTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormTitle.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(8, 2, 6, 8));
        fields.setOpaque(false);

        fields.add(UIHelper.createFormLabel("Tariff Code:"));
        txtCode = UIHelper.createTextField(10);
        fields.add(txtCode);

        fields.add(UIHelper.createFormLabel("Tariff Scheme Name:"));
        txtName = UIHelper.createTextField(10);
        fields.add(txtName);

        fields.add(UIHelper.createFormLabel("Base Rate (₹/kWh):"));
        txtRateKwh = UIHelper.createTextField(10);
        fields.add(txtRateKwh);

        fields.add(UIHelper.createFormLabel("Overstay Fee (₹/hr):"));
        txtParkingFee = UIHelper.createTextField(10);
        fields.add(txtParkingFee);

        fields.add(UIHelper.createFormLabel("Peak Multiplier (e.g. 1.25):"));
        txtMultiplier = UIHelper.createTextField(10);
        txtMultiplier.setText("1.00");
        fields.add(txtMultiplier);

        fields.add(UIHelper.createFormLabel("Effective Date:"));
        txtEffective = UIHelper.createTextField(10);
        txtEffective.setText("2026-01-01");
        fields.add(txtEffective);

        fields.add(UIHelper.createFormLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "EXPIRED", "PENDING"});
        cmbStatus.setFont(UITheme.FONT_REGULAR);
        cmbStatus.setBackground(UITheme.BG_INPUT);
        cmbStatus.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStatus);

        fields.add(UIHelper.createFormLabel("Description:"));
        txtDesc = UIHelper.createTextField(10);
        fields.add(txtDesc);

        formCard.add(fields, BorderLayout.CENTER);

        // Actions
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 6, 0));
        actionRow.setOpaque(false);

        btnAdd = UIHelper.createSuccessButton("Add");
        btnAdd.addActionListener(e -> onAddTariff());

        btnUpdate = UIHelper.createPrimaryButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdateTariff());

        btnDelete = UIHelper.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteTariff());

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
        String[] cols = {"ID", "CODE", "NAME", "RATE (₹/kWh)", "OVERSTAY (₹/hr)", "MULTIPLIER", "EFFECTIVE", "STATUS"};
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

    private void onAddTariff() {
        try {
            InputValidator.validateNotEmpty(txtCode.getText(), "Tariff Code");
            InputValidator.validateNotEmpty(txtName.getText(), "Scheme Name");
            double rate = InputValidator.parsePositiveDouble(txtRateKwh.getText(), "Rate per kWh");
            double parking = Double.parseDouble(txtParkingFee.getText().trim());
            double mult = InputValidator.parsePositiveDouble(txtMultiplier.getText(), "Peak Multiplier");

            Tariff t = new Tariff();
            t.setTariffCode(txtCode.getText().trim().toUpperCase());
            t.setTariffName(txtName.getText().trim());
            t.setRatePerKwh(rate);
            t.setBaseParkingFeePerHour(parking);
            t.setPeakHourMultiplier(mult);
            t.setEffectiveFrom(Date.valueOf(txtEffective.getText().trim()));
            t.setStatus((String) cmbStatus.getSelectedItem());
            t.setDescription(txtDesc.getText().trim());

            int id = tariffDAO.insertTariff(t);
            UIHelper.showSuccess(this, "Tariff scheme created! ID: #" + id);
            clearForm();
            refreshTariffs();
        } catch (Exception ex) {
            UIHelper.showError(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void onUpdateTariff() {
        if (selectedTariffId <= 0) return;
        try {
            InputValidator.validateNotEmpty(txtCode.getText(), "Tariff Code");
            InputValidator.validateNotEmpty(txtName.getText(), "Scheme Name");
            double rate = InputValidator.parsePositiveDouble(txtRateKwh.getText(), "Rate per kWh");
            double parking = Double.parseDouble(txtParkingFee.getText().trim());
            double mult = InputValidator.parsePositiveDouble(txtMultiplier.getText(), "Peak Multiplier");

            Tariff t = new Tariff();
            t.setTariffId(selectedTariffId);
            t.setTariffCode(txtCode.getText().trim().toUpperCase());
            t.setTariffName(txtName.getText().trim());
            t.setRatePerKwh(rate);
            t.setBaseParkingFeePerHour(parking);
            t.setPeakHourMultiplier(mult);
            t.setEffectiveFrom(Date.valueOf(txtEffective.getText().trim()));
            t.setStatus((String) cmbStatus.getSelectedItem());
            t.setDescription(txtDesc.getText().trim());

            tariffDAO.updateTariff(t);
            UIHelper.showSuccess(this, "Tariff updated successfully!");
            clearForm();
            refreshTariffs();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update Error: " + ex.getMessage());
        }
    }

    private void onDeleteTariff() {
        if (selectedTariffId <= 0) return;
        boolean confirm = UIHelper.showConfirm(this, "Delete tariff #" + selectedTariffId + "?", "Confirm Delete");
        if (confirm) {
            try {
                tariffDAO.deleteTariff(selectedTariffId);
                UIHelper.showSuccess(this, "Tariff deleted.");
                clearForm();
                refreshTariffs();
            } catch (Exception ex) {
                UIHelper.showError(this, "Cannot delete tariff in use by charging sessions: " + ex.getMessage());
            }
        }
    }

    private void onTableRowSelected(int row) {
        selectedTariffId = (int) tableModel.getValueAt(row, 0);
        try {
            Tariff t = tariffDAO.getTariffById(selectedTariffId);
            if (t != null) {
                txtCode.setText(t.getTariffCode());
                txtName.setText(t.getTariffName());
                txtRateKwh.setText(String.valueOf(t.getRatePerKwh()));
                txtParkingFee.setText(String.valueOf(t.getBaseParkingFeePerHour()));
                txtMultiplier.setText(String.valueOf(t.getPeakHourMultiplier()));
                txtEffective.setText(t.getEffectiveFrom() != null ? t.getEffectiveFrom().toString() : "2026-01-01");
                cmbStatus.setSelectedItem(t.getStatus());
                txtDesc.setText(t.getDescription() != null ? t.getDescription() : "");

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }

    private void clearForm() {
        selectedTariffId = 0;
        txtCode.setText("");
        txtName.setText("");
        txtRateKwh.setText("");
        txtParkingFee.setText("");
        txtMultiplier.setText("1.00");
        txtDesc.setText("");
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    public void refreshTariffs() {
        try {
            List<Tariff> list = tariffDAO.getAllTariffs();
            tableModel.setRowCount(0);
            for (Tariff t : list) {
                tableModel.addRow(new Object[]{
                    t.getTariffId(),
                    t.getTariffCode(),
                    t.getTariffName(),
                    "₹" + String.format("%.2f", t.getRatePerKwh()),
                    "₹" + String.format("%.2f", t.getBaseParkingFeePerHour()),
                    t.getPeakHourMultiplier() + "x",
                    t.getEffectiveFrom(),
                    t.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing tariffs: " + e.getMessage());
        }
    }
}
