package com.campus.ev.ui;

import com.campus.ev.config.DatabaseConfig;
import com.campus.ev.db.ConnectionManager;
import com.campus.ev.db.DatabaseInitializer;
import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.OperationalSummaryDTO;
import com.campus.ev.ui.components.AppMenuBar;
import com.campus.ev.ui.components.TopCommandBar;
import com.campus.ev.ui.dashboard.CommandCenterPanel;
import com.campus.ev.ui.dialogs.AboutDialog;
import com.campus.ev.ui.dialogs.DbConnectionDialog;
import com.campus.ev.ui.dialogs.NewReservationDialog;
import com.campus.ev.ui.dialogs.StartSessionDialog;
import com.campus.ev.ui.grid.CampusGridPanel;
import com.campus.ev.ui.management.*;
import com.campus.ev.ui.reports.ReportsAnalyticsPanel;
import com.campus.ev.ui.reservation.ReservationWorkspacePanel;
import com.campus.ev.ui.session.LiveSessionCockpitPanel;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    // View Identifiers
    public static final String VIEW_COMMAND_CENTER = "COMMAND_CENTER";
    public static final String VIEW_CHARGING_GRID  = "CHARGING_GRID";
    public static final String VIEW_RESERVATIONS   = "RESERVATIONS";
    public static final String VIEW_LIVE_SESSIONS  = "LIVE_SESSIONS";
    public static final String VIEW_VEHICLES       = "VEHICLES";
    public static final String VIEW_USERS          = "USERS";
    public static final String VIEW_STATIONS       = "STATIONS";
    public static final String VIEW_POINTS         = "POINTS";
    public static final String VIEW_TARIFFS        = "TARIFFS";
    public static final String VIEW_REPORTS        = "REPORTS";

    private final TopCommandBar topCommandBar;
    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    // View Panels
    private CommandCenterPanel panelCommandCenter;
    private CampusGridPanel panelChargingGrid;
    private ReservationWorkspacePanel panelReservations;
    private LiveSessionCockpitPanel panelLiveSessions;
    private VehicleManagementPanel panelVehicles;
    private UserManagementPanel panelUsers;
    private StationManagementPanel panelStations;
    private ChargingPointManagementPanel panelPoints;
    private TariffManagementPanel panelTariffs;
    private ReportsAnalyticsPanel panelReports;

    // Navigation Buttons
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String currentView = VIEW_COMMAND_CENTER;

    // Status Bar
    private final JLabel lblStatusDb;
    private final JLabel lblStatusMode;

    public MainFrame() {
        super("Smart Campus EV Charging Control Center • University Facilities & Energy Management");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 880);
        setMinimumSize(new Dimension(1150, 750));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARKEST);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectionManager.closeConnection();
                System.exit(0);
            }
        });

        // 1. MENU BAR
        setJMenuBar(new AppMenuBar(this));

        // 2. TOP PANEL: Command Bar + Quick Navigation Switcher Bar
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        topCommandBar = new TopCommandBar(
            this::refreshAllViews,
            this::showDbConnectionDialog
        );
        topContainer.add(topCommandBar, BorderLayout.NORTH);

        JPanel navBar = createNavigationBar();
        topContainer.add(navBar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 3. CENTER: CardLayout Workspace Container
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setBackground(UITheme.BG_PANEL);

        initViewPanels();
        add(cardsContainer, BorderLayout.CENTER);

        // 4. BOTTOM STATUS BAR
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setBackground(UITheme.BG_DARKEST);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1),
            new EmptyBorder(6, 18, 6, 18)
        ));

        lblStatusDb = new JLabel("● Database: " + DatabaseConfig.getDatabase() + " (" + DatabaseConfig.getHost() + ":" + DatabaseConfig.getPort() + ")");
        lblStatusDb.setFont(UITheme.FONT_REGULAR_BOLD);
        lblStatusDb.setForeground(UITheme.ACCENT_EMERALD);

        lblStatusMode = new JLabel("Smart University Energy Grid Control • Java Swing + JDBC + MySQL");
        lblStatusMode.setFont(UITheme.FONT_REGULAR);
        lblStatusMode.setForeground(UITheme.TEXT_SECONDARY);

        statusBar.add(lblStatusDb, BorderLayout.WEST);
        statusBar.add(lblStatusMode, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        navigateTo(VIEW_COMMAND_CENTER);
    }

    private JPanel createNavigationBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 6));
        bar.setBackground(new Color(18, 26, 45));
        bar.setBorder(new LineBorder(UITheme.BORDER, 1));

        addNavButton(bar, VIEW_COMMAND_CENTER, "⚡ Command Center");
        addNavButton(bar, VIEW_CHARGING_GRID,  "🗺 Campus Grid");
        addNavButton(bar, VIEW_RESERVATIONS,   "📅 Reservations");
        addNavButton(bar, VIEW_LIVE_SESSIONS,  "🔋 Live Cockpit");
        addNavButton(bar, VIEW_VEHICLES,       "🚗 EV Fleet");
        addNavButton(bar, VIEW_USERS,          "👥 Users & Roles");
        addNavButton(bar, VIEW_STATIONS,       "🏢 Stations");
        addNavButton(bar, VIEW_POINTS,         "🔌 Charging Points");
        addNavButton(bar, VIEW_TARIFFS,        "💰 Tariffs");
        addNavButton(bar, VIEW_REPORTS,        "📊 Reports & Stored Proc");

        return bar;
    }

    private void addNavButton(JPanel bar, String viewKey, String label) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_REGULAR_BOLD);
        btn.setBackground(UITheme.BG_CARD);
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigateTo(viewKey));

        navButtons.put(viewKey, btn);
        bar.add(btn);
    }

    private void initViewPanels() {
        panelCommandCenter = new CommandCenterPanel(this);
        panelChargingGrid  = new CampusGridPanel(this);
        panelReservations  = new ReservationWorkspacePanel(this);
        panelLiveSessions  = new LiveSessionCockpitPanel(this);
        panelVehicles      = new VehicleManagementPanel(this);
        panelUsers         = new UserManagementPanel(this);
        panelStations      = new StationManagementPanel(this);
        panelPoints        = new ChargingPointManagementPanel(this);
        panelTariffs       = new TariffManagementPanel(this);
        panelReports       = new ReportsAnalyticsPanel(this);

        cardsContainer.add(panelCommandCenter, VIEW_COMMAND_CENTER);
        cardsContainer.add(panelChargingGrid,  VIEW_CHARGING_GRID);
        cardsContainer.add(panelReservations,  VIEW_RESERVATIONS);
        cardsContainer.add(panelLiveSessions,  VIEW_LIVE_SESSIONS);
        cardsContainer.add(panelVehicles,      VIEW_VEHICLES);
        cardsContainer.add(panelUsers,         VIEW_USERS);
        cardsContainer.add(panelStations,      VIEW_STATIONS);
        cardsContainer.add(panelPoints,        VIEW_POINTS);
        cardsContainer.add(panelTariffs,       VIEW_TARIFFS);
        cardsContainer.add(panelReports,       VIEW_REPORTS);
    }

    public void navigateTo(String viewKey) {
        this.currentView = viewKey;
        cardLayout.show(cardsContainer, viewKey);

        // Highlight selected nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(viewKey);
            JButton b = entry.getValue();
            b.setBackground(active ? UITheme.ACCENT_CYAN : UITheme.BG_CARD);
            b.setForeground(active ? UITheme.BG_DARKEST : UITheme.TEXT_SECONDARY);
            b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(active ? UITheme.ACCENT_CYAN : UITheme.BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
            ));
        }

        // Trigger contextual refresh on the newly active panel
        if (VIEW_COMMAND_CENTER.equals(viewKey)) panelCommandCenter.refreshData();
        else if (VIEW_CHARGING_GRID.equals(viewKey)) panelChargingGrid.refreshData();
        else if (VIEW_RESERVATIONS.equals(viewKey)) panelReservations.refreshReservations();
        else if (VIEW_LIVE_SESSIONS.equals(viewKey)) panelLiveSessions.refreshSessions();
        else if (VIEW_VEHICLES.equals(viewKey)) panelVehicles.refreshVehicles();
        else if (VIEW_USERS.equals(viewKey)) panelUsers.refreshUsers();
        else if (VIEW_STATIONS.equals(viewKey)) panelStations.refreshStations();
        else if (VIEW_POINTS.equals(viewKey)) panelPoints.refreshPoints();
        else if (VIEW_TARIFFS.equals(viewKey)) panelTariffs.refreshTariffs();
        else if (VIEW_REPORTS.equals(viewKey)) panelReports.refreshAllReports();
    }

    public void selectReportTab(int tabIndex) {
        panelReports.selectTab(tabIndex);
    }

    public void refreshAllViews() {
        try {
            panelCommandCenter.refreshData();
            panelChargingGrid.refreshData();
            panelReservations.refreshReservations();
            panelLiveSessions.refreshSessions();
            panelVehicles.refreshVehicles();
            panelUsers.refreshUsers();
            panelStations.refreshStations();
            panelPoints.refreshPoints();
            panelTariffs.refreshTariffs();
            panelReports.refreshAllReports();

            lblStatusDb.setText("● Database: " + DatabaseConfig.getDatabase() + " (" + DatabaseConfig.getHost() + ":" + DatabaseConfig.getPort() + ") [Connected]");
            lblStatusDb.setForeground(UITheme.ACCENT_EMERALD);
        } catch (Exception ex) {
            lblStatusDb.setText("● Database Offline: " + ex.getMessage());
            lblStatusDb.setForeground(UITheme.ACCENT_ROSE);
        }
    }

    public void updateTopBarTelemetry(OperationalSummaryDTO dto) {
        topCommandBar.updateTelemetry(dto);
    }

    public void showNewReservationDialog(ChargingPoint cp) {
        new NewReservationDialog(this, cp).setVisible(true);
    }

    public void showStartSessionDialog(ChargingPoint cp) {
        new StartSessionDialog(this, cp).setVisible(true);
    }

    public void showDbConnectionDialog() {
        new DbConnectionDialog(this).setVisible(true);
    }

    public void showAboutDialog() {
        new AboutDialog(this).setVisible(true);
    }

    public void promptDatabaseInitialization() {
        boolean confirm = UIHelper.showConfirm(this,
            "Re-initialize database with fresh schema, sample data, and stored procedures?\n\n" +
            "WARNING: This will reset all records to the default demo state.",
            "Initialize Database");
        if (confirm) {
            try {
                DatabaseInitializer.initializeDatabase();
                UIHelper.showSuccess(this, "Database successfully re-initialized with sample campus data and stored procedures!");
                refreshAllViews();
            } catch (Exception ex) {
                UIHelper.showError(this, "Database initialization failed: " + ex.getMessage());
            }
        }
    }
}
