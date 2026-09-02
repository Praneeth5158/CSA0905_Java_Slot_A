# Team Member Contribution Breakdown

This project was developed collaboratively following standard software engineering practices and modular division of responsibilities.

---

### Member 1: GUI Architecture & Swing Custom Design System
- **Name**: [Insert Team Member 1 Name]
- **Roll Number / Student ID**: [Insert Student ID]
- **Email**: [Insert University Email]
- **Key Contributions**:
  - Engineered the dark control room visual theme (`UITheme.java`, `UIHelper.java`, `CustomComponents.java`).
  - Designed the **Interactive Campus Map 2D Canvas** (`CampusMapCanvas.java`) with anti-aliased spatial layout and clickable node chips.
  - Implemented the **Contextual Node Inspector** (`NodeInspectorPanel.java`) and the **24-Hour Timeline Grid** (`TimeSlotGridComponent.java`).
  - Built the `MainFrame` layout manager, `TopCommandBar`, and `AppMenuBar`.

---

### Member 2: Relational Database Architecture & JDBC DAO Layer
- **Name**: [Insert Team Member 2 Name]
- **Roll Number / Student ID**: [Insert Student ID]
- **Email**: [Insert University Email]
- **Key Contributions**:
  - Designed the 3NF relational database schema across 10 tables (`database/schema.sql`).
  - Authored comprehensive realistic sample data (`database/sample_data.sql`).
  - Implemented MySQL Stored Procedures (`database/procedures.sql`): `sp_get_station_utilization`, `sp_calculate_session_billing`, and `sp_generate_campus_energy_summary`.
  - Built the complete JDBC DAO layer demonstrating `Statement`, `PreparedStatement`, and `CallableStatement` with parameter binding.

---

### Member 3: Business Logic, Reservation Conflict Engine, Reports & Testing
- **Name**: [Insert Team Member 3 Name]
- **Roll Number / Student ID**: [Insert Student ID]
- **Email**: [Insert University Email]
- **Key Contributions**:
  - Implemented the strict reservation conflict detection algorithm (`ReservationService.java`).
  - Built the real-time charging telemetry simulator and ACID transaction-safe session finalization workflow (`ChargingService.java`).
  - Developed the 4-tabbed **Analytics & Reporting Studio** (`ReportsAnalyticsPanel.java`) with CSV export and carbon offset calculation.
  - Created the 20-point verification test suite (`SystemTestRunner.java`), build automation scripts, and technical documentation.
