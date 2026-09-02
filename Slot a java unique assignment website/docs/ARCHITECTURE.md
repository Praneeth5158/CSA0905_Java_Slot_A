# System Architecture & Technical Specifications

## 1. Overview
The **Smart Campus EV Charging Control Center** is a high-performance Java desktop application engineered for university facilities and energy management departments. The architecture adheres to a clean **3-Tier Layered Architecture** ensuring complete separation of concerns between presentation, business services, and data persistence.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION TIER (UI)                          │
│  MainFrame (CardLayout Workspace Switcher)                            │
│  ├── TopCommandBar (Live Telemetry & Status Tickers)                   │
│  ├── AppMenuBar (File, Operations, Management, Reports, Help)          │
│  ├── CommandCenterPanel (Control Room Overview & Real-time Logs)       │
│  ├── CampusGridPanel & CampusMapCanvas (Interactive Spatial Network)   │
│  ├── NodeInspectorPanel (Contextual Telemetry & Instant Actions)       │
│  ├── ReservationWorkspacePanel (24-hr Visual Timeline Matrix)          │
│  ├── LiveSessionCockpitPanel (Power Flow Telemetry & Meter Gauges)     │
│  ├── ManagementPanels (Vehicles, Users, Stations, Points, Tariffs)     │
│  └── ReportsAnalyticsPanel (Utilization, Energy, Revenue, Carbon SDG)  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        BUSINESS SERVICE TIER                           │
│  ├── ChargingService (Session simulation, atomic stopping transaction) │
│  ├── ReservationService (Strict time overlap & vehicle busy guard)     │
│  ├── AnalyticsService (Stored procedures & reporting aggregation)      │
│  └── InputValidator (Regex, boundary & sanitization guards)            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                       DATA ACCESS OBJECT (DAO)                         │
│  ├── Statement: Metadata, static operational summaries                 │
│  ├── PreparedStatement: Parameterized CRUD, search, and date filters   │
│  └── CallableStatement: MySQL Stored Procedures (IN/OUT parameters)    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                      PERSISTENCE TIER (MySQL 8.0+)                     │
│  campus_users, ev_vehicles, charging_stations, charging_points,        │
│  reservations, charging_sessions, energy_usage, billing_payments,      │
│  tariffs, activity_logs + Stored Procedures & Indexes                  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Front-End Design & Interaction Model
1. **Interactive Workspace (CardLayout)**: Replaces disconnected popups with a unified command center.
2. **Campus Map 2D Canvas (`CampusMapCanvas`)**: Custom Swing rendering with anti-aliasing, drawing dynamic hubs, pathways, status colors, and hover highlights.
3. **Contextual Inspector (`NodeInspectorPanel`)**: Automatically inspects any selected node and enables contextual actions (`[Reserve]`, `[Start Charge]`, `[Stop Charge]`, `[Toggle Maintenance]`).
4. **24-Hour Timeline Matrix (`TimeSlotGridComponent`)**: Visual slot grid allowing users to inspect hourly point availability and select slots with 1 click.
5. **Real-Time Live Cockpit (`LiveSessionCockpitPanel`)**: Dynamic circular power meter, elapsed timer ticker, running kWh counter, and live tariff billing accumulator.

---

## 3. JDBC Architecture & Statement Mechanisms
- **`java.sql.Statement`**: Used in `ReportDAO.getOperationalSummary()` for fast aggregations across all tables without input parameters.
- **`java.sql.PreparedStatement`**: Used across all DAO classes (`UserDAO`, `VehicleDAO`, `ReservationDAO`, `ChargingSessionDAO`, `ChargingPointDAO`, `TariffDAO`, `ActivityLogDAO`) preventing SQL injection and optimizing query execution plans.
- **`java.sql.CallableStatement`**: Invokes MySQL stored procedures:
  - `sp_get_station_utilization(IN stationId)`
  - `sp_calculate_session_billing(IN sessionId, OUT energy, OUT rate, OUT cost, OUT parking, OUT total)`
  - `sp_generate_campus_energy_summary(IN daysBack)`

---

## 4. Transaction Safety (ACID)
Mission-critical state transitions execute within explicit transactions (`setAutoCommit(false)`):
- **Stopping a Charging Session**:
  1. Updates `charging_sessions` table (end time, duration, energy delivered, cost, status = `COMPLETED`).
  2. Updates `charging_points` table (status = `AVAILABLE`).
  3. Inserts `billing_payments` table (invoice number, amount, payment method, transaction reference).
  4. Updates `reservations` table (status = `COMPLETED`).
  5. Inserts `activity_logs` entry.
  6. Atomic `conn.commit()` or `conn.rollback()` on exception.
