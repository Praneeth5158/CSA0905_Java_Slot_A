package com.campus.ev.model;

import java.sql.Date;

public class Tariff {
    private int tariffId;
    private String tariffCode;
    private String tariffName;
    private double ratePerKwh;
    private double baseParkingFeePerHour;
    private double peakHourMultiplier;
    private Date effectiveFrom;
    private String status; // ACTIVE, EXPIRED, PENDING
    private String description;

    public Tariff() {}

    public Tariff(int tariffId, String tariffCode, String tariffName, double ratePerKwh, 
                  double baseParkingFeePerHour, double peakHourMultiplier, Date effectiveFrom, 
                  String status, String description) {
        this.tariffId = tariffId;
        this.tariffCode = tariffCode;
        this.tariffName = tariffName;
        this.ratePerKwh = ratePerKwh;
        this.baseParkingFeePerHour = baseParkingFeePerHour;
        this.peakHourMultiplier = peakHourMultiplier;
        this.effectiveFrom = effectiveFrom;
        this.status = status;
        this.description = description;
    }

    public int getTariffId() { return tariffId; }
    public void setTariffId(int tariffId) { this.tariffId = tariffId; }

    public String getTariffCode() { return tariffCode; }
    public void setTariffCode(String tariffCode) { this.tariffCode = tariffCode; }

    public String getTariffName() { return tariffName; }
    public void setTariffName(String tariffName) { this.tariffName = tariffName; }

    public double getRatePerKwh() { return ratePerKwh; }
    public void setRatePerKwh(double ratePerKwh) { this.ratePerKwh = ratePerKwh; }

    public double getBaseParkingFeePerHour() { return baseParkingFeePerHour; }
    public void setBaseParkingFeePerHour(double baseParkingFeePerHour) { this.baseParkingFeePerHour = baseParkingFeePerHour; }

    public double getPeakHourMultiplier() { return peakHourMultiplier; }
    public void setPeakHourMultiplier(double peakHourMultiplier) { this.peakHourMultiplier = peakHourMultiplier; }

    public Date getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(Date effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return tariffName + " (₹" + String.format("%.2f", ratePerKwh) + "/kWh)";
    }
}
