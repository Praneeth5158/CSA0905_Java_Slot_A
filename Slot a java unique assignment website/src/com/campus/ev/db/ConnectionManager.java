package com.campus.ev.db;

import com.campus.ev.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Robust singleton JDBC connection manager for MySQL 8.0+.
 * Supports connection validation, auto-reconnect, and transaction boundaries.
 */
public class ConnectionManager {

    private static Connection connection = null;

    static {
        try {
            // Load MySQL Connector/J driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older driver names
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("CRITICAL: MySQL JDBC Driver not found in classpath! " + ex.getMessage());
            }
        }
    }

    private ConnectionManager() {}

    /**
     * Obtains an active connection to the database. Re-establishes connection if closed or invalid.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            String url = DatabaseConfig.getJdbcUrl();
            String user = DatabaseConfig.getUsername();
            String pass = DatabaseConfig.getPassword();
            connection = DriverManager.getConnection(url, user, pass);
        }
        return connection;
    }

    /**
     * Creates a new separate Connection instance (useful for isolated background worker tasks or transactions).
     */
    public static Connection createNewConnection() throws SQLException {
        String url = DatabaseConfig.getJdbcUrl();
        String user = DatabaseConfig.getUsername();
        String pass = DatabaseConfig.getPassword();
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Connects to the MySQL server directly without specifying a database name (used during schema auto-creation).
     */
    public static Connection getBaseConnection() throws SQLException {
        String url = DatabaseConfig.getBaseJdbcUrl();
        String user = DatabaseConfig.getUsername();
        String pass = DatabaseConfig.getPassword();
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Tests if connection can be established with given parameters.
     */
    public static boolean testConnection(String host, int port, String database, String user, String pass) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection testConn = DriverManager.getConnection(url, user, pass)) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes the active singleton connection safely.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
