package com.campus.ev.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChargingStation {
    private int stationId;
    private String stationCode;
    private String stationName;
    private String campusZone;
    private String locationDescription;
    private int totalPoints;
    private double maxGridCapacityKw;
    private String operatingStatus; // OPERATIONAL, DEGRADED, OFFLINE, MAINTENANCE
    private boolean solarPowered;
    private double latitude;
    private double longitude;
    private Timestamp createdAt;

    private List<ChargingPoint> points = new ArrayList<>();

    public ChargingStation() {}

    public ChargingStation(int stationId, String stationCode, String stationName, String campusZone, 
                           String locationDescription, int totalPoints, double maxGridCapacityKw, 
                           String operatingStatus, boolean solarPowered, double latitude, double longitude) {
        this.stationId = stationId;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.campusZone = campusZone;
        this.locationDescription = locationDescription;
        this.totalPoints = totalPoints;
        this.maxGridCapacityKw = maxGridCapacityKw;
        this.operatingStatus = operatingStatus;
        this.solarPowered = solarPowered;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public double getMaxGridCapacityKw() { return maxGridCapacityKw; }
    public void setMaxGridCapacityKw(double maxGridCapacityKw) { this.maxGridCapacityKw = maxGridCapacityKw; }

    public String getOperatingStatus() { return operatingStatus; }
    public void setOperatingStatus(String operatingStatus) { this.operatingStatus = operatingStatus; }

    public boolean isSolarPowered() { return solarPowered; }
    public void setSolarPowered(boolean solarPowered) { this.solarPowered = solarPowered; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<ChargingPoint> getPoints() { return points; }
    public void setPoints(List<ChargingPoint> points) { this.points = points; }

    @Override
    public String toString() {
        return stationName + " (" + stationCode + " - " + campusZone + ")";
    }
}
