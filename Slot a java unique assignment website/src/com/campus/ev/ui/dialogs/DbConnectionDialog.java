package com.campus.ev.ui.dialogs;

import com.campus.ev.config.DatabaseConfig;
import com.campus.ev.db.ConnectionManager;
import com.campus.ev.db.DatabaseInitializer;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DbConnectionDialog extends JDialog {

    private final MainFrame mainFrame;
    private final JTextField txtHost;
    private final JTextField txtPort;
    private final JTextField txtDatabase;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

    public DbConnectionDialog(MainFrame mainFrame) {
        super(mainFrame, "Database Connection & Schema Setup", true);
        this.mainFrame = mainFrame;

        setSize(460, 380);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 6, 20));

        JLabel lblTitle = new JLabel("MySQL Database Configuration");
        lblTitle.setFont(UITheme.FONT_HEADER_MED);
        lblTitle.setForeground(UITheme.TEXT_CYAN);

        JLabel lblSub = new JLabel("Configure connection credentials and auto-initialize tables");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        header.add(lblTitle);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // FORM FIELDS
        JPanel fields = new JPanel(new GridLayout(5, 2, 8, 10));
        fields.setOpaque(false);
        fields.setBorder(new EmptyBorder(10, 20, 10, 20));

        fields.add(UIHelper.createFormLabel("Host:"));
        txtHost = UIHelper.createTextField(10);
        txtHost.setText(DatabaseConfig.getHost());
        fields.add(txtHost);

        fields.add(UIHelper.createFormLabel("Port:"));
        txtPort = UIHelper.createTextField(10);
        txtPort.setText(String.valueOf(DatabaseConfig.getPort()));
        fields.add(txtPort);

        fields.add(UIHelper.createFormLabel("Database Name:"));
        txtDatabase = UIHelper.createTextField(10);
        txtDatabase.setText(DatabaseConfig.getDatabase());
        fields.add(txtDatabase);

        fields.add(UIHelper.createFormLabel("Username:"));
        txtUsername = UIHelper.createTextField(10);
        txtUsername.setText(DatabaseConfig.getUsername());
        fields.add(txtUsername);

        fields.add(UIHelper.createFormLabel("Password:"));
        txtPassword = new JPasswordField(DatabaseConfig.getPassword());
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.setBackground(UITheme.BG_INPUT);
        txtPassword.setForeground(UITheme.TEXT_PRIMARY);
        txtPassword.setCaretColor(UITheme.ACCENT_CYAN);
        fields.add(txtPassword);

        add(fields, BorderLayout.CENTER);

        // ACTIONS
        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 8));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 20, 16, 20));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 8, 0));
        row1.setOpaque(false);
        JButton btnTest = UIHelper.createSecondaryButton("🔌 Test Connection");
        btnTest.addActionListener(e -> onTestConnection());
        JButton btnInit = UIHelper.createWarningButton("⚡ Auto-Init Database");
        btnInit.addActionListener(e -> onAutoInitDatabase());
        row1.add(btnTest);
        row1.add(btnInit);

        JPanel row2 = new JPanel(new GridLayout(1, 2, 8, 0));
        row2.setOpaque(false);
        JButton btnSave = UIHelper.createSuccessButton("Save & Connect");
        btnSave.addActionListener(e -> onSaveAndConnect());
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        row2.add(btnSave);
        row2.add(btnCancel);

        footer.add(row1);
        footer.add(row2);
        add(footer, BorderLayout.SOUTH);
    }

    private void onTestConnection() {
        String h = txtHost.getText().trim();
        int p = Integer.parseInt(txtPort.getText().trim());
        String db = txtDatabase.getText().trim();
        String u = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        boolean ok = ConnectionManager.testConnection(h, p, db, u, pass);
        if (ok) {
            UIHelper.showSuccess(this, "Connection Successful! MySQL database is reachable.");
        } else {
            UIHelper.showError(this, "Connection Failed. Please check MySQL service, host, credentials, or create the database first.");
        }
    }

    private void onAutoInitDatabase() {
        String h = txtHost.getText().trim();
        int p = Integer.parseInt(txtPort.getText().trim());
        String db = txtDatabase.getText().trim();
        String u = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        DatabaseConfig.saveConfig(h, p, db, u, pass);
        ConnectionManager.closeConnection();

        try {
            DatabaseInitializer.initializeDatabase();
            UIHelper.showSuccess(this, "Database schema, sample data, and stored procedures initialized successfully!");
            mainFrame.refreshAllViews();
            dispose();
        } catch (Exception ex) {
            UIHelper.showError(this, "Database initialization failed: " + ex.getMessage());
        }
    }

    private void onSaveAndConnect() {
        String h = txtHost.getText().trim();
        int p = Integer.parseInt(txtPort.getText().trim());
        String db = txtDatabase.getText().trim();
        String u = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        DatabaseConfig.saveConfig(h, p, db, u, pass);
        ConnectionManager.closeConnection();

        mainFrame.refreshAllViews();
        UIHelper.showSuccess(this, "Configuration saved successfully.");
        dispose();
    }
}
