package com.campus.ev.model;

import java.sql.Timestamp;

public class EnergyUsage {
    private int usageId;
    private int sessionId;
    private Timestamp readingTimestamp;
    private double instantVoltageV;
    private double instantCurrentA;
    private double instantPowerKw;
    private double cumulativeKwh;
    private double batteryTempCelsius;

    public EnergyUsage() {}

    public EnergyUsage(int usageId, int sessionId, Timestamp readingTimestamp, 
                       double instantVoltageV, double instantCurrentA, double instantPowerKw, 
                       double cumulativeKwh, double batteryTempCelsius) {
        this.usageId = usageId;
        this.sessionId = sessionId;
        this.readingTimestamp = readingTimestamp;
        this.instantVoltageV = instantVoltageV;
        this.instantCurrentA = instantCurrentA;
        this.instantPowerKw = instantPowerKw;
        this.cumulativeKwh = cumulativeKwh;
        this.batteryTempCelsius = batteryTempCelsius;
    }

    public int getUsageId() { return usageId; }
    public void setUsageId(int usageId) { this.usageId = usageId; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public Timestamp getReadingTimestamp() { return readingTimestamp; }
    public void setReadingTimestamp(Timestamp readingTimestamp) { this.readingTimestamp = readingTimestamp; }

    public double getInstantVoltageV() { return instantVoltageV; }
    public void setInstantVoltageV(double instantVoltageV) { this.instantVoltageV = instantVoltageV; }

    public double getInstantCurrentA() { return instantCurrentA; }
    public void setInstantCurrentA(double instantCurrentA) { this.instantCurrentA = instantCurrentA; }

    public double getInstantPowerKw() { return instantPowerKw; }
    public void setInstantPowerKw(double instantPowerKw) { this.instantPowerKw = instantPowerKw; }

    public double getCumulativeKwh() { return cumulativeKwh; }
    public void setCumulativeKwh(double cumulativeKwh) { this.cumulativeKwh = cumulativeKwh; }

    public double getBatteryTempCelsius() { return batteryTempCelsius; }
    public void setBatteryTempCelsius(double batteryTempCelsius) { this.batteryTempCelsius = batteryTempCelsius; }
}
