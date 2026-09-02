package com.campus.ev.model;

import java.sql.Date;
import java.sql.Timestamp;

public class ChargingPoint {
    private int pointId;
    private String pointCode;
    private int stationId;
    private int pointNumber;
    private String connectorType; // TYPE_2_AC, CCS_2_DC, CHADEMO, GB_T_DC, BHARAT_AC_001
    private double powerRatingKw;
    private String status;        // AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
    private boolean isFastCharger;
    private String hardwareModel;
    private Date lastServiceDate;
    private Timestamp createdAt;

    // Joined helper fields
    private String stationName;
    private String stationCode;
    private String campusZone;

    // Live Telemetry / Session Helper fields
    private Integer activeSessionId;
    private String activeSessionCode;
    private String activeVehicleNumber;
    private String activeUserName;
    private Timestamp activeSessionStartTime;
    private double activeEnergyKwh;
    private double activeCost;

    public ChargingPoint() {}

    public ChargingPoint(int pointId, String pointCode, int stationId, int pointNumber, 
                         String connectorType, double powerRatingKw, String status, 
                         boolean isFastCharger, String hardwareModel, Date lastServiceDate) {
        this.pointId = pointId;
        this.pointCode = pointCode;
        this.stationId = stationId;
        this.pointNumber = pointNumber;
        this.connectorType = connectorType;
        this.powerRatingKw = powerRatingKw;
        this.status = status;
        this.isFastCharger = isFastCharger;
        this.hardwareModel = hardwareModel;
        this.lastServiceDate = lastServiceDate;
    }

    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }

    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }

    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    public int getPointNumber() { return pointNumber; }
    public void setPointNumber(int pointNumber) { this.pointNumber = pointNumber; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public double getPowerRatingKw() { return powerRatingKw; }
    public void setPowerRatingKw(double powerRatingKw) { this.powerRatingKw = powerRatingKw; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isFastCharger() { return isFastCharger; }
    public void setFastCharger(boolean fastCharger) { isFastCharger = fastCharger; }

    public String getHardwareModel() { return hardwareModel; }
    public void setHardwareModel(String hardwareModel) { this.hardwareModel = hardwareModel; }

    public Date getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(Date lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public Integer getActiveSessionId() { return activeSessionId; }
    public void setActiveSessionId(Integer activeSessionId) { this.activeSessionId = activeSessionId; }

    public String getActiveSessionCode() { return activeSessionCode; }
    public void setActiveSessionCode(String activeSessionCode) { this.activeSessionCode = activeSessionCode; }

    public String getActiveVehicleNumber() { return activeVehicleNumber; }
    public void setActiveVehicleNumber(String activeVehicleNumber) { this.activeVehicleNumber = activeVehicleNumber; }

    public String getActiveUserName() { return activeUserName; }
    public void setActiveUserName(String activeUserName) { this.activeUserName = activeUserName; }

    public Timestamp getActiveSessionStartTime() { return activeSessionStartTime; }
    public void setActiveSessionStartTime(Timestamp activeSessionStartTime) { this.activeSessionStartTime = activeSessionStartTime; }

    public double getActiveEnergyKwh() { return activeEnergyKwh; }
    public void setActiveEnergyKwh(double activeEnergyKwh) { this.activeEnergyKwh = activeEnergyKwh; }

    public double getActiveCost() { return activeCost; }
    public void setActiveCost(double activeCost) { this.activeCost = activeCost; }

    @Override
    public String toString() {
        return pointCode + " [" + connectorType + " " + powerRatingKw + "kW] (" + status + ")";
    }
}
