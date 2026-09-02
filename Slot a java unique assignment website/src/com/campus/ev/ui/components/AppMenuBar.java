package com.campus.ev.ui.components;

import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.KeyEvent;

public class AppMenuBar extends JMenuBar {

    public AppMenuBar(MainFrame mainFrame) {
        setBackground(UITheme.BG_DARKEST);
        setBorder(new LineBorder(UITheme.BORDER, 1));

        // 1. FILE MENU
        JMenu menuFile = createStyledMenu("File", KeyEvent.VK_F);
        
        JMenuItem itemNewRes = createStyledMenuItem("New Reservation", KeyEvent.VK_N);
        itemNewRes.addActionListener(e -> mainFrame.showNewReservationDialog(null));

        JMenuItem itemNewSession = createStyledMenuItem("New Charging Session", KeyEvent.VK_S);
        itemNewSession.addActionListener(e -> mainFrame.showStartSessionDialog(null));

        JMenuItem itemExport = createStyledMenuItem("Reports & Analytics", KeyEvent.VK_R);
        itemExport.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_REPORTS));

        JMenuItem itemDbInit = createStyledMenuItem("Re-Initialize Database...", KeyEvent.VK_I);
        itemDbInit.addActionListener(e -> mainFrame.promptDatabaseInitialization());

        JMenuItem itemExit = createStyledMenuItem("Exit Application", KeyEvent.VK_X);
        itemExit.addActionListener(e -> System.exit(0));

        menuFile.add(itemNewRes);
        menuFile.add(itemNewSession);
        menuFile.addSeparator();
        menuFile.add(itemExport);
        menuFile.add(itemDbInit);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        add(menuFile);

        // 2. OPERATIONS MENU
        JMenu menuOps = createStyledMenu("Operations", KeyEvent.VK_O);

        JMenuItem itemCmdCenter = createStyledMenuItem("Command Center", KeyEvent.VK_C);
        itemCmdCenter.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_COMMAND_CENTER));

        JMenuItem itemGrid = createStyledMenuItem("Campus Charging Grid", KeyEvent.VK_G);
        itemGrid.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_CHARGING_GRID));

        JMenuItem itemReservations = createStyledMenuItem("Reservation Workspace", KeyEvent.VK_B);
        itemReservations.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_RESERVATIONS));

        JMenuItem itemLiveCockpit = createStyledMenuItem("Live Charging Cockpit", KeyEvent.VK_L);
        itemLiveCockpit.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_LIVE_SESSIONS));

        menuOps.add(itemCmdCenter);
        menuOps.add(itemGrid);
        menuOps.add(itemReservations);
        menuOps.add(itemLiveCockpit);
        add(menuOps);

        // 3. MANAGEMENT MENU
        JMenu menuMgmt = createStyledMenu("Management", KeyEvent.VK_M);

        JMenuItem itemVehicles = createStyledMenuItem("EV Vehicles Fleet", KeyEvent.VK_V);
        itemVehicles.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_VEHICLES));

        JMenuItem itemUsers = createStyledMenuItem("Campus Users & Roles", KeyEvent.VK_U);
        itemUsers.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_USERS));

        JMenuItem itemStations = createStyledMenuItem("Charging Stations", KeyEvent.VK_S);
        itemStations.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_STATIONS));

        JMenuItem itemPoints = createStyledMenuItem("Charging Points / Nodes", KeyEvent.VK_P);
        itemPoints.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_POINTS));

        JMenuItem itemTariffs = createStyledMenuItem("Dynamic Tariffs", KeyEvent.VK_T);
        itemTariffs.addActionListener(e -> mainFrame.navigateTo(MainFrame.VIEW_TARIFFS));

        menuMgmt.add(itemVehicles);
        menuMgmt.add(itemUsers);
        menuMgmt.addSeparator();
        menuMgmt.add(itemStations);
        menuMgmt.add(itemPoints);
        menuMgmt.add(itemTariffs);
        add(menuMgmt);

        // 4. REPORTS MENU
        JMenu menuReports = createStyledMenu("Reports", KeyEvent.VK_R);

        JMenuItem itemRepUtil = createStyledMenuItem("Station Utilization (Stored Procedure)", KeyEvent.VK_1);
        itemRepUtil.addActionListener(e -> {
            mainFrame.navigateTo(MainFrame.VIEW_REPORTS);
            mainFrame.selectReportTab(0);
        });

        JMenuItem itemRepEnergy = createStyledMenuItem("Energy Consumption Analysis", KeyEvent.VK_2);
        itemRepEnergy.addActionListener(e -> {
            mainFrame.navigateTo(MainFrame.VIEW_REPORTS);
            mainFrame.selectReportTab(1);
        });

        JMenuItem itemRepRev = createStyledMenuItem("Revenue & Settlement Report", KeyEvent.VK_3);
        itemRepRev.addActionListener(e -> {
            mainFrame.navigateTo(MainFrame.VIEW_REPORTS);
            mainFrame.selectReportTab(2);
        });

        JMenuItem itemRepFleet = createStyledMenuItem("Vehicle Usage & Green Carbon Offset", KeyEvent.VK_4);
        itemRepFleet.addActionListener(e -> {
            mainFrame.navigateTo(MainFrame.VIEW_REPORTS);
            mainFrame.selectReportTab(3);
        });

        menuReports.add(itemRepUtil);
        menuReports.add(itemRepEnergy);
        menuReports.add(itemRepRev);
        menuReports.add(itemRepFleet);
        add(menuReports);

        // 5. HELP MENU
        JMenu menuHelp = createStyledMenu("Help", KeyEvent.VK_H);

        JMenuItem itemDbConfig = createStyledMenuItem("Database Configuration...", KeyEvent.VK_D);
        itemDbConfig.addActionListener(e -> mainFrame.showDbConnectionDialog());

        JMenuItem itemAbout = createStyledMenuItem("About System & SDG Goals...", KeyEvent.VK_A);
        itemAbout.addActionListener(e -> mainFrame.showAboutDialog());

        menuHelp.add(itemDbConfig);
        menuHelp.addSeparator();
        menuHelp.add(itemAbout);
        add(menuHelp);
    }

    private JMenu createStyledMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        menu.setFont(UITheme.FONT_REGULAR_BOLD);
        menu.setForeground(UITheme.TEXT_PRIMARY);
        menu.getPopupMenu().setBackground(UITheme.BG_CARD);
        menu.getPopupMenu().setBorder(new LineBorder(UITheme.BORDER, 1));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String title, int mnemonic) {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setFont(UITheme.FONT_REGULAR);
        item.setBackground(UITheme.BG_CARD);
        item.setForeground(UITheme.TEXT_PRIMARY);
        item.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return item;
    }
}
