package com.campus.ev.ui.management;

import com.campus.ev.dao.UserDAO;
import com.campus.ev.model.User;
import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import com.campus.ev.validation.InputValidator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();

    // Form inputs
    private JTextField txtUserCode;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtDepartment;
    private JComboBox<String> cmbRole;
    private JComboBox<String> cmbStatus;
    private JTextField txtRfidUid;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Search & Table
    private JTextField txtSearch;
    private JComboBox<String> cmbRoleFilter;
    private DefaultTableModel tableModel;
    private JTable table;

    private int selectedUserId = 0;

    public UserManagementPanel(MainFrame mainFrame) {
        super(new BorderLayout(14, 14));
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG_PANEL);
        setBorder(new EmptyBorder(14, 18, 18, 18));

        initComponents();
        refreshUsers();
    }

    private void initComponents() {
        // TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout(10, 10));
        topHeader.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("CAMPUS USER & ROLE MANAGEMENT");
        lblTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Manage faculty, students, fleet operators, and campus RFID authentication cards");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Role:"));
        cmbRoleFilter = new JComboBox<>(new String[]{"ALL", "STUDENT", "FACULTY", "CAMPUS_FLEET", "FACILITY_STAFF", "VISITOR"});
        cmbRoleFilter.setFont(UITheme.FONT_SMALL);
        cmbRoleFilter.setBackground(UITheme.BG_INPUT);
        cmbRoleFilter.setForeground(UITheme.TEXT_PRIMARY);
        cmbRoleFilter.addActionListener(e -> refreshUsers());
        searchPanel.add(cmbRoleFilter);

        txtSearch = UIHelper.createTextField(14);
        JButton btnSearch = UIHelper.createSecondaryButton("🔍 Search");
        btnSearch.addActionListener(e -> refreshUsers());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(searchPanel, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // MAIN SPLIT: Form on Left (360px), Table on Right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(360);
        splitPane.setResizeWeight(0.32);

        // FORM CARD
        JPanel formCard = UIHelper.createCardPanel(new BorderLayout(10, 10));
        JLabel lblFormTitle = new JLabel("USER IDENTITY & CAMPUS PROFILE");
        lblFormTitle.setFont(UITheme.FONT_HEADER_SMALL);
        lblFormTitle.setForeground(UITheme.TEXT_CYAN);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(8, 2, 6, 8));
        fields.setOpaque(false);

        fields.add(UIHelper.createFormLabel("User Code / ID:"));
        txtUserCode = UIHelper.createTextField(10);
        fields.add(txtUserCode);

        fields.add(UIHelper.createFormLabel("Full Name:"));
        txtFullName = UIHelper.createTextField(10);
        fields.add(txtFullName);

        fields.add(UIHelper.createFormLabel("Email:"));
        txtEmail = UIHelper.createTextField(10);
        fields.add(txtEmail);

        fields.add(UIHelper.createFormLabel("Phone:"));
        txtPhone = UIHelper.createTextField(10);
        fields.add(txtPhone);

        fields.add(UIHelper.createFormLabel("Department:"));
        txtDepartment = UIHelper.createTextField(10);
        fields.add(txtDepartment);

        fields.add(UIHelper.createFormLabel("Campus Role:"));
        cmbRole = new JComboBox<>(new String[]{"STUDENT", "FACULTY", "CAMPUS_FLEET", "FACILITY_STAFF", "VISITOR"});
        cmbRole.setFont(UITheme.FONT_REGULAR);
        cmbRole.setBackground(UITheme.BG_INPUT);
        cmbRole.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbRole);

        fields.add(UIHelper.createFormLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "SUSPENDED", "INACTIVE"});
        cmbStatus.setFont(UITheme.FONT_REGULAR);
        cmbStatus.setBackground(UITheme.BG_INPUT);
        cmbStatus.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(cmbStatus);

        fields.add(UIHelper.createFormLabel("RFID Card UID:"));
        txtRfidUid = UIHelper.createTextField(10);
        fields.add(txtRfidUid);

        formCard.add(fields, BorderLayout.CENTER);

        // Actions
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 6, 0));
        actionRow.setOpaque(false);

        btnAdd = UIHelper.createSuccessButton("Add");
        btnAdd.addActionListener(e -> onAddUser());

        btnUpdate = UIHelper.createPrimaryButton("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdateUser());

        btnDelete = UIHelper.createDangerButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteUser());

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
        String[] cols = {"USER ID", "CODE", "NAME", "ROLE", "DEPARTMENT", "EMAIL", "PHONE", "RFID CARD", "STATUS"};
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

    private void onAddUser() {
        try {
            InputValidator.validateNotEmpty(txtUserCode.getText(), "User Code");
            InputValidator.validateNotEmpty(txtFullName.getText(), "Full Name");
            InputValidator.validateEmail(txtEmail.getText());
            InputValidator.validatePhone(txtPhone.getText());
            InputValidator.validateNotEmpty(txtDepartment.getText(), "Department");

            String code = txtUserCode.getText().trim().toUpperCase();
            String email = txtEmail.getText().trim().toLowerCase();

            if (userDAO.isUserCodeExists(code, 0)) {
                UIHelper.showError(this, "User code " + code + " already exists.");
                return;
            }
            if (userDAO.isEmailExists(email, 0)) {
                UIHelper.showError(this, "Email address " + email + " is already registered.");
                return;
            }

            User u = new User();
            u.setUserCode(code);
            u.setFullName(txtFullName.getText().trim());
            u.setEmail(email);
            u.setPhone(txtPhone.getText().trim());
            u.setDepartment(txtDepartment.getText().trim());
            u.setCampusRole((String) cmbRole.getSelectedItem());
            u.setStatus((String) cmbStatus.getSelectedItem());
            u.setRfidCardUid(txtRfidUid.getText().trim());

            int id = userDAO.insertUser(u);
            UIHelper.showSuccess(this, "Campus user registered successfully! ID: #" + id);
            clearForm();
            refreshUsers();
        } catch (Exception ex) {
            UIHelper.showError(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void onUpdateUser() {
        if (selectedUserId <= 0) return;
        try {
            InputValidator.validateNotEmpty(txtUserCode.getText(), "User Code");
            InputValidator.validateNotEmpty(txtFullName.getText(), "Full Name");
            InputValidator.validateEmail(txtEmail.getText());
            InputValidator.validatePhone(txtPhone.getText());
            InputValidator.validateNotEmpty(txtDepartment.getText(), "Department");

            String code = txtUserCode.getText().trim().toUpperCase();
            String email = txtEmail.getText().trim().toLowerCase();

            if (userDAO.isUserCodeExists(code, selectedUserId)) {
                UIHelper.showError(this, "User code " + code + " belongs to another user.");
                return;
            }
            if (userDAO.isEmailExists(email, selectedUserId)) {
                UIHelper.showError(this, "Email " + email + " is already registered to another user.");
                return;
            }

            User u = new User();
            u.setUserId(selectedUserId);
            u.setUserCode(code);
            u.setFullName(txtFullName.getText().trim());
            u.setEmail(email);
            u.setPhone(txtPhone.getText().trim());
            u.setDepartment(txtDepartment.getText().trim());
            u.setCampusRole((String) cmbRole.getSelectedItem());
            u.setStatus((String) cmbStatus.getSelectedItem());
            u.setRfidCardUid(txtRfidUid.getText().trim());

            userDAO.updateUser(u);
            UIHelper.showSuccess(this, "User updated successfully!");
            clearForm();
            refreshUsers();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update Error: " + ex.getMessage());
        }
    }

    private void onDeleteUser() {
        if (selectedUserId <= 0) return;
        boolean confirm = UIHelper.showConfirm(this, "Delete user #" + selectedUserId + "?", "Confirm Delete");
        if (confirm) {
            try {
                userDAO.deleteUser(selectedUserId);
                UIHelper.showSuccess(this, "User record removed.");
                clearForm();
                refreshUsers();
            } catch (Exception ex) {
                UIHelper.showError(this, "Cannot delete user with registered vehicles or sessions: " + ex.getMessage());
            }
        }
    }

    private void onTableRowSelected(int row) {
        selectedUserId = (int) tableModel.getValueAt(row, 0);
        try {
            User u = userDAO.getUserById(selectedUserId);
            if (u != null) {
                txtUserCode.setText(u.getUserCode());
                txtFullName.setText(u.getFullName());
                txtEmail.setText(u.getEmail());
                txtPhone.setText(u.getPhone());
                txtDepartment.setText(u.getDepartment());
                cmbRole.setSelectedItem(u.getCampusRole());
                cmbStatus.setSelectedItem(u.getStatus());
                txtRfidUid.setText(u.getRfidCardUid() != null ? u.getRfidCardUid() : "");

                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        } catch (Exception ignored) {}
    }

    private void clearForm() {
        selectedUserId = 0;
        txtUserCode.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDepartment.setText("");
        txtRfidUid.setText("");
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    public void refreshUsers() {
        try {
            String query = txtSearch != null ? txtSearch.getText().trim() : "";
            String roleFilter = cmbRoleFilter != null ? (String) cmbRoleFilter.getSelectedItem() : "ALL";

            List<User> list = userDAO.searchUsers(query, roleFilter);
            tableModel.setRowCount(0);
            for (User u : list) {
                tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUserCode(),
                    u.getFullName(),
                    u.getCampusRole(),
                    u.getDepartment(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getRfidCardUid() != null ? u.getRfidCardUid() : "-",
                    u.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Notice refreshing users: " + e.getMessage());
        }
    }
}
