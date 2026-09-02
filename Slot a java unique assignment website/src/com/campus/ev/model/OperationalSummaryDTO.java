package com.campus.ev.model;

/**
 * Data Transfer Object for real-time top command bar and command center telemetry.
 */
public class OperationalSummaryDTO {
    private int availablePoints;
    private int occupiedPoints;
    private int reservedPoints;
    private int maintenancePoints;
    private int activeSessionsCount;
    private double todayEnergyKwh;
    private double todayRevenue;
    private int totalStations;
    private int totalRegisteredUsers;
    private int totalRegisteredVehicles;

    public OperationalSummaryDTO() {}

    public int getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }

    public int getOccupiedPoints() { return occupiedPoints; }
    public void setOccupiedPoints(int occupiedPoints) { this.occupiedPoints = occupiedPoints; }

    public int getReservedPoints() { return reservedPoints; }
    public void setReservedPoints(int reservedPoints) { this.reservedPoints = reservedPoints; }

    public int getMaintenancePoints() { return maintenancePoints; }
    public void setMaintenancePoints(int maintenancePoints) { this.maintenancePoints = maintenancePoints; }

    public int getActiveSessionsCount() { return activeSessionsCount; }
    public void setActiveSessionsCount(int activeSessionsCount) { this.activeSessionsCount = activeSessionsCount; }

    public double getTodayEnergyKwh() { return todayEnergyKwh; }
    public void setTodayEnergyKwh(double todayEnergyKwh) { this.todayEnergyKwh = todayEnergyKwh; }

    public double getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(double todayRevenue) { this.todayRevenue = todayRevenue; }

    public int getTotalStations() { return totalStations; }
    public void setTotalStations(int totalStations) { this.totalStations = totalStations; }

    public int getTotalRegisteredUsers() { return totalRegisteredUsers; }
    public void setTotalRegisteredUsers(int totalRegisteredUsers) { this.totalRegisteredUsers = totalRegisteredUsers; }

    public int getTotalRegisteredVehicles() { return totalRegisteredVehicles; }
    public void setTotalRegisteredVehicles(int totalRegisteredVehicles) { this.totalRegisteredVehicles = totalRegisteredVehicles; }

    public int getTotalPoints() {
        return availablePoints + occupiedPoints + reservedPoints + maintenancePoints;
    }
}
