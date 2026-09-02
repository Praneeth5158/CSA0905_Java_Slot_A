package com.campus.ev.model;

/**
 * Data Transfer Object for Station Utilization analysis returned by MySQL Stored Procedure.
 */
public class StationUtilizationDTO {
    private int stationId;
    private String stationCode;
    private String stationName;
    private String campusZone;
    private int totalPoints;
    private int occupiedPoints;
    private int availablePoints;
    private int reservedPoints;
    private int maintenancePoints;
    private int totalLifetimeSessions;
    private double totalEnergyDeliveredKwh;
    private double currentUtilizationPercent;

    public StationUtilizationDTO() {}

    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getOccupiedPoints() { return occupiedPoints; }
    public void setOccupiedPoints(int occupiedPoints) { this.occupiedPoints = occupiedPoints; }

    public int getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }

    public int getReservedPoints() { return reservedPoints; }
    public void setReservedPoints(int reservedPoints) { this.reservedPoints = reservedPoints; }

    public int getMaintenancePoints() { return maintenancePoints; }
    public void setMaintenancePoints(int maintenancePoints) { this.maintenancePoints = maintenancePoints; }

    public int getTotalLifetimeSessions() { return totalLifetimeSessions; }
    public void setTotalLifetimeSessions(int totalLifetimeSessions) { this.totalLifetimeSessions = totalLifetimeSessions; }

    public double getTotalEnergyDeliveredKwh() { return totalEnergyDeliveredKwh; }
    public void setTotalEnergyDeliveredKwh(double totalEnergyDeliveredKwh) { this.totalEnergyDeliveredKwh = totalEnergyDeliveredKwh; }

    public double getCurrentUtilizationPercent() { return currentUtilizationPercent; }
    public void setCurrentUtilizationPercent(double currentUtilizationPercent) { this.currentUtilizationPercent = currentUtilizationPercent; }
}
