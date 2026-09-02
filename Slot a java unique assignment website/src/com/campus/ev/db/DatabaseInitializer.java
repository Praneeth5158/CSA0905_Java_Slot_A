package com.campus.ev.db;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automates schema creation, stored procedure installation, and sample data population.
 * Enables zero-friction evaluation by professors and examiners.
 */
public class DatabaseInitializer {

    public static boolean isDatabaseInitialized() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM charging_points")) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            // Database or table does not exist yet
            return false;
        }
        return false;
    }

    public static void initializeDatabase() throws SQLException, IOException {
        // Step 1: Create database if missing using base connection
        try (Connection baseConn = ConnectionManager.getBaseConnection();
             Statement stmt = baseConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS campus_ev_db");
        }

        // Step 2: Run schema.sql
        executeSqlFile("database/schema.sql");

        // Step 3: Run sample_data.sql
        executeSqlFile("database/sample_data.sql");

        // Step 4: Run procedures.sql
        executeProceduresSqlFile("database/procedures.sql");
    }

    public static void executeSqlFile(String filePath) throws SQLException, IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("SQL script not found: " + file.getAbsolutePath());
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        }

        String[] statements = sb.toString().split(";");
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    try {
                        stmt.execute(trimmedSql);
                    } catch (SQLException ex) {
                        System.err.println("Notice executing SQL statement: " + ex.getMessage());
                    }
                }
            }
        }
    }

    public static void executeProceduresSqlFile(String filePath) throws SQLException, IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        // Parse stored procedures delimited by $$ or standard delimiters
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("--") || line.trim().startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        }

        String content = sb.toString();
        // Extract procedures
        String[] procedures = content.split("(?i)DELIMITER");
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String part : procedures) {
                String trimmed = part.trim();
                if (trimmed.startsWith("$$")) {
                    trimmed = trimmed.substring(2);
                }
                if (trimmed.endsWith("$$")) {
                    trimmed = trimmed.substring(0, trimmed.length() - 2);
                }
                String[] innerStatements = trimmed.split("\\$\\$");
                for (String proc : innerStatements) {
                    String cleanProc = proc.trim();
                    if (!cleanProc.isEmpty() && !cleanProc.equalsIgnoreCase(";")) {
                        try {
                            stmt.execute(cleanProc);
                        } catch (SQLException ex) {
                            System.err.println("Notice installing procedure: " + ex.getMessage());
                        }
                    }
                }
            }
        }
    }
}
