# User Manual: Smart Campus EV Charging Control Center

## 1. Getting Started
1. Run `build.bat` or `javac --release 8 -cp "lib/*;src" -d bin src/com/campus/ev/**/*.java`.
2. Launch the desktop application using `run.bat` or `java -cp "bin;lib/*" com.campus.ev.Main`.
3. If connecting to a local MySQL instance for the first time:
   - Click `⚙ DB` on the top bar or go to `Help -> Database Configuration...`.
   - Verify hostname, port, database name, and credentials.
   - Click `⚡ Auto-Init Database` to automatically build schema, load realistic campus data, and install stored procedures.

---

## 2. Navigating the Workspaces

### ⚡ Command Center
- **Telemetry Cards**: Live count of Available, Occupied, Reserved, Maintenance nodes, and today's gross energy/revenue.
- **Activity Feed**: Real-time event stream from the database.
- **Quick Action Hub**: 1-click launch to any operational workflow.

### 🗺 Interactive Campus Grid
- **Interactive Spatial Map**: Click any charging node (e.g. `CP-01` to `CP-18`) on the map canvas to open the **Contextual Node Inspector**.
- **Contextual Inspector**:
  - Live technical specifications & connector ratings.
  - Active vehicle & driver information.
  - Instant action buttons (`[Reserve]`, `[Start Charge]`, `[Stop Charge]`, `[Toggle Maintenance]`).
- **Filters**: Quickly filter visible nodes by `ALL`, `AVAILABLE`, `OCCUPIED`, `RESERVED`, or `MAINTENANCE`.

### 📅 Reservation Workspace
- **Booking Form**: Select vehicle, campus station, charging point, reservation date, and time slot.
- **24-Hour Timeline Matrix**: Visual slot blocks showing hourly availability. Click any block to auto-fill times.
- **Conflict Avoidance**: Prevents overlapping bookings, busy vehicle conflicts, or maintenance mode locks.

### 🔋 Live Charging Cockpit
- **Live Stream Selector**: Select any active charging session to inspect live power flow.
- **Meters & Gauges**: Circular power flow gauge, elapsed duration timer, live cumulative kWh delivered, and real-time billing cost accumulator.
- **Session Control**: Click `⏹ STOP CHARGING & SETTLE BILL` to compute final duration, tariff, overstay fees, and generate a transaction-safe invoice.

### 🚗 EV Fleet & Management
- **Vehicle Registry**: 4-quadrant workspace (Identity, Make & Model, Battery & Power Specs, Owner & Connector compatibility).
- **Users & Roles**: Manage Faculty, Student, Campus Fleet, Facility Staff, and Visitor accounts with RFID smart cards.
- **Stations & Charging Points**: Add new geographic hubs, solar canopy flags, and dispenser hardware configs.
- **Dynamic Tariffs**: Configure peak multipliers, green solar rates, and hourly overstay parking fees.

### 📊 Analytics & Reporting Studio
- **Tab 1: Station Utilization**: Executes MySQL Stored Procedure `sp_get_station_utilization` via `CallableStatement`.
- **Tab 2: Energy Consumption**: Parameterized date-filtered consumption timeline.
- **Tab 3: Revenue & Settlement**: Revenue breakdowns by payment channel.
- **Tab 4: Fleet Sustainability & Carbon Offset**: Tracks CO2 emissions avoided (SDG 7, 9, 11).
- **Export**: Export any report to `.csv` or copy a formatted text summary to clipboard.
