package com.campus.ev.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int logId;
    private String eventType;
    private String description;
    private String pointCode;
    private String userCode;
    private String vehicleNumber;
    private String severity; // INFO, SUCCESS, WARNING, ALERT
    private Timestamp loggedAt;

    public ActivityLog() {}

    public ActivityLog(int logId, String eventType, String description, String pointCode, 
                       String userCode, String vehicleNumber, String severity, Timestamp loggedAt) {
        this.logId = logId;
        this.eventType = eventType;
        this.description = description;
        this.pointCode = pointCode;
        this.userCode = userCode;
        this.vehicleNumber = vehicleNumber;
        this.severity = severity;
        this.loggedAt = loggedAt;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Timestamp getLoggedAt() { return loggedAt; }
    public void setLoggedAt(Timestamp loggedAt) { this.loggedAt = loggedAt; }
}
