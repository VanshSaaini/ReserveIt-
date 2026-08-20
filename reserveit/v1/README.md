# ReserveIt — Backend API

A Spring Boot REST API for **ReserveIt**, the clinic appointment booking platform, built to match
the `reserveit-frontend` React app. Secures every endpoint with **JWT** and enforces **4 roles**:
`PATIENT`, `DOCTOR`, `CLINIC_ADMIN`, `SUPER_ADMIN`.

## Stack

- Java 22, Spring Boot 4.1 (`spring-boot-starter-data-jpa`, `-security`, `-webmvc`, `-validation`)
- PostgreSQL (via Spring Data JPA / Hibernate)
- JJWT 0.12 for token issuing/validation
- Lombok
- BCrypt password hashing

## Getting started

1. **Create the database**

   ```sql
   CREATE DATABASE reserveit;
   ```

2. **Configure `src/main/resources/application.properties`** (or override via env vars —
   see the file, every value has a `${VAR:default}` fallback):

   | Property | Env var | Default |
   |---|---|---|
   | `spring.datasource.url` | — | `jdbc:postgresql://localhost:5432/reserveit` |
   | `spring.datasource.username` / `password` | — | `postgres` / `saini18` |
   | `jwt.secret` | `JWT_SECRET` | dev placeholder — **change in production** |
   | `jwt.expiration-ms` | `JWT_EXPIRATION_MS` | `86400000` (24h) |
   | `app.cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` |
   | `app.admin.email` / `app.admin.password` | `ADMIN_EMAIL` / `ADMIN_PASSWORD` | `admin@reserveit.com` / `Admin@123` |

   `spring.jpa.hibernate.ddl-auto=update` will create/update all tables automatically on
   startup — no manual migrations needed for local dev.

3. **Run it**

   ```bash
   ./mvnw spring-boot:run
   ```

   The API listens on `http://localhost:8080`. On first boot, `DataSeeder` creates a
   `SUPER_ADMIN` account using the `app.admin.*` credentials above — log in with that to
   reach the admin endpoints.

4. **Run the frontend against it** — `reserveit-frontend`'s Vite dev server already proxies
   `/api/**` to `http://localhost:8080` (see `vite.config.js`), so `npm run dev` in the
   frontend just works with no extra config.

## Auth

`POST /api/auth/register` and `POST /api/auth/login` match `src/api/client.js` in the
frontend exactly.

- **Register** — `accountType` is `"patient"` or `"clinic"`. A patient registration creates a
  `User` + `Patient` profile. A clinic registration creates a `User` (role `CLINIC_ADMIN`) +
  a `Clinic` using `clinicName` / `clinicAddress`.
- **Login** — returns `{ token, tokenType, userId, email, firstName, lastName, role, clinicId }`.
  Send the token back as `Authorization: Bearer <token>` on every subsequent request.
- Doctor accounts aren't self-registrable — a clinic admin creates them via
  `POST /api/doctors` for their own clinic. The one `SUPER_ADMIN` account is seeded on startup.

## Roles & endpoints

| Area | Endpoint | Method | Access |
|---|---|---|---|
| Auth | `/api/auth/register`, `/api/auth/login` | POST | public |
| Clinics | `/api/clinics`, `/api/clinics/{id}` | GET | public |
| Clinics | `/api/clinics/{id}/doctors`, `/api/clinics/{id}/services` | GET | public |
| Clinics (self) | `/api/clinics/me` | GET/PUT | `CLINIC_ADMIN` |
| Clinics (self) | `/api/clinics/me/doctors`, `/api/clinics/me/services` | GET | `CLINIC_ADMIN` |
| Doctors | `/api/doctors/{id}` | GET | public |
| Doctors | `/api/doctors/{id}/availability/slots?date=YYYY-MM-DD` | GET | public — real bookable slots |
| Doctors | `/api/doctors` | POST | `CLINIC_ADMIN` — add a doctor to own clinic |
| Doctors | `/api/doctors/{id}/active` | PATCH | `CLINIC_ADMIN`, `SUPER_ADMIN` |
| Doctors (self) | `/api/doctors/me` | GET | `DOCTOR` |
| Doctors (self) | `/api/doctors/me/availability` | GET/POST | `DOCTOR` |
| Doctors (self) | `/api/doctors/me/availability/{id}` | DELETE | `DOCTOR` |
| Services | `/api/clinics/me/services` | POST | `CLINIC_ADMIN` |
| Services | `/api/services/{id}` | PUT/DELETE | `CLINIC_ADMIN` (own clinic only) |
| Patients | `/api/patients/me` | GET | `PATIENT` |
| Appointments | `/api/appointments` | POST | `PATIENT` — book |
| Appointments | `/api/appointments/me` | GET | `PATIENT` — own history |
| Appointments | `/api/appointments/doctor/me` | GET | `DOCTOR` — own schedule |
| Appointments | `/api/appointments/clinic/me` | GET | `CLINIC_ADMIN` — clinic-wide oversight |
| Appointments | `/api/appointments/{id}/status` | PATCH | `DOCTOR`, `CLINIC_ADMIN`, `SUPER_ADMIN` |
| Appointments | `/api/appointments/{id}/reschedule` | PATCH | `PATIENT` (own appointment) |
| Appointments | `/api/appointments/{id}/cancel` | PATCH | `PATIENT`, `DOCTOR`, `CLINIC_ADMIN`, `SUPER_ADMIN` |
| Admin | `/api/admin/users`, `/api/admin/users/{id}/active` | GET/PATCH | `SUPER_ADMIN` |
| Admin | `/api/admin/clinics`, `/api/admin/clinics/{id}/active` | GET/PATCH | `SUPER_ADMIN` |

All non-public endpoints require `Authorization: Bearer <token>`. Ownership is enforced in
the service layer (e.g. a clinic admin can only manage their own clinic's doctors/services;
a doctor can only manage their own availability and appointments).

## How booking works

1. `GET /api/doctors/{id}/availability/slots?date=...` computes bookable slots from the
   doctor's recurring weekly `DoctorAvailability` windows, minus any slot already covered by
   a non-cancelled `Appointment` on that date.
2. `POST /api/appointments` re-validates that the requested slot doesn't conflict with an
   existing appointment before saving (protects against a race between two patients booking
   the same slot).
3. Appointment status moves through `CONFIRMED → COMPLETED / CANCELLED / RESCHEDULED`,
   settable by the doctor or clinic admin who owns it; a patient can self-service
   cancel/reschedule their own upcoming appointments.

## Notes / simplifications

- A doctor belongs to exactly **one** clinic in this MVP (`Doctor.clinic` is a `@ManyToOne`,
  not a many-to-many) — straightforward to extend to multi-clinic doctors later via a join
  table if needed.
- Email/reminder notifications mentioned in the frontend copy aren't wired to a real mail
  provider — hook `AppointmentManagementService` up to `spring-boot-starter-mail` (or an
  events-based async listener) when you're ready.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development; swap to a real
  migration tool (Flyway/Liquibase) before production.
