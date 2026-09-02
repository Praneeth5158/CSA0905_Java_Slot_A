package com.campus.ev.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String userCode;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String campusRole; // STUDENT, FACULTY, CAMPUS_FLEET, FACILITY_STAFF, VISITOR
    private String status;     // ACTIVE, SUSPENDED, INACTIVE
    private String rfidCardUid;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {}

    public User(int userId, String userCode, String fullName, String email, String phone, 
                String department, String campusRole, String status, String rfidCardUid) {
        this.userId = userId;
        this.userCode = userCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.campusRole = campusRole;
        this.status = status;
        this.rfidCardUid = rfidCardUid;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCampusRole() { return campusRole; }
    public void setCampusRole(String campusRole) { this.campusRole = campusRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRfidCardUid() { return rfidCardUid; }
    public void setRfidCardUid(String rfidCardUid) { this.rfidCardUid = rfidCardUid; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return fullName + " (" + userCode + " - " + campusRole + ")";
    }
}
