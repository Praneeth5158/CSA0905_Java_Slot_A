package com.campus.ev;

import com.campus.ev.config.DatabaseConfig;
import com.campus.ev.db.ConnectionManager;
import com.campus.ev.db.DatabaseInitializer;
import com.campus.ev.ui.MainFrame;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // 1. Setup UI System Properties
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            System.setProperty("sun.java2d.opengl", "true");
        } catch (Exception ignored) {}

        // 2. Launch Main Interface on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Database if not already setup
                new Thread(() -> {
                    try {
                        if (!DatabaseInitializer.isDatabaseInitialized()) {
                            System.out.println("Notice: Initializing database schema, sample data, and stored procedures...");
                            DatabaseInitializer.initializeDatabase();
                            System.out.println("Database auto-initialized successfully.");
                        }
                    } catch (Exception e) {
                        System.err.println("Notice: Database initialization deferred (can configure via Settings dialog): " + e.getMessage());
                    }
                }).start();

                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to launch Smart Campus EV Control Center: " + ex.getMessage(), 
                    "Launch Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
