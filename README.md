# ReserveIt

> Full-stack clinic appointment booking and management platform built with React, Spring Boot, PostgreSQL, JWT authentication, role-based access control, and Gmail SMTP notifications.

[![React](https://img.shields.io/badge/React-18.3.1-61DAFB?logo=react&logoColor=white)](https://react.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Vite](https://img.shields.io/badge/Vite-5.4-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)

## Overview

ReserveIt is a full-stack clinic appointment management system designed to connect patients, doctors, clinic administrators, and super administrators through a role-based web application.

Patients can discover clinics, view doctors and services, check availability, book appointments, view their appointments, and reschedule or cancel eligible bookings. Clinic administrators and doctors can manage appointment schedules and statuses, while administrators can manage users and clinics.

The platform also includes Gmail SMTP integration for appointment communication:

- Automatic confirmation email after a successful appointment booking.
- Manual **Send Reminder** action from the clinic appointment table.
- Appointment emails include patient, doctor, clinic, date, time, status, service, and notes when available.
- Email failures do not undo a successfully created appointment.

## Key Features

### Patient

- Patient registration and login.
- JWT-based authenticated sessions.
- Patient profile management.
- Browse and search clinics.
- View clinic doctors and services.
- View doctor availability and appointment slots.
- Book available appointments.
- View personal appointment history.
- Reschedule eligible appointments.
- Cancel eligible appointments.
- Receive appointment confirmation emails.

### Doctor

- Doctor authentication and role-based access.
- Doctor profile management.
- Manage availability.
- View personal appointment schedule.
- Update appointment status.
- Manage appointments assigned to the doctor.

### Clinic Administrator

- Clinic administrator authentication.
- Manage clinic profile.
- Manage clinic doctors.
- Manage clinic services.
- View clinic-wide appointments.
- Update appointment status.
- Cancel appointments.
- Send appointment reminder emails to individual patients.

### Super Administrator

- Manage users.
- Activate/deactivate users.
- Manage clinics.
- Activate/deactivate clinics.
- Access protected administrative operations.

### Email Notifications

ReserveIt uses Spring Boot Mail with Gmail SMTP.

#### Automatic confirmation

```text
Patient books appointment
        ↓
Appointment is validated
        ↓
Appointment is saved
        ↓
Confirmation email is sent
```

#### Manual reminder

```text
Clinic Appointments
        ↓
Select appointment
        ↓
Send Reminder
        ↓
Reminder email sent to that appointment's patient
```

## Technology Stack

### Frontend

| Technology | Purpose |
|---|---|
| React 18 | User interface |
| React Router 6 | Client-side routing |
| Vite 5 | Development server and build tool |
| JavaScript (ES Modules) | Application logic |
| CSS | Application styling |
| Fetch API | Backend communication |

### Backend

| Technology | Purpose |
|---|---|
| Java 22 | Backend language |
| Spring Boot 4.1 | Application framework |
| Spring Web MVC | REST APIs |
| Spring Data JPA | Persistence layer |
| Hibernate | ORM |
| Spring Security | Authentication and authorization |
| JWT / JJWT 0.12.6 | Token-based authentication |
| Bean Validation | Request validation |
| Lombok | Boilerplate reduction |
| Maven | Dependency and build management |
| Spring Boot Mail | Email delivery |

### Database & Infrastructure

| Technology | Purpose |
|---|---|
| PostgreSQL | Relational database |
| Gmail SMTP | Email delivery |
| Vite Dev Server | Frontend development |
| REST API | Frontend/backend integration |

## Architecture

ReserveIt follows a layered full-stack architecture:

```text
┌──────────────────────────────────────────┐
│              React Frontend              │
│     React + React Router + Vite          │
└───────────────────┬──────────────────────┘
                    │ HTTP / JSON
                    │ JWT Bearer Token
┌───────────────────▼──────────────────────┐
│           Spring Boot REST API           │
│                                          │
│ Controllers → Services → Repositories   │
│                  │                       │
│            Spring Security               │
│                  │                       │
│              JWT Auth                    │
└───────────────┬───────────────┬──────────┘
                │               │
                ▼               ▼
        ┌──────────────┐  ┌──────────────┐
        │ PostgreSQL   │  │ Gmail SMTP   │
        │   Database   │  │   Email      │
        └──────────────┘  └──────────────┘
```

## Project Structure

```text
reserveit/
├── reserveit-frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── styles/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── .env.example
│   ├── package.json
│   ├── package-lock.json
│   └── vite.config.js
│
└── v1/
    ├── src/
    │   ├── main/java/com/Reserveit/v1/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── exception/
    │   │   ├── repository/
    │   │   ├── security/
    │   │   └── service/
    │   └── main/resources/
    │       └── application.properties
    ├── pom.xml
    ├── mvnw
    └── mvnw.cmd
```

## Backend API

The backend exposes REST endpoints under `/api`.

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Clinics

```text
GET  /api/clinics
GET  /api/clinics/{id}
GET  /api/clinics/{id}/doctors
GET  /api/clinics/{id}/services
GET  /api/clinics/me
PUT  /api/clinics/me
```

### Doctors

```text
GET   /api/doctors/{id}
GET   /api/doctors/{id}/availability/slots
GET   /api/doctors/me
GET   /api/doctors/me/availability
POST  /api/doctors/me/availability
DELETE /api/doctors/me/availability/{id}
```

### Appointments

```text
POST  /api/appointments
GET   /api/appointments/me
GET   /api/appointments/doctor/me
GET   /api/appointments/clinic/me
POST  /api/appointments/{id}/reminder
PATCH /api/appointments/{id}/status
PATCH /api/appointments/{id}/reschedule
PATCH /api/appointments/{id}/cancel
```

### Patients

```text
GET /api/patients/me
PUT /api/patients/me
```

### Administration

```text
GET   /api/admin/users
PATCH /api/admin/users/{id}/active
GET   /api/admin/clinics
PATCH /api/admin/clinics/{id}/active
```

> API authorization is enforced through Spring Security and role-based access rules.

## Authentication & Authorization

ReserveIt uses JWT-based authentication.

The main application roles are:

```text
PATIENT
DOCTOR
CLINIC_ADMIN
SUPER_ADMIN
```

A successful login returns a JWT token that the frontend stores and sends with authenticated API requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

Protected endpoints use role-based authorization such as:

```java
@PreAuthorize("hasRole('PATIENT')")
```

and:

```java
@PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR', 'SUPER_ADMIN')")
```

## Email Configuration

Gmail SMTP credentials are supplied through environment variables.

The application expects:

```text
MAIL_USERNAME
MAIL_PASSWORD
```

Example:

```powershell
$env:MAIL_USERNAME="yourgmail@gmail.com"
$env:MAIL_PASSWORD="your-gmail-app-password"
```

For Gmail, use a **Google App Password** with 2-Step Verification enabled. Do not use or commit your normal Gmail account password.

The application uses:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

## Environment Variables

### Backend

Recommended environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_MS
CORS_ALLOWED_ORIGINS
ADMIN_EMAIL
ADMIN_PASSWORD
MAIL_USERNAME
MAIL_PASSWORD
```

Example local configuration:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/reserveit"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-db-password"

$env:JWT_SECRET="your-production-jwt-secret"
$env:JWT_EXPIRATION_MS="86400000"

$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"

$env:ADMIN_EMAIL="admin@reserveit.com"
$env:ADMIN_PASSWORD="your-admin-password"

$env:MAIL_USERNAME="yourgmail@gmail.com"
$env:MAIL_PASSWORD="your-gmail-app-password"
```

Never commit real credentials, JWT secrets, database passwords, or Gmail App Passwords.

## Prerequisites

Install the following before running the project:

- Java 22
- Maven or use the included Maven Wrapper
- Node.js and npm
- PostgreSQL
- A Gmail account with 2-Step Verification and an App Password if email functionality is required

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/ReserveIt.git
cd ReserveIt
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE reserveit;
```

Configure the database environment variables:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/reserveit"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-password"
```

### 3. Configure Gmail

Create a Gmail App Password and configure:

```powershell
$env:MAIL_USERNAME="yourgmail@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
```

### 4. Start the backend

From the backend directory:

```powershell
cd reserveit\v1
```

Using Maven:

```powershell
mvn clean install
mvn spring-boot:run
```

Or using the Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs on the Spring Boot configured port, which is `8080` by default unless overridden.

### 5. Configure the frontend

From the frontend directory:

```powershell
cd ..\reserveit-frontend
```

Install dependencies:

```powershell
npm install
```

If required, create a `.env` file based on `.env.example` and set:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Start the development server:

```powershell
npm run dev
```

The Vite development server normally runs at:

```text
http://localhost:5173
```

## Build for Production

### Frontend

```powershell
cd reserveit-frontend
npm run build
```

Preview the production build locally:

```powershell
npm run preview
```

### Backend

```powershell
cd ..\v1
mvn clean package
```

The generated JAR will be placed in:

```text
target/
```

## Testing

Run backend tests with:

```powershell
cd reserveit\v1
mvn test
```

Run frontend linting with:

```powershell
cd ..\reserveit-frontend
npm run lint
```

Build the frontend to verify the production bundle:

```powershell
npm run build
```

## Email Workflow

### Appointment confirmation

When a patient books an appointment, the backend:

1. Authenticates the patient.
2. Validates the selected doctor and service.
3. Calculates the appointment end time.
4. Checks for appointment conflicts.
5. Saves the appointment.
6. Sends a confirmation email to the patient's registered email address.
7. Returns the appointment response to the frontend.

The email contains:

```text
Appointment ID
Patient
Doctor
Clinic
Date
Start Time
End Time
Status
Service (when available)
Notes (when available)
```

If SMTP is temporarily unavailable, the appointment remains saved and the email failure is logged.

### Appointment reminder

Authorized clinic staff, doctors, and super administrators can send a reminder for an eligible appointment.

Endpoint:

```http
POST /api/appointments/{id}/reminder
```

The frontend exposes this functionality through the **Send Reminder** button in the Clinic Appointments table.

Cancelled and completed appointments cannot receive reminders.

## Security Considerations

Before deploying ReserveIt to production:

- Never commit `.env` files or real secrets.
- Use environment variables or a dedicated secrets manager.
- Replace development JWT secrets with a strong production secret.
- Use a strong database password.
- Change the default administrator credentials.
- Restrict CORS to trusted production domains.
- Use HTTPS in production.
- Do not use `spring.jpa.hibernate.ddl-auto=update` as the production database migration strategy.
- Use Flyway or Liquibase for controlled schema migrations.
- Configure production logging without exposing credentials or tokens.
- Use a dedicated transactional email provider if email volume grows beyond Gmail's appropriate usage.

## Development Notes

The current local-development configuration uses:

```text
Frontend: http://localhost:5173
Backend:  http://localhost:8080
Database: PostgreSQL
Email:    Gmail SMTP
```

The backend is structured around:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
PostgreSQL
```

Security is handled separately through:

```text
Spring Security
      ↓
JWT Authentication Filter
      ↓
Authenticated Principal
      ↓
Role-based Authorization
```

## Roadmap

Potential future improvements include:

- Automated scheduled appointment reminders.
- HTML email templates.
- Email delivery status tracking.
- Password reset via email.
- Email verification during registration.
- SMS/WhatsApp appointment notifications.
- Calendar integration.
- Flyway/Liquibase database migrations.
- API documentation with OpenAPI/Swagger.
- Docker and Docker Compose deployment.
- Production observability and centralized logging.
- Automated CI/CD pipeline.
- Comprehensive integration and end-to-end tests.

## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch.

```bash
git checkout -b feature/your-feature
```

3. Make your changes.
4. Run tests and linting.
5. Commit your changes.

```bash
git commit -m "feat: add your feature"
```

6. Push the branch.

```bash
git push origin feature/your-feature
```

7. Open a Pull Request.

## License

No license has been specified for this project yet.

If this repository is intended to be open source, add an appropriate license such as MIT before publishing it for unrestricted reuse.

## Author

**Vansh Saini**

---

Built with React, Spring Boot, PostgreSQL, JWT, and Gmail SMTP.

## Clinic Billing & Business Analytics

Clinic administrators can now manage appointment billing and clinic performance from the dashboard.

- Appointment price is captured when the booking is created, preserving historical revenue even if service prices change later.
- Each appointment has `PENDING` or `PAID` payment status.
- Clinic admins can mark an appointment paid or return it to pending.
- Appointment history includes patient, doctor, service, date/time, price and payment status.
- Daily booked revenue, collected revenue and pending revenue.
- Monthly booked revenue, collected revenue and pending revenue.
- Monthly appointment, completion, cancellation and payment KPIs.
- Doctor-level appointment and revenue performance.
- Service-level revenue performance.
- Doctor/payment/status filters on the clinic appointment history.
- Explicit SQL migration is provided under `v1/database/appointment_billing_phase.sql` for existing production databases.
