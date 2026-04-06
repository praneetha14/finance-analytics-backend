# Finance Analytics Backend

A robust Spring Boot microservice for financial tracking, providing real-time analytics, automated category grouping, and secure role-based access.

## 🚀 Key Features

- **JWT Security**: Multi-layered security with Bearer Token authentication.
- **Smart Validation**: Returns all input errors (mobile, email, amount, recordType, etc.) in a single, clean JSON response.
- **Advanced Dashboard**: 
    - **Category-wise Insights**: Breakdown of both Income and Expenses.
    - **Monthly Trends**: Month-over-month balance tracking (e.g., "2024-03": 5000.0).
    - **Recent Activity**: Quick view of the last 5 transactions.
- **Intelligent Record Management**:
    - **Advanced Search**: Filter records by Date Range, Category, Type, or User ID.
    - **Duplicate Protection**: Prevents identical entries within a 60-second window.
    - **Normalization**: Automatically groups "Food", "FOOD", and "food" into a single lowercase category.
- **Role-Based Access (RBAC)**:
    - **ADMIN**: Full management of Users, Roles, and Records.
    - **ANALYST**: Access to all financial records and aggregated insights/trends.
    - **VIEWER**: Read-only access to their own dashboard summaries.

---

## 🔑 Getting Started

### 1. Default Admin Credentials
- **Email**: `admin@finance.com`
- **Password**: `Admin@123`

### 2. Authentication Flow (Swagger)
1. Call `POST /api/v1/auth/login` with the credentials above.
2. Copy the `token` from the response.
3. Click **Authorize** in Swagger and enter: `Bearer <your_token>`.

---

## 🛠 API Documentation

### 1. Authentication (`AuthController`)
- `POST /api/v1/auth/login`: Authenticate and receive a JWT token.

### 2. Financial Records (`FinancialRecordController`)
- `GET /api/v1/records/search`: **Advanced Filtering**
    - Query Params: `userId`, `type` (INCOME/EXPENSE), `category`, `startDate`, `endDate`.
- `POST /api/v1/records/create/{userId}`: Create record (includes normalization and 60s duplicate check).
- `PUT /api/v1/records/update/{recordId}`: Update existing record details.
- `DELETE /api/v1/records/delete/{recordId}`: Soft delete a record.
- `GET /api/v1/records/get/{recordId}`: Fetch specific record details.

### 3. Dashboard & Insights (`DashboardController`)
- `GET /api/v1/dashboard/summary/{userId}`: Returns totals, category breakdowns, recent activity, and monthly trends.
- `GET /api/v1/dashboard/records/all`: Paginated view of all active records (Analyst/Admin only).

### 4. User Management (`UserController` - Admin Only)
- `POST /api/v1/users/create`: Create new user with Role ID (Strict mobile/email validation).
- `GET /api/v1/dashboard/getAllUsers`: Paginated list of all system users.
- `POST /api/v1/users/{userId}/assign-roles`: Update roles for a specific user.

---

## 🛡 Security & Error Handling

| Status Code | Description | Error Handling Behavior |
| :--- | :--- | :--- |
| **200/201** | Success | Returns the requested resource or success VO. |
| **400** | Validation/Input | Returns **all** field errors at once in a JSON map. |
| **401** | Unauthorized | Friendly message for invalid credentials or missing tokens. |
| **403** | Forbidden | Clear message for missing permissions (Access Denied). |
| **409** | Conflict/Duplicate | Triggered by the 60-second duplicate record check. |
