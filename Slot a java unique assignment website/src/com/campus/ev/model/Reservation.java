package com.campus.ev.model;

import java.sql.Timestamp;

public class Reservation {
    private int reservationId;
    private String reservationCode;
    private int userId;
    private int vehicleId;
    private int pointId;
    private int stationId;
    private Timestamp startTime;
    private Timestamp endTime;
    private double estimatedKwh;
    private String status; // CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED, EXPIRED
    private Timestamp createdAt;

    // Joined helper fields
    private String userName;
    private String userCode;
    private String vehicleNumber;
    private String vehicleModel;
    private String pointCode;
    private String stationName;
    private String campusZone;

    public Reservation() {}

    public Reservation(int reservationId, String reservationCode, int userId, int vehicleId, 
                       int pointId, int stationId, Timestamp startTime, Timestamp endTime, 
                       double estimatedKwh, String status) {
        this.reservationId = reservationId;
        this.reservationCode = reservationCode;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.pointId = pointId;
        this.stationId = stationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedKwh = estimatedKwh;
        this.status = status;
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }

    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public double getEstimatedKwh() { return estimatedKwh; }
    public void setEstimatedKwh(double estimatedKwh) { this.estimatedKwh = estimatedKwh; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    @Override
    public String toString() {
        return reservationCode + " (" + pointCode + " - " + vehicleNumber + ")";
    }
}
