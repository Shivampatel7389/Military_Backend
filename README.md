# Military Asset Management System (MAMS) - Backend API Service

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/Security-JWT_HMAC512-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

A production-grade, enterprise military logistics and inventory ledger backend engineered for defense armories, inter-base asset transfers, troop assignments, and munitions expenditure reconciliation.

---

## 🏛️ System Architecture & Highlights

- **Role-Based Access Control (RBAC):** Strict operational segregation for `ADMIN` (Supreme Commander), `BASE_COMMANDER` (Installation Commander), and `LOGISTICS_OFFICER` (Supply Corps).
- **Multi-Tenant Base Scoping:** Automated data boundary enforcement preventing base commanders from mutating or accessing armory assets outside their jurisdiction.
- **Mathematical Balance Ledger:**
  - `Opening Balance` = Sum of all historical inbound movements minus outbound transfers & expenditures prior to filter horizon.
  - `Net Movement` = Purchases + Transfers In − Transfers Out.
  - `Closing Balance` = Opening Balance + Net Movement − Expended.
  - `Available Stock` = Closing Physical Balance − Active Troop Assignments.
- **Immutable Security Audit Trail:** Every transaction, transfer, expenditure, and login is cryptographically recorded with operator username, role, target entity ID, state transition, and origin IP address.

---

## 🚀 Technology Stack

- **Framework:** Spring Boot 3.3.4 (Spring Web, Spring Security 6, Spring Data JPA)
- **Database:** MySQL 8.0 with InnoDB engine
- **Authentication:** Stateless JSON Web Token (JWT) with HMAC-SHA512
- **Validation:** Jakarta Bean Validation (Hibernate Validator)
- **Persistence:** Spring Data JPA with Hibernate ORM

---

## 📋 API Endpoints Specification

### 1. Authentication (`/api/auth`)
- `POST /api/auth/login` — Authenticate officer and issue JWT with base clearance metadata
- `GET /api/auth/me` — Inspect current active officer security principal

### 2. Strategic Dashboard Telemetry (`/api/dashboard`)
- `GET /api/dashboard` — Live balance telemetry (Opening, Net Movement, Assigned, Expended, Closing, Category breakdown)
- `GET /api/dashboard/net-movement-details` — Line-item audit drilldown for purchases, transfers in, and transfers out

### 3. Inbound Procurement (`/api/purchases`)
- `GET /api/purchases` — Query procurement ledger with base, category, and date filtering
- `POST /api/purchases` — Record newly acquired defense materiel / factory receipts

### 4. Inter-Base Movements (`/api/transfers`)
- `GET /api/transfers` — Query transfer dispatch ledger
- `POST /api/transfers` — Execute atomic convoy transfer with balance validation

### 5. Troop Armory Custody (`/api/assignments`)
- `GET /api/assignments` — Query issued gear roster
- `POST /api/assignments` — Issue weapon/asset to personnel
- `POST /api/assignments/{id}/return` — Check asset back into base physical armory

### 6. Munitions Expenditure (`/api/expenditures`)
- `GET /api/expenditures` — Query live-fire & training consumption registry
- `POST /api/expenditures` — Deduct expended munitions from armory stock

### 7. Armory Master Registries
- `GET /api/equipment` — Master defense asset catalog
- `POST /api/equipment` — Catalog new defense asset/model
- `GET /api/bases` — Military installations and garrisons
- `GET /api/personnel` — Troop & staff service roster
- `GET /api/users` — Authorized operator accounts (Admin only)
- `GET /api/audit-logs` — Immutable security audit trail

---

## 🛠️ Setup & Running

### Prerequisites
- Java 17+ or Java 21+
- Apache Maven 3.8+
- MySQL 8.0

### Configuration
Set the required environment variables or provide via system properties:
```bash
export DB_HOST=localhost
export DB_PORT=3307
export DB_NAME=mams_db
export DB_USERNAME=root
export DB_PASSWORD=your_secure_password
export JWT_SECRET=c3VwZXItc2VjdXJlLW1pbGl0YXJ5LWFzc2V0LW1hbmFnZW1lbnQtc3lzdGVtLWtleS1mb3Itand0LWF1dGhlbnRpY2F0aW9uLXNwcmluZy1ib290
```

### Build & Run
```bash
# Compile and package
mvn clean package -DskipTests

# Run the backend server
mvn spring-boot:run
```
The server will bind to `http://localhost:8080`.
