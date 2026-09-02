package com.campus.ev.service;

import com.campus.ev.dao.*;
import com.campus.ev.model.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class ChargingService {

    private final ChargingSessionDAO sessionDAO = new ChargingSessionDAO();
    private final ChargingPointDAO pointDAO = new ChargingPointDAO();
    private final TariffDAO tariffDAO = new TariffDAO();
    private final EnergyUsageDAO energyUsageDAO = new EnergyUsageDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    // Background telemetry simulator ticker
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final List<Runnable> telemetryListeners = new CopyOnWriteArrayList<>();

    static {
        // Run background meter simulation every 3 seconds for active sessions
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ChargingSessionDAO dao = new ChargingSessionDAO();
                EnergyUsageDAO euDao = new EnergyUsageDAO();
                List<ChargingSession> activeSessions = dao.getActiveSessions();

                for (ChargingSession s : activeSessions) {
                    // Simulate energy increment: ~ 0.05 to 0.15 kWh per 3 sec depending on power rating
                    double incrementKwh = 0.08 + (Math.random() * 0.04);
                    double newKwh = s.getTotalEnergyKwh() + incrementKwh;
                    double rate = s.getTariffRate() > 0 ? s.getTariffRate() : 7.50;
                    double energyCost = Math.round(newKwh * rate * 100.0) / 100.0;
                    double totalAmount = energyCost;

                    dao.updateLiveSessionProgress(s.getSessionId(), newKwh, energyCost, totalAmount);

                    // Insert sample telemetry
                    EnergyUsage eu = new EnergyUsage();
                    eu.setSessionId(s.getSessionId());
                    eu.setReadingTimestamp(new Timestamp(System.currentTimeMillis()));
                    eu.setInstantVoltageV(398.0 + Math.random() * 6.0);
                    eu.setInstantCurrentA(110.0 + Math.random() * 15.0);
                    eu.setInstantPowerKw(45.0 + Math.random() * 5.0);
                    eu.setCumulativeKwh(newKwh);
                    eu.setBatteryTempCelsius(32.0 + Math.random() * 4.0);
                    euDao.insertReading(eu);
                }

                // Notify UI listeners to refresh telemetry displays
                for (Runnable r : telemetryListeners) {
                    try { r.run(); } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {
                // Ignore background simulation DB glitches if app is restarting
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    public static void addTelemetryListener(Runnable listener) {
        telemetryListeners.add(listener);
    }

    public static void removeTelemetryListener(Runnable listener) {
        telemetryListeners.remove(listener);
    }

    public List<ChargingSession> getAllSessions() throws SQLException {
        return sessionDAO.getAllSessions();
    }

    public List<ChargingSession> getActiveSessions() throws SQLException {
        return sessionDAO.getActiveSessions();
    }

    public ChargingSession getActiveSessionForPoint(int pointId) throws SQLException {
        return sessionDAO.getActiveSessionByPoint(pointId);
    }

    public int startChargingSession(int pointId, int vehicleId, int userId, Integer reservationId, 
                                   int initialSoc, String paymentMethod) throws Exception {
        ChargingPoint cp = pointDAO.getPointById(pointId);
        if (cp == null) {
            throw new IllegalArgumentException("Charging point not found.");
        }
        if ("OCCUPIED".equalsIgnoreCase(cp.getStatus())) {
            throw new IllegalStateException("Charging point " + cp.getPointCode() + " is already OCCUPIED.");
        }
        if ("MAINTENANCE".equalsIgnoreCase(cp.getStatus())) {
            throw new IllegalStateException("Charging point " + cp.getPointCode() + " is in MAINTENANCE mode.");
        }

        // Retrieve active tariff
        Tariff tariff = tariffDAO.getActiveTariff();
        int tariffId = (tariff != null) ? tariff.getTariffId() : 1;

        String sessionCode = "SES-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        ChargingSession cs = new ChargingSession();
        cs.setSessionCode(sessionCode);
        cs.setReservationId(reservationId);
        cs.setPointId(pointId);
        cs.setVehicleId(vehicleId);
        cs.setUserId(userId);
        cs.setTariffId(tariffId);
        cs.setStartTime(new Timestamp(System.currentTimeMillis()));
        cs.setInitialSocPercent(initialSoc > 0 ? initialSoc : 20);
        cs.setFinalSocPercent(initialSoc > 0 ? initialSoc : 20);
        cs.setTotalEnergyKwh(0.000);
        cs.setPeakPowerKw(cp.getPowerRatingKw());
        cs.setEnergyCost(0.00);
        cs.setParkingFee(0.00);
        cs.setTotalAmount(0.00);
        cs.setStatus("CHARGING");

        return sessionDAO.startSessionTransaction(cs, cp.getPointCode(), "USR-" + userId, "VEH-" + vehicleId);
    }

    /**
     * Transactional Stop Charging Session:
     * Calculates bill, writes payment, updates station, writes logs.
     */
    public Payment stopChargingSession(int sessionId, String paymentMethod) throws Exception {
        ChargingSession session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Charging session #" + sessionId + " not found.");
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        session.setEndTime(endTime);

        long startMs = session.getStartTime().getTime();
        long endMs = endTime.getTime();
        int durationMinutes = Math.max(1, (int)((endMs - startMs) / (60 * 1000)));
        session.setDurationMinutes(durationMinutes);

        // Ensure minimum simulated energy if stopped immediately
        double energyKwh = session.getTotalEnergyKwh();
        if (energyKwh <= 0.001) {
            energyKwh = 1.5 + (Math.random() * 2.0); // minimum demo energy
            session.setTotalEnergyKwh(energyKwh);
        }

        // Final SOC estimation
        int finalSoc = Math.min(100, session.getInitialSocPercent() + (int)(energyKwh * 1.8));
        session.setFinalSocPercent(finalSoc);

        // Tariff calculation
        double rate = session.getTariffRate() > 0 ? session.getTariffRate() : 7.50;
        double energyCost = Math.round(energyKwh * rate * 100.0) / 100.0;
        session.setEnergyCost(energyCost);

        // Overstay parking fee if > 60 minutes
        double parkingFee = 0.00;
        if (durationMinutes > 60) {
            parkingFee = Math.round(((durationMinutes - 60) / 60.0) * 10.0 * 100.0) / 100.0;
        }
        session.setParkingFee(parkingFee);

        double totalAmount = energyCost + parkingFee;
        session.setTotalAmount(totalAmount);

        // Prepare Payment object
        String invoiceNum = "INV-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + (int)(Math.random() * 9000 + 1000);
        String txnRef = "TXN-" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setInvoiceNumber(invoiceNum);
        payment.setSessionId(sessionId);
        payment.setUserId(session.getUserId());
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "CAMPUS_WALLET");
        payment.setTransactionRef(txnRef);
        payment.setPaymentStatus("PAID");
        payment.setPaymentTime(endTime);

        boolean success = sessionDAO.stopSessionTransaction(session, payment, session.getPointCode(), session.getUserCode(), session.getVehicleNumber());
        if (!success) {
            throw new SQLException("Failed to finalize charging session transaction.");
        }

        return payment;
    }
}
