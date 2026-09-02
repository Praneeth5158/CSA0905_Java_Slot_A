package com.campus.ev.ui.grid;

import com.campus.ev.dao.ChargingPointDAO;
import com.campus.ev.dao.ChargingStationDAO;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.ChargingStation;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CampusGridPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ChargingStationDAO stationDAO = new ChargingStationDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();

    private final CampusMapCanvas mapCanvas;
    private final NodeInspectorPanel inspectorPanel;
    private final JComboBox<String> cmbStationFilter;

    public CampusGridPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        // TOP: Filter & Controls Bar
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("INTERACTIVE CAMPUS CHARGING GRID");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Spatial overview of campus charging points, live state, and telemetry");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        // CENTER: Main Interactive Split Workspace (Map Canvas + Contextual Inspector)
        mapCanvas = new CampusMapCanvas();
        inspectorPanel = new NodeInspectorPanel(mainFrame);

        mapCanvas.setOnPointSelected(cp -> {
            inspectorPanel.setInspectPoint(cp);
        });

        // Filter Buttons
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setOpaque(false);

        String[] filters = {"ALL", "AVAILABLE", "OCCUPIED", "RESERVED", "MAINTENANCE"};
        ButtonGroup btnGroup = new ButtonGroup();
        for (String f : filters) {
            JToggleButton tb = new JToggleButton(f);
            tb.setFont(UITheme.FONT_SMALL);
            tb.setBackground(UITheme.BG_CARD);
            tb.setForeground(f.equalsIgnoreCase("ALL") ? UITheme.TEXT_PRIMARY : UITheme.getStatusColor(f));
            tb.setFocusPainted(false);
            if (f.equalsIgnoreCase("ALL")) tb.setSelected(true);
            tb.addActionListener(e -> {
                mapCanvas.setFilter(f);
            });
            btnGroup.add(tb);
            filterPanel.add(tb);
        }

        cmbStationFilter = new JComboBox<>(new String[]{"All Campus Stations"});
        cmbStationFilter.setFont(UITheme.FONT_SMALL);
        cmbStationFilter.setBackground(UITheme.BG_INPUT);
        cmbStationFilter.setForeground(UITheme.TEXT_PRIMARY);
        filterPanel.add(cmbStationFilter);

        topBar.add(titleBlock, BorderLayout.WEST);
        topBar.add(filterPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapCanvas, inspectorPanel);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setResizeWeight(0.72);
        splitPane.setDividerLocation(780);

        add(splitPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        try {
            List<ChargingStation> stations = stationDAO.getAllStations();
            List<ChargingPoint> points = pointDAO.getAllPointsWithDetails();
            mapCanvas.setData(stations, points);

            // If a node was previously selected, refresh inspector details
            // otherwise leave idle
        } catch (Exception e) {
            System.err.println("Notice refreshing grid data: " + e.getMessage());
        }
    }

    public void selectPoint(ChargingPoint cp) {
        mapCanvas.setSelectedPoint(cp);
        inspectorPanel.setInspectPoint(cp);
    }
}
