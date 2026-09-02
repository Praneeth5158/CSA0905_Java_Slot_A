package com.campus.ev.model;

import java.sql.Timestamp;

public class Vehicle {
    private int vehicleId;
    private String vehicleNumber;
    private int userId;
    private String vehicleType; // 2-WHEELER_SCOOTER, 4-WHEELER_SEDAN, 4-WHEELER_SUV, CAMPUS_BUS_SHUTTLE, FACILITY_UTILITY_VAN
    private String brand;
    private String model;
    private double batteryCapacityKwh;
    private double maxChargeRateKw;
    private String connectorType; // TYPE_2_AC, CCS_2_DC, CHADEMO, GB_T_DC, BHARAT_AC_001
    private String status;        // ACTIVE, INACTIVE
    private Timestamp registeredAt;

    // Joined helper fields
    private String ownerName;
    private String ownerCode;

    public Vehicle() {}

    public Vehicle(int vehicleId, String vehicleNumber, int userId, String vehicleType, 
                   String brand, String model, double batteryCapacityKwh, double maxChargeRateKw, 
                   String connectorType, String status) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.maxChargeRateKw = maxChargeRateKw;
        this.connectorType = connectorType;
        this.status = status;
    }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public void setBatteryCapacityKwh(double batteryCapacityKwh) { this.batteryCapacityKwh = batteryCapacityKwh; }

    public double getMaxChargeRateKw() { return maxChargeRateKw; }
    public void setMaxChargeRateKw(double maxChargeRateKw) { this.maxChargeRateKw = maxChargeRateKw; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Timestamp registeredAt) { this.registeredAt = registeredAt; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerCode() { return ownerCode; }
    public void setOwnerCode(String ownerCode) { this.ownerCode = ownerCode; }

    @Override
    public String toString() {
        return vehicleNumber + " (" + brand + " " + model + " [" + connectorType + "])";
    }
}
