# Finance Analytics Backend

This microservice provides financial analytics for users, allowing them to track their income and expenses, and providing summaries and detailed reports.

## Features

- **JWT Authentication**: Secure API access using JSON Web Tokens.
- **Role-Based Access Control (RBAC)**: Fine-grained permissions for different roles:
    - **ADMIN**: Full access to all features, including user and role management.
    - **ANALYST**: Access to view financial records and summaries for all users.
    - **VIEWER**: Access to view own financial records and summaries.
- **Financial Record Management**: Create, update, delete, and view financial records.
- **Dashboard**: Get summaries of income, expenses, and balances, including category-wise breakdowns.
- **User Management**: Admin can create, update, delete, and manage user roles.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** (with JWT)
- **Spring Data JPA**
- **MySQL** (or H2 for testing)
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)

## Getting Started

### Prerequisites

- JDK 17
- Gradle

### Configuration

The application can be configured via `src/main/resources/application.yml` or `application.properties`.

Key configurations:
- `jwt.secret`: Secret key for JWT signing.
- `jwt.expiration`: Token expiration time in milliseconds.

### Running the Application

```bash
./gradlew bootRun
```

The application will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Default Admin User

On the first run, a default admin user is created:
- **Email**: `admin@finance.com`
- **Password**: `Admin@123`

## API Endpoints

### Authentication
- `POST /api/v1/auth/login`: Authenticate and receive a JWT token.

### Financial Records
- `POST /api/v1/records/create/{userId}`: Create a new financial record.
- `PUT /api/v1/records/update/{recordId}`: Update an existing record.
- `DELETE /api/v1/records/delete/{recordId}`: Soft delete a record.
- `GET /api/v1/records/{recordId}`: Get a record by ID.
- `GET /api/v1/records/user/{userId}`: Get all records for a user.

### Dashboard
- `GET /api/v1/dashboard/summary/{userId}`: Get financial summary for a user.
- `GET /api/v1/dashboard/records/{userId}`: Get records for a user.
- `GET /api/v1/dashboard/records/all`: Get all records (Analyst/Admin).

### Users (Admin only)
- `GET /api/v1/users`: List all users.
- `POST /api/v1/users/create`: Create a new user.
- `PUT /api/v1/users/update/{userId}`: Update user details.
- `DELETE /api/v1/users/delete/{userId}`: Soft delete a user.
- `POST /api/v1/users/{userId}/assign-roles`: Assign roles to a user.

## Security

Permissions are embedded in the JWT token. The following permissions are supported:
- `FINANCIAL_RECORD_READ`, `FINANCIAL_RECORD_WRITE`, `FINANCIAL_RECORD_DELETE`
- `FINANCIAL_SUMMARY_READ`
- `USER_READ`, `USER_WRITE`, `USER_DELETE`
- `ROLE_READ`, `ROLE_WRITE`
- `PERMISSION_READ`, `PERMISSION_WRITE`
