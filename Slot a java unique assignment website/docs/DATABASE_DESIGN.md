# Relational Database Design & Schema Specification

## 1. Relational Schema Architecture
The database `campus_ev_db` is normalized to 3NF (Third Normal Form) to eliminate data redundancy while maintaining relational integrity with foreign keys, constraints, and indexes.

```
+------------------+       +-------------------+       +--------------------+
|   campus_users   | 1---* |    ev_vehicles    | 1---* |  charging_sessions |
+------------------+       +-------------------+       +--------------------+
        |                            |                           |
        | 1                          | 1                         | 1
        *                            *                           *
+------------------+       +-------------------+       +--------------------+
|   reservations   | *---1 |  charging_points  | *---1 | charging_stations  |
+------------------+       +-------------------+       +--------------------+
                                                         |
+------------------+       +-------------------+         |
| billing_payments | *---1 |   energy_usage    |         |
+------------------+       +-------------------+         |
        |                            |                   |
        +----------------------------+-------------------+
```

---

## 2. Table Data Dictionary

### 2.1 `campus_users`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `user_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `user_code` | VARCHAR(20) | NOT NULL, UNIQUE | Campus ID (e.g. USR-FAC-101) |
| `full_name` | VARCHAR(100) | NOT NULL | User's full legal name |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | University email address |
| `phone` | VARCHAR(20) | NOT NULL | Mobile phone number |
| `department` | VARCHAR(80) | NOT NULL | Department / Faculty / Division |
| `campus_role`| ENUM | NOT NULL | `STUDENT`, `FACULTY`, `CAMPUS_FLEET`, `FACILITY_STAFF`, `VISITOR` |
| `status` | ENUM | NOT NULL | `ACTIVE`, `SUSPENDED`, `INACTIVE` |
| `rfid_card_uid`| VARCHAR(50)| UNIQUE | RFID smart card authentication identifier |

### 2.2 `ev_vehicles`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `vehicle_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique vehicle identifier |
| `vehicle_number` | VARCHAR(25) | NOT NULL, UNIQUE | License plate number (e.g. KA-01-EV-2024) |
| `user_id` | INT | NOT NULL, FK -> `campus_users` | Registered vehicle owner |
| `vehicle_type` | ENUM | NOT NULL | `2-WHEELER_SCOOTER`, `4-WHEELER_SEDAN`, `4-WHEELER_SUV`, `CAMPUS_BUS_SHUTTLE`, `FACILITY_UTILITY_VAN` |
| `brand` | VARCHAR(50) | NOT NULL | Manufacturer (e.g. Tata, Ather, Olectra) |
| `model` | VARCHAR(50) | NOT NULL | Model designation (e.g. Nexon EV, 450X) |
| `battery_capacity_kwh`| DECIMAL(6,2)| NOT NULL | Battery pack gross capacity (kWh) |
| `max_charge_rate_kw` | DECIMAL(5,2)| NOT NULL | Maximum supported DC/AC charging kW |
| `connector_type` | ENUM | NOT NULL | `TYPE_2_AC`, `CCS_2_DC`, `CHADEMO`, `GB_T_DC`, `BHARAT_AC_001` |
| `status` | ENUM | NOT NULL | `ACTIVE`, `INACTIVE` |

### 2.3 `charging_stations`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `station_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Station Hub identifier |
| `station_code` | VARCHAR(20) | NOT NULL, UNIQUE | Campus Hub Code (e.g. STN-MAIN-01) |
| `station_name` | VARCHAR(100) | NOT NULL | Human-readable hub name |
| `campus_zone` | VARCHAR(80) | NOT NULL | Zone (e.g. Central Campus, North Quad) |
| `total_points` | INT | NOT NULL DEFAULT 1 | Total charging points at hub |
| `max_grid_capacity_kw` | DECIMAL(7,2)| NOT NULL | Maximum transformer substation power limit |
| `operating_status` | ENUM | NOT NULL | `OPERATIONAL`, `DEGRADED`, `OFFLINE`, `MAINTENANCE` |
| `solar_powered` | BOOLEAN | NOT NULL DEFAULT FALSE | True if equipped with solar canopy |

### 2.4 `charging_points`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `point_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Dispenser node identifier |
| `point_code` | VARCHAR(20) | NOT NULL, UNIQUE | Node Code (e.g. CP-01) |
| `station_id` | INT | NOT NULL, FK -> `charging_stations` | Parent station hub |
| `point_number` | INT | NOT NULL | Dispenser slot number |
| `connector_type`| ENUM | NOT NULL | Supported charging connector standard |
| `power_rating_kw` | DECIMAL(6,2)| NOT NULL | Power delivery capability (kW) |
| `status` | ENUM | NOT NULL | `AVAILABLE`, `OCCUPIED`, `RESERVED`, `MAINTENANCE` |
| `is_fast_charger`| BOOLEAN | NOT NULL | True for high power DC fast chargers |

