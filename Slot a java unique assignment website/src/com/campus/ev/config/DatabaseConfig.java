package com.campus.ev.config;

import java.io.*;
import java.util.Properties;

/**
 * Manages database connection configurations with persistent properties file fallback.
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "db.properties";
    
    private static String host = "localhost";
    private static int port = 3306;
    private static String database = "campus_ev_db";
    private static String username = "root";
    private static String password = "root"; // Common default, user can change via DB Config UI

    static {
        loadConfig();
    }

    public static synchronized void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                database = props.getProperty("db.name", database);
                username = props.getProperty("db.user", username);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("Warning: Could not read db.properties, using defaults. " + e.getMessage());
            }
        }
    }

    public static synchronized void saveConfig(String newHost, int newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        database = newDb;
        username = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Smart Campus EV Charging DB Configuration");
        } catch (IOException e) {
            System.err.println("Failed to persist db.properties: " + e.getMessage());
        }
    }

    public static String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&allowMultiQueries=true";
    }

    public static String getBaseJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&allowMultiQueries=true";
    }

    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDatabase() { return database; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
