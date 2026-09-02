package com.campus.ev.ui.reports;

import com.campus.ev.model.CampusEnergySummaryDTO;
import com.campus.ev.model.StationUtilizationDTO;
import com.campus.ev.service.AnalyticsService;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.CurrencyUtil;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ReportsAnalyticsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AnalyticsService analyticsService = new AnalyticsService();

    private JTabbedPane tabbedPane;

    // Tab 1: Station Utilization (Stored Procedure)
    private DefaultTableModel modelUtilization;
    private JTable tableUtilization;

    // Tab 2: Energy Consumption
    private DefaultTableModel modelEnergy;
    private JTable tableEnergy;

    // Tab 3: Revenue & Payments
    private DefaultTableModel modelRevenue;
    private JTable tableRevenue;

    // Tab 4: Vehicle Fleet & Carbon Offset
    private DefaultTableModel modelFleet;
    private JTable tableFleet;

    // Date Range Filters
    private JComboBox<String> cmbDatePreset;

    public ReportsAnalyticsPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        refreshAllReports();
    }

    private void initComponents() {
        // TOP HEADER & CONTROLS
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("ANALYTICS & REPORTING STUDIO");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Stored procedure utilization analysis, energy metrics, revenue settlements & carbon offset");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);

        toolbar.add(new JLabel("Range:"));
        cmbDatePreset = new JComboBox<>(new String[]{"Today", "Last 7 Days", "Last 30 Days", "All Time"});
        cmbDatePreset.setFont(UITheme.FONT_SMALL);
        cmbDatePreset.setBackground(UITheme.BG_INPUT);
        cmbDatePreset.setForeground(UITheme.TEXT_PRIMARY);
        cmbDatePreset.setSelectedIndex(2); // Last 30 Days
        cmbDatePreset.addActionListener(e -> refreshAllReports());
        toolbar.add(cmbDatePreset);

        JButton btnRefresh = UIHelper.createSecondaryButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> refreshAllReports());
        toolbar.add(btnRefresh);

        JButton btnExport = UIHelper.createPrimaryButton("📥 Export CSV");
        btnExport.addActionListener(e -> onExportCsv());
        toolbar.add(btnExport);

        JButton btnCopy = UIHelper.createSuccessButton("📋 Copy Summary");
        btnCopy.addActionListener(e -> onCopySummary());
        toolbar.add(btnCopy);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(toolbar, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // TABBED PANE
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UITheme.BG_DARKEST);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        tabbedPane.setFont(UITheme.FONT_REGULAR_BOLD);

        // Tab 1: Station Utilization
        tabbedPane.addTab("1. Station Utilization (Stored Proc)", createUtilizationTab());

        // Tab 2: Energy Consumption
        tabbedPane.addTab("2. Energy Consumption Timeline", createEnergyTab());

        // Tab 3: Revenue & Payments
        tabbedPane.addTab("3. Revenue & Settlement", createRevenueTab());

        // Tab 4: Vehicle Fleet & Carbon Offset
        tabbedPane.addTab("4. Fleet Green & Carbon Offset (SDG)", createFleetTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUtilizationTab() {
        JPanel p = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lbl = new JLabel("STATION UTILIZATION REPORT (Executed via MySQL Stored Procedure `sp_get_station_utilization`)");
        lbl.setFont(UITheme.FONT_HEADER_SMALL);
        lbl.setForeground(UITheme.ACCENT_CYAN);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"STATION ID", "CODE", "NAME", "ZONE", "TOTAL NODES", "OCCUPIED", "AVAILABLE", "RESERVED", "MAINT", "TOTAL SESSIONS", "LIFETIME ENERGY", "UTILIZATION %"};
        modelUtilization = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableUtilization = new JTable(modelUtilization);
        UIHelper.styleTable(tableUtilization);
        p.add(UIHelper.createScrollPane(tableUtilization), BorderLayout.CENTER);
        return p;
    }

    private JPanel createEnergyTab() {
        JPanel p = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lbl = new JLabel("DAILY ENERGY CONSUMPTION BREAKDOWN (PreparedStatement Parameterized Query)");
        lbl.setFont(UITheme.FONT_HEADER_SMALL);
        lbl.setForeground(UITheme.ACCENT_CYAN);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"DATE", "TOTAL SESSIONS", "ENERGY CONSUMED (kWh)", "AVG ENERGY / SESSION (kWh)", "ESTIMATED REVENUE (₹)"};
        modelEnergy = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableEnergy = new JTable(modelEnergy);
        UIHelper.styleTable(tableEnergy);
        p.add(UIHelper.createScrollPane(tableEnergy), BorderLayout.CENTER);
        return p;
    }

    private JPanel createRevenueTab() {
        JPanel p = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lbl = new JLabel("REVENUE & SETTLEMENT SUMMARY BY PAYMENT CHANNEL");
        lbl.setFont(UITheme.FONT_HEADER_SMALL);
        lbl.setForeground(UITheme.ACCENT_CYAN);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"SETTLEMENT DATE", "PAYMENT METHOD", "TRANSACTION COUNT", "TOTAL SETTLED AMOUNT (₹)"};
        modelRevenue = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableRevenue = new JTable(modelRevenue);
        UIHelper.styleTable(tableRevenue);
        p.add(UIHelper.createScrollPane(tableRevenue), BorderLayout.CENTER);
        return p;
    }

    private JPanel createFleetTab() {
        JPanel p = UIHelper.createCardPanel(new BorderLayout(8, 8));
        JLabel lbl = new JLabel("CAMPUS FLEET SUSTAINABILITY & CLEAN CARBON OFFSET (SDG 7, 9, 11)");
        lbl.setFont(UITheme.FONT_HEADER_SMALL);
        lbl.setForeground(UITheme.ACCENT_EMERALD);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"VEHICLE NUMBER", "BRAND & MODEL", "TYPE", "OWNER", "CAMPUS ROLE", "SESSIONS", "TOTAL ENERGY (kWh)", "TOTAL SPENT (₹)", "CO2 OFFSET (kg CO2)"};
        modelFleet = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableFleet = new JTable(modelFleet);
        UIHelper.styleTable(tableFleet);
        p.add(UIHelper.createScrollPane(tableFleet), BorderLayout.CENTER);
        return p;
    }

    public void selectTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    public void refreshAllReports() {
        try {
            // 1. Station Utilization via Stored Procedure
            List<StationUtilizationDTO> utilList = analyticsService.getStationUtilizationReport(0);
            modelUtilization.setRowCount(0);
            for (StationUtilizationDTO u : utilList) {
                modelUtilization.addRow(new Object[]{
                    u.getStationId(),
                    u.getStationCode(),
                    u.getStationName(),
                    u.getCampusZone(),
                    u.getTotalPoints(),
                    u.getOccupiedPoints(),
                    u.getAvailablePoints(),
                    u.getReservedPoints(),
                    u.getMaintenancePoints(),
                    u.getTotalLifetimeSessions(),
                    CurrencyUtil.formatKwh(u.getTotalEnergyDeliveredKwh()),
                    CurrencyUtil.formatPercent(u.getCurrentUtilizationPercent())
                });
            }

            // Date bounds
            Calendar cal = Calendar.getInstance();
            Date endDate = new Date(cal.getTimeInMillis());
            int idx = cmbDatePreset != null ? cmbDatePreset.getSelectedIndex() : 2;
            if (idx == 0) cal.add(Calendar.DAY_OF_YEAR, 0);       // Today
            else if (idx == 1) cal.add(Calendar.DAY_OF_YEAR, -7); // 7 days
            else if (idx == 2) cal.add(Calendar.DAY_OF_YEAR, -30);// 30 days
            else cal.add(Calendar.YEAR, -5);                      // All time
            Date startDate = new Date(cal.getTimeInMillis());

            // 2. Energy Consumption
            List<Map<String, Object>> energyList = analyticsService.getEnergyConsumptionReport(startDate, endDate);
            modelEnergy.setRowCount(0);
            for (Map<String, Object> row : energyList) {
                modelEnergy.addRow(new Object[]{
                    row.get("report_date"),
                    row.get("total_sessions"),
                    CurrencyUtil.formatKwh((Double) row.get("total_energy_kwh")),
                    CurrencyUtil.formatKwh((Double) row.get("avg_energy_kwh")),
                    CurrencyUtil.formatINR((Double) row.get("total_revenue"))
                });
            }

            // 3. Revenue Report
            List<Map<String, Object>> revList = analyticsService.getRevenueReport(startDate, endDate);
            modelRevenue.setRowCount(0);
            for (Map<String, Object> row : revList) {
                modelRevenue.addRow(new Object[]{
                    row.get("payment_date"),
                    row.get("payment_method"),
                    row.get("payment_count"),
                    CurrencyUtil.formatINR((Double) row.get("total_settled"))
                });
            }

            // 4. Fleet & Carbon Offset
            List<Map<String, Object>> fleetList = analyticsService.getVehicleUsageReport();
            modelFleet.setRowCount(0);
            for (Map<String, Object> row : fleetList) {
                modelFleet.addRow(new Object[]{
                    row.get("vehicle_number"),
                    row.get("brand_model"),
                    row.get("vehicle_type"),
                    row.get("owner_name"),
                    row.get("campus_role"),
                    row.get("total_sessions"),
                    CurrencyUtil.formatKwh((Double) row.get("total_energy_kwh")),
                    CurrencyUtil.formatINR((Double) row.get("total_paid")),
                    row.get("co2_offset_kg") + " kg"
                });
            }

        } catch (Exception e) {
            System.err.println("Notice refreshing reports: " + e.getMessage());
        }
    }

    private void onExportCsv() {
        JTable currentTable;
        String defaultName;
        int activeTab = tabbedPane.getSelectedIndex();
        if (activeTab == 0) { currentTable = tableUtilization; defaultName = "station_utilization.csv"; }
        else if (activeTab == 1) { currentTable = tableEnergy; defaultName = "energy_consumption.csv"; }
        else if (activeTab == 2) { currentTable = tableRevenue; defaultName = "revenue_report.csv"; }
        else { currentTable = tableFleet; defaultName = "fleet_carbon_offset.csv"; }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(defaultName));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                for (int c = 0; c < currentTable.getColumnCount(); c++) {
                    fw.write("\"" + currentTable.getColumnName(c) + "\"" + (c == currentTable.getColumnCount() - 1 ? "\n" : ","));
                }
                for (int r = 0; r < currentTable.getRowCount(); r++) {
                    for (int c = 0; c < currentTable.getColumnCount(); c++) {
                        Object val = currentTable.getValueAt(r, c);
                        fw.write("\"" + (val != null ? val.toString().replace("\"", "\"\"") : "") + "\"" + (c == currentTable.getColumnCount() - 1 ? "\n" : ","));
                    }
                }
                UIHelper.showSuccess(this, "Report exported to " + file.getAbsolutePath());
            } catch (Exception ex) {
                UIHelper.showError(this, "Failed to export CSV: " + ex.getMessage());
            }
        }
    }

    private void onCopySummary() {
        try {
            CampusEnergySummaryDTO summary = analyticsService.getCampusEnergySummary(30);
            String summaryText = 
                "=====================================================\n" +
                "  SMART CAMPUS EV CHARGING CONTROL CENTER REPORT\n" +
                "=====================================================\n" +
                "Lifetime Sessions Recorded: " + summary.getTotalSessions() + "\n" +
                "Total Energy Delivered:     " + CurrencyUtil.formatKwh(summary.getTotalEnergyKwh()) + "\n" +
                "Gross Revenue Settled:      " + CurrencyUtil.formatINR(summary.getTotalRevenue()) + "\n" +
                "Unique Active Drivers:      " + summary.getUniqueActiveUsers() + "\n" +
                "Unique Campus Vehicles:     " + summary.getUniqueVehicles() + "\n" +
                "Average Session Duration:   " + String.format("%.1f mins", summary.getAvgDurationMins()) + "\n" +
                "Green Carbon Offset Avoided:" + summary.getCarbonOffsetKgCo2() + " kg CO2 (SDG 7, 11)\n" +
                "=====================================================\n";

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(summaryText), null);
            UIHelper.showSuccess(this, "Analytics summary copied to clipboard!\n\n" + summaryText);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to generate summary: " + ex.getMessage());
        }
    }
}
