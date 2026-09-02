package com.campus.ev.model;

import java.sql.Timestamp;

public class ChargingSession {
    private int sessionId;
    private String sessionCode;
    private Integer reservationId;
    private int pointId;
    private int vehicleId;
    private int userId;
    private int tariffId;
    private Timestamp startTime;
    private Timestamp endTime;
    private int durationMinutes;
    private int initialSocPercent;
    private int finalSocPercent;
    private double totalEnergyKwh;
    private double peakPowerKw;
    private double energyCost;
    private double parkingFee;
    private double totalAmount;
    private String status; // CHARGING, PAUSED, COMPLETED, STOPPED_USER, FAULT_STOPPED
    private Timestamp createdAt;

    // Joined helper fields
    private String pointCode;
    private String stationName;
    private String campusZone;
    private String vehicleNumber;
    private String vehicleModel;
    private String userName;
    private String userCode;
    private String tariffName;
    private double tariffRate;

    public ChargingSession() {}

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }

    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTariffId() { return tariffId; }
    public void setTariffId(int tariffId) { this.tariffId = tariffId; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getInitialSocPercent() { return initialSocPercent; }
    public void setInitialSocPercent(int initialSocPercent) { this.initialSocPercent = initialSocPercent; }

    public int getFinalSocPercent() { return finalSocPercent; }
    public void setFinalSocPercent(int finalSocPercent) { this.finalSocPercent = finalSocPercent; }

    public double getTotalEnergyKwh() { return totalEnergyKwh; }
    public void setTotalEnergyKwh(double totalEnergyKwh) { this.totalEnergyKwh = totalEnergyKwh; }

    public double getPeakPowerKw() { return peakPowerKw; }
    public void setPeakPowerKw(double peakPowerKw) { this.peakPowerKw = peakPowerKw; }

    public double getEnergyCost() { return energyCost; }
    public void setEnergyCost(double energyCost) { this.energyCost = energyCost; }

    public double getParkingFee() { return parkingFee; }
    public void setParkingFee(double parkingFee) { this.parkingFee = parkingFee; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPointCode() { return pointCode; }
    public void setPointCode(String pointCode) { this.pointCode = pointCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getTariffName() { return tariffName; }
    public void setTariffName(String tariffName) { this.tariffName = tariffName; }

    public double getTariffRate() { return tariffRate; }
    public void setTariffRate(double tariffRate) { this.tariffRate = tariffRate; }

    @Override
    public String toString() {
        return sessionCode + " (" + pointCode + " - " + vehicleNumber + " [" + status + "])";
    }
}
