package com.campus.ev.model;

/**
 * Data Transfer Object for aggregated campus energy analytics.
 */
public class CampusEnergySummaryDTO {
    private int totalSessions;
    private double totalEnergyKwh;
    private double totalRevenue;
    private int uniqueActiveUsers;
    private int uniqueVehicles;
    private double avgDurationMins;
    private double carbonOffsetKgCo2;

    public CampusEnergySummaryDTO() {}

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public double getTotalEnergyKwh() { return totalEnergyKwh; }
    public void setTotalEnergyKwh(double totalEnergyKwh) { this.totalEnergyKwh = totalEnergyKwh; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getUniqueActiveUsers() { return uniqueActiveUsers; }
    public void setUniqueActiveUsers(int uniqueActiveUsers) { this.uniqueActiveUsers = uniqueActiveUsers; }

    public int getUniqueVehicles() { return uniqueVehicles; }
    public void setUniqueVehicles(int uniqueVehicles) { this.uniqueVehicles = uniqueVehicles; }

    public double getAvgDurationMins() { return avgDurationMins; }
    public void setAvgDurationMins(double avgDurationMins) { this.avgDurationMins = avgDurationMins; }

    public double getCarbonOffsetKgCo2() { return carbonOffsetKgCo2; }
    public void setCarbonOffsetKgCo2(double carbonOffsetKgCo2) { this.carbonOffsetKgCo2 = carbonOffsetKgCo2; }
}
