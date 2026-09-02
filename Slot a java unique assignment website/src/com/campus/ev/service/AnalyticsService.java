package com.campus.ev.service;

import com.campus.ev.dao.ReportDAO;
import com.campus.ev.model.CampusEnergySummaryDTO;
import com.campus.ev.model.OperationalSummaryDTO;
import com.campus.ev.model.StationUtilizationDTO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    private final ReportDAO reportDAO = new ReportDAO();

    public OperationalSummaryDTO getOperationalSummary() throws SQLException {
        return reportDAO.getOperationalSummary();
    }

    /**
     * Executes Stored Procedure `sp_get_station_utilization` via CallableStatement.
     */
    public List<StationUtilizationDTO> getStationUtilizationReport(Integer stationId) throws SQLException {
        return reportDAO.getStationUtilizationByProcedure(stationId);
    }

    public List<Map<String, Object>> getEnergyConsumptionReport(Date startDate, Date endDate) throws SQLException {
        return reportDAO.getEnergyConsumptionReport(startDate, endDate);
    }

    public List<Map<String, Object>> getRevenueReport(Date startDate, Date endDate) throws SQLException {
        return reportDAO.getRevenueReport(startDate, endDate);
    }

    public List<Map<String, Object>> getVehicleUsageReport() throws SQLException {
        return reportDAO.getVehicleUsageReport();
    }

    public CampusEnergySummaryDTO getCampusEnergySummary(int daysBack) throws SQLException {
        return reportDAO.getCampusEnergySummaryProcedure(daysBack);
    }

    public Map<String, Double> calculateSessionBillingProcedure(int sessionId) throws SQLException {
        return reportDAO.calculateSessionBillingProcedure(sessionId);
    }
}
