# Task Management REST API (`task-manager`)

A production-ready Spring Boot 3.x REST API for task management built using Java 21, Spring Data JPA, PostgreSQL, Flyway database migrations, Jakarta Bean Validation, Spring Boot Actuator, and Springdoc OpenAPI (Swagger).

This project is designed with a clean layered architecture, stateless design, and externalized environment configuration to serve as a foundational DevOps/SRE demonstration application (easily containerizable with Docker, Kubernetes, Helm, Argo CD, Terraform, Prometheus/Grafana, etc.).

---

## Architecture

```text
Client
  ↓
REST Controller
  ↓
Service
  ↓
Repository
  ↓
PostgreSQL
```

- **Controller Layer (`TaskController`)**: Exposes RESTful endpoints, handles HTTP requests/responses, validates payloads via DTOs, and documents API specs.
- **Service Layer (`TaskService`)**: Encapsulates business logic, transactional boundaries, and structured SLF4J logging.
- **Repository Layer (`TaskRepository`)**: Interacts with PostgreSQL using Spring Data JPA (`JpaRepository`).
- **Database Layer**: PostgreSQL instance managed via Flyway SQL migrations (`V1__create_tasks_table.sql`).

---

## Technology Stack

- **Java 21**
- **Spring Boot 3.4.3**
- **Maven**
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway Migration**
- **Spring Boot Validation (Jakarta Bean Validation)**
- **Spring Boot Actuator**
- **Springdoc OpenAPI / Swagger UI 2.8.5**
- **JUnit 5, Mockito & Spring Boot Test**
- **H2 Database** (Test scope)

---

## Prerequisites

- **Java JDK 21** or later
- **Apache Maven 3.8+**
- **PostgreSQL 14+** (for runtime database)

---

## Configuration

The application uses standard Spring Boot `application.yml` configuration with environment variable support and sensible local defaults:

| Environment Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_HOST` | PostgreSQL Hostname / Container Name | `localhost` |
| `DB_PORT` | PostgreSQL Database Port | `5432` |
| `DB_NAME` | PostgreSQL Database Name | `taskdb` |
| `DB_USERNAME` | PostgreSQL Database Username | `postgres` |
| `DB_PASSWORD` | PostgreSQL Database Password | `postgres` |

---

## Database Setup

1. Connect to your PostgreSQL server:
   ```bash
   psql -U postgres
   ```

2. Create the `taskdb` database:
   ```sql
   CREATE DATABASE taskdb;
   ```

3. (Optional) Create a specific database user and grant privileges:
   ```sql
   CREATE USER taskuser WITH PASSWORD 'taskpass';
   GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;
   ```

Upon starting the application, **Flyway** automatically runs the migration script in `src/main/resources/db/migration/V1__create_tasks_table.sql` to construct the `tasks` schema.

---

## Running Locally

### Compile and Test
Run the unit and integration test suite (uses embedded H2 database automatically):
```bash
mvn clean test
```

### Start the Application
Start the Spring Boot server (connects to PostgreSQL using configured environment variables):
```bash
# Using default environment settings (localhost:5432/taskdb)
mvn spring-boot:run

# Or with explicit environment variables:
DB_HOST=localhost DB_PORT=5432 DB_NAME=taskdb DB_USERNAME=postgres DB_PASSWORD=postgres mvn spring-boot:run
```

The application will start on port `8080`.

---

## API Examples

### 1. Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Kubernetes",
    "description": "Practice Kubernetes deployment and Helm charts",
    "status": "TODO",
    "priority": "HIGH"
  }'
```
**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Learn Kubernetes",
  "description": "Practice Kubernetes deployment and Helm charts",
  "status": "TODO",
  "priority": "HIGH",
  "createdAt": "2026-09-15T23:51:36.565Z",
  "updatedAt": "2026-09-15T23:51:36.565Z"
}
```

### 2. Get All Tasks
```bash
curl -X GET http://localhost:8080/api/tasks
```
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Learn Kubernetes",
    "description": "Practice Kubernetes deployment and Helm charts",
    "status": "TODO",
    "priority": "HIGH",
    "createdAt": "2026-09-15T23:51:36.565Z",
    "updatedAt": "2026-09-15T23:51:36.565Z"
  }
]
```

### 3. Get Task by ID
```bash
curl -X GET http://localhost:8080/api/tasks/1
```
**Response (200 OK or 404 Not Found):**
```json
{
  "id": 1,
  "title": "Learn Kubernetes",
  "description": "Practice Kubernetes deployment and Helm charts",
  "status": "TODO",
  "priority": "HIGH",
  "createdAt": "2026-09-15T23:51:36.565Z",
  "updatedAt": "2026-09-15T23:51:36.565Z"
}
```

### 4. Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Master Kubernetes",
    "description": "Practice Helm charts and Argo CD deployments",
    "status": "IN_PROGRESS",
    "priority": "HIGH"
  }'
```
**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Master Kubernetes",
  "description": "Practice Helm charts and Argo CD deployments",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "createdAt": "2026-09-15T23:51:36.565Z",
  "updatedAt": "2026-09-15T23:52:00.123Z"
}
```

### 5. Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```
**Response (204 No Content)**

---

## Actuator & Health Monitoring

The application includes Spring Boot Actuator for health checks and readiness monitoring:

- **Health Endpoint:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Sample response:
  ```json
  {
    "status": "UP",
    "components": {
      "db": {
        "status": "UP",
        "details": {
          "database": "PostgreSQL",
          "validationQuery": "isValid()"
        }
      },
      "diskSpace": {
        "status": "UP"
      },
      "ping": {
        "status": "UP"
      }
    }
  }
  ```

---

## API Documentation (Swagger / OpenAPI)

Interactive API documentation is automatically generated by Springdoc OpenAPI:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Testing

Run unit, web layer, and repository tests using Maven:
```bash
mvn clean test
```

Test suite coverage:
- **`TaskServiceTest`**: Unit tests with Mockito for service methods, business logic, error handling, and exception throwing.
- **`TaskControllerTest`**: MockMvc tests verifying REST status codes (201, 200, 204, 400, 404), JSON serialization, validation failures, and `@RestControllerAdvice` error responses.
- **`TaskRepositoryTest`**: `@DataJpaTest` verifying entity mapping and database operations against an in-memory H2 database.
