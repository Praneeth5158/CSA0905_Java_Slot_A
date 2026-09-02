# ⚡ Smart Campus EV Charging Management System
### *Autonomous Energy Grid Control Center for Smart University Campuses*

[![Java](https://img.shields.io/badge/Java-8%20%2F%2017%20%2F%2021%20%2F%2025-blue.svg)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing%20%2F%20AWT-green.svg)]()
[![JDBC](https://img.shields.io/badge/Database-JDBC%20%2B%20MySQL%208.0-orange.svg)]()
[![SDG](https://img.shields.io/badge/SDGs-Goal%207%20%7C%209%20%7C%2011-emerald.svg)](https://sdgs.un.org/goals)

---

## 🌟 Executive Summary
The **Smart Campus EV Charging Management System** is a professional desktop application engineered for university facilities management, sustainability offices, and campus fleet operations. 

Moving away from standard CRUD forms, this system is designed as an interactive **"Smart Campus Energy Grid & Control Center"**, featuring spatial campus charging node maps, live telemetry flow, interactive 24-hour timeline scheduling, and ACID transaction-safe billing.

---

## 🚀 Key Innovations & Distinctive Features

### 1. 🗺 Interactive Campus Charging Grid & 2D Spatial Canvas
- Custom Swing graphics canvas rendering university hubs: **Main Academic Block, Central Library, Hostel Zone, Research & Innovation Park, Sports Complex, and Multi-Level Parking**.
- Clickable charging nodes (`CP-01` to `CP-18`) with dynamic color indicators:
  - 🟢 **AVAILABLE** (Ready for charging)
  - 🔵 **OCCUPIED / CHARGING** (Live power flow)
  - 🟡 **RESERVED** (Booked by campus user)
  - 🔴 **MAINTENANCE** (Service required)
- Interactive node filtering (`ALL`, `AVAILABLE`, `OCCUPIED`, `RESERVED`, `MAINTENANCE`).

### 2. 🔍 Contextual Node Inspector
- Selecting any node opens a dynamic telemetry side-drawer with live technical specifications, active vehicle details, running energy kWh meter, dynamic billing cost, and 1-click contextual actions (`[RESERVE]`, `[START SESSION]`, `[STOP CHARGING]`, `[TOGGLE MAINTENANCE]`).

### 3. 📅 24-Hour Interactive Reservation Workspace
- Visual slot matrix showing 00:00 to 23:59 hourly availability blocks.
- Real-time conflict validation preventing overlapping bookings, vehicle double-booking, or reservations on maintenance nodes.

### 4. 🔋 Live Charging Cockpit
- Dedicated active session telemetry with circular power flow meter, live duration ticker (`HH:mm:ss`), cumulative kWh accumulation, running tariff billing, and transaction-safe session completion.

### 5. 📊 Analytics & Reporting Studio (MySQL Stored Procedures)
- **Station Utilization Analysis**: Executed via MySQL Stored Procedure `sp_get_station_utilization` using `java.sql.CallableStatement`.
- **Daily Energy Timeline**: Parameterized date range queries using `java.sql.PreparedStatement`.
- **Revenue & Payment Settlements**: Categorized by payment method (Campus Wallet, UPI QR, Smart ID Card, Fleet Waived).
- **Fleet Sustainability & Carbon Offset**: Tracks CO2 emissions avoided (approx. 0.82 kg CO2 / kWh).
- **Exporting**: 1-click export to `.csv` or formatted text summary to clipboard.

---

## 🛠 Technology Stack & Coursework Compliance

| Technology Layer | Coursework Requirement | Implementation in System |
|---|---|---|
| **Core Platform** | Java SE | Pure Java (Java 8 / 17 / 21 / 25 compatible) |
| **GUI Framework** | AWT & Swing | `JFrame`, `JPanel`, `CardLayout`, `BorderLayout`, `GridLayout`, `GridBagLayout`, `FlowLayout`, `JTable`, `JMenuBar`, `JDialog` |
| **Database** | MySQL 8.0+ | 10 Normalized 3NF Tables with Foreign Keys, Constraints & Indexes |
| **JDBC API** | Statement | `ReportDAO.getOperationalSummary()` for fast campus counters |
| **JDBC API** | PreparedStatement | Parameterized CRUD, search filters, and conflict detection across all DAOs |
| **JDBC API** | CallableStatement | MySQL Stored Procedure execution (`sp_get_station_utilization`, `sp_calculate_session_billing`) |
| **Data Integrity**| ACID Transactions | Atomic commit & rollback on charging session finalization |

---

## 📂 Project Directory Structure

```
Slot a java unique assignment website/
├── lib/
│   └── mysql-connector-j-8.3.0.jar      # Official MySQL JDBC Driver
├── database/
│   ├── schema.sql                       # Complete 3NF relational schema DDL
│   ├── sample_data.sql                  # Realistic smart campus seed dataset
│   ├── procedures.sql                   # MySQL Stored Procedures
│   └── queries.sql                      # Reference operational queries
├── src/
│   └── com/campus/ev/
│       ├── Main.java                    # Application Entry Point
│       ├── config/
│       │   └── DatabaseConfig.java      # Configuration manager
│       ├── db/
│       │   ├── ConnectionManager.java   # Singleton JDBC connection pool
│       │   └── DatabaseInitializer.java # Automated schema & seed loader
│       ├── model/                       # Domain Models & DTOs
│       ├── dao/                         # DAO Layer (Statement, PreparedStatement, CallableStatement)
│       ├── service/                     # Business Services & Telemetry Simulator
│       ├── util/                        # Custom Theme, UI Helpers, Custom Components
│       ├── validation/                  # Regex input validators
│       ├── ui/                          # Swing UI Workspaces & Custom Canvases
│       └── test/                        # 20-Point Automated Test Suite
├── docs/
│   ├── ARCHITECTURE.md                  # Detailed Layered Architecture
│   ├── DATABASE_DESIGN.md               # Data Dictionary & ER Relationships
│   ├── PSEUDOCODE.md                    # Core Algorithm Pseudocode
│   ├── USER_MANUAL.md                   # Visual User Guide
│   ├── TESTING_CHECKLIST.md             # 20-Point Test Rubric Verification
│   └── TEAM_CONTRIBUTIONS.md            # Team Member Responsibilities
├── db.properties                        # Persistent DB connection settings
├── build.bat                            # Windows Build Script
├── run.bat                              # Windows Launch Script
└── README.md                            # Main Documentation
```

---

## ⚡ How to Build & Run

### Method 1: Using Automation Scripts (Windows)
1. **Build the project**:
   ```cmd
   build.bat
   ```
2. **Launch the application**:
   ```cmd
   run.bat
   ```

### Method 2: Manual Terminal Commands
1. **Compile**:
   ```powershell
   javac --release 8 -cp "lib/*;src" -d bin (Get-ChildItem -Path src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
   ```
2. **Run**:
   ```powershell
   java -cp "bin;lib/*" com.campus.ev.Main
   ```

---

## 🔌 Database Setup & Instant Evaluation
1. Start your local MySQL Server 8.0+.
2. Launch the desktop app.
3. Click `⚙ DB` on the top bar (or `Help -> Database Configuration...`).
4. Enter your MySQL `root` password.
5. Click `⚡ Auto-Init Database` — this will automatically create the database `campus_ev_db`, create all tables, populate realistic campus sample data, and install stored procedures!

---

## 🌍 UN Sustainable Development Goals (SDG Alignment)
- **SDG 7 (Affordable and Clean Energy)**: Subsidized green solar off-peak charging tariffs (10:00 - 15:00) leveraging campus rooftop solar arrays.
- **SDG 9 (Industry, Innovation, and Infrastructure)**: High-speed DC Fast charging telemetry and dynamic power grid capacity balancing.
- **SDG 11 (Sustainable Cities and Communities)**: University campus zero-emission EV transportation ecosystem with measurable metric of kg CO2 emissions avoided.