### 2.5 `tariffs`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `tariff_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Tariff identifier |
| `tariff_code` | VARCHAR(25) | NOT NULL, UNIQUE | Identifier code (e.g. TAR-STD-2026) |
| `tariff_name` | VARCHAR(100) | NOT NULL | Scheme name |
| `rate_per_kwh`| DECIMAL(6,2)| NOT NULL | Per-kWh billing rate (₹) |
| `base_parking_fee_per_hour`| DECIMAL(6,2)| NOT NULL | Overstay idle parking fee (₹/hr) |
| `peak_hour_multiplier`| DECIMAL(4,2)| NOT NULL | Multiplier during high-demand windows |
| `effective_from` | DATE | NOT NULL | Scheme activation date |
| `status` | ENUM | NOT NULL | `ACTIVE`, `EXPIRED`, `PENDING` |

### 2.6 `reservations`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `reservation_id`| INT | PRIMARY KEY, AUTO_INCREMENT | Reservation record identifier |
| `reservation_code`| VARCHAR(30)| NOT NULL, UNIQUE | Booking confirmation code |
| `user_id` | INT | NOT NULL, FK -> `campus_users` | Reserving driver |
| `vehicle_id` | INT | NOT NULL, FK -> `ev_vehicles` | Scheduled vehicle |
| `point_id` | INT | NOT NULL, FK -> `charging_points` | Scheduled node |
| `station_id` | INT | NOT NULL, FK -> `charging_stations` | Scheduled hub |
| `start_time` | DATETIME | NOT NULL | Start of booked interval |
| `end_time` | DATETIME | NOT NULL | End of booked interval |
| `estimated_kwh` | DECIMAL(6,2)| NOT NULL | Estimated energy quota |
| `status` | ENUM | NOT NULL | `CONFIRMED`, `CHECKED_IN`, `COMPLETED`, `CANCELLED`, `EXPIRED` |

### 2.7 `charging_sessions`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `session_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Active/completed session ID |
| `session_code` | VARCHAR(30) | NOT NULL, UNIQUE | Session code (e.g. SES-20260901-1001) |
| `reservation_id`| INT NULL | FK -> `reservations` | Linked reservation (optional) |
| `point_id` | INT | NOT NULL, FK -> `charging_points` | Dispenser node |
| `vehicle_id` | INT | NOT NULL, FK -> `ev_vehicles` | Charging vehicle |
| `user_id` | INT | NOT NULL, FK -> `campus_users` | Driver / User |
| `tariff_id` | INT | NOT NULL, FK -> `tariffs` | Applied billing tariff |
| `start_time` | DATETIME | NOT NULL | Charging session commencement |
| `end_time` | DATETIME NULL | | Charging session termination |
| `duration_minutes`| INT | DEFAULT 0 | Total connected minutes |
| `total_energy_kwh`| DECIMAL(7,3)| NOT NULL DEFAULT 0.000 | Cumulative kWh delivered |
| `energy_cost` | DECIMAL(8,2)| NOT NULL DEFAULT 0.00 | Computed energy charges (₹) |
| `parking_fee` | DECIMAL(8,2)| NOT NULL DEFAULT 0.00 | Computed overstay fees (₹) |
| `total_amount` | DECIMAL(8,2)| NOT NULL DEFAULT 0.00 | Final gross billing total (₹) |
| `status` | ENUM | NOT NULL | `CHARGING`, `PAUSED`, `COMPLETED`, `STOPPED_USER`, `FAULT_STOPPED` |

### 2.8 `billing_payments`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `payment_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Payment transaction identifier |
| `invoice_number`| VARCHAR(35) | NOT NULL, UNIQUE | Tax invoice number (e.g. INV-20260901-001) |
| `session_id` | INT | NOT NULL, UNIQUE, FK -> `charging_sessions` | Associated session |
| `user_id` | INT | NOT NULL, FK -> `campus_users` | Billed user |
| `amount` | DECIMAL(8,2)| NOT NULL | Settled amount (₹) |
| `payment_method`| ENUM | NOT NULL | `CAMPUS_WALLET`, `UPI_QR`, `SMART_ID_CARD`, `STUDENT_PORTAL`, `WAIVED_FLEET` |
| `transaction_ref`| VARCHAR(80)| NOT NULL | Gateway / Wallet transaction ref |
| `payment_status`| ENUM | NOT NULL | `PAID`, `PENDING`, `FAILED`, `REFUNDED` |
| `payment_time` | DATETIME | NOT NULL | Timestamp of settlement |

---

## 3. MySQL Stored Procedures (CallableStatement)

### `sp_get_station_utilization(IN p_station_id INT)`
Computes total points, available count, occupied count, reserved count, maintenance count, lifetime completed charging sessions, delivered energy kWh, and percentage utilization for a specific station or aggregated across all campus hubs.

### `sp_calculate_session_billing(IN p_session_id INT, OUT p_energy_kwh, OUT p_rate_per_kwh, OUT p_energy_cost, OUT p_parking_fee, OUT p_total_amount)`
Calculates final session cost based on tariff multipliers and overstay parking fees using output parameters.

### `sp_generate_campus_energy_summary(IN p_days_back INT)`
Aggregates campus total energy delivery, total revenue, unique active users, unique vehicles, and calculated carbon offset (kg CO2 avoided vs petrol @ 0.82 kg/kWh).
