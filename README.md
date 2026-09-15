# Help Desk / Ticket Management System

A clean, robust, student-friendly **Help Desk / Ticket Management System** backend built with **Java** and **Spring Boot 3**. This project demonstrates core Advanced Java enterprise concepts including RESTful APIs, Spring Data JPA, Hibernate ORM, Layered Architecture, DTO pattern, Jakarta Bean Validation, Enums, and Global Exception Handling.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Technologies Used](#-technologies-used)
3. [Required Software](#-required-software)
4. [Architecture Overview](#-architecture-overview)
5. [Database Design & Relationships](#-database-design--relationships)
6. [Project Structure](#-project-structure)
7. [Setup & Installation](#-setup--installation)
8. [REST API Endpoints Reference](#-rest-api-endpoints-reference)
9. [Postman Testing Order & Sample Requests](#-postman-testing-order--sample-requests)
10. [Error Handling & Responses](#-error-handling--responses)

---

## 🌟 Project Overview
The Help Desk / Ticket Management System allows users to submit support tickets, support agents to be assigned to tickets, track progress across status lifecycles (`OPEN` ➔ `IN_PROGRESS` ➔ `RESOLVED` ➔ `CLOSED`), and post chronological discussions/comments on tickets.

---

## 🛠 Technologies Used
- **Language**: Java 17 / 21
- **Framework**: Spring Boot 3.3.5
- **ORM / Persistence**: Spring Data JPA & Hibernate
- **Database**: MySQL 8.x
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Email`, `@Size`)
- **Build Tool**: Apache Maven
- **API Testing**: Postman

---

## 💻 Required Software
1. **JDK 17 or JDK 21+** (e.g. Oracle JDK or Eclipse Temurin)
2. **MySQL Server & MySQL Workbench** (or MySQL CLI)
3. **IDE**: IntelliJ IDEA, Eclipse, or VS Code
4. **Postman**: For API testing
5. **Maven**: (Bundled with IntelliJ / Eclipse or installed on PATH)

---

## 🏛 Architecture Overview
The application strictly follows the standard **Layered Architecture**:

```
        HTTP Request (Postman)
                 │
                 ▼
        ┌──────────────────┐
        │    Controller    │  @RestController
        │      Layer       │  Handles HTTP requests, path variables & @Valid DTOs
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │     Service      │  @Service
        │      Layer       │  Contains business logic, validations & DTO-Entity mappers
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │    Repository    │  @Repository (Spring Data JPA)
        │      Layer       │  JpaRepository interfaces for database access & queries
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │ Hibernate / JPA  │  Object-Relational Mapping (ORM)
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │  MySQL Database  │  helpdesk_db (users, tickets, comments tables)
        └──────────────────┘
```

---

## 🗄 Database Design & Relationships

### Entities & Tables
1. **`User` (`users` table)**:
   - `id` (Primary Key, Auto-increment)
   - `name` (String, Not Null)
   - `email` (String, Unique, Not Null)
   - `password` (String, Not Null)
   - `role` (Enum String: `USER`, `AGENT`, `ADMIN`)
   - `created_at` (Timestamp, Not Null, Auto-generated)

2. **`Ticket` (`tickets` table)**:
   - `id` (Primary Key, Auto-increment)
   - `title` (String, Not Null)
   - `description` (TEXT, Not Null)
   - `status` (Enum String: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`)
   - `priority` (Enum String: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
   - `created_at` (Timestamp, Not Null)
   - `updated_at` (Timestamp)
   - `created_by_user_id` (Foreign Key ➔ `users.id`, `@ManyToOne`)
   - `assigned_to_user_id` (Foreign Key ➔ `users.id`, `@ManyToOne`, Nullable)

3. **`Comment` (`comments` table)**:
   - `id` (Primary Key, Auto-increment)
   - `message` (TEXT, Not Null)
   - `created_at` (Timestamp, Not Null)
   - `user_id` (Foreign Key ➔ `users.id`, `@ManyToOne`)
   - `ticket_id` (Foreign Key ➔ `tickets.id`, `@ManyToOne`)

### Relationship Summary
- **User ➔ Tickets (Created)**: One-to-Many (`@ManyToOne` in Ticket). A user can create many tickets.
- **Agent/User ➔ Tickets (Assigned)**: One-to-Many (`@ManyToOne` in Ticket). An agent can be assigned to multiple tickets.
- **Ticket ➔ Comments**: One-to-Many (`@OneToMany` in Ticket, `@ManyToOne` in Comment). A ticket holds multiple comments.
- **User ➔ Comments**: One-to-Many (`@ManyToOne` in Comment). A user can post comments across tickets.

---

## 📂 Project Structure

```
helpdesk-ticket-management/
│
├── pom.xml
├── README.md
├── PROJECT_EXPLANATION.md
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── helpdesk/
    │   │           ├── HelpDeskApplication.java
    │   │           │
    │   │           ├── controller/
    │   │           │   ├── UserController.java
    │   │           │   ├── TicketController.java
    │   │           │   └── CommentController.java
    │   │           │
    │   │           ├── service/
    │   │           │   ├── UserService.java
    │   │           │   ├── TicketService.java
    │   │           │   └── CommentService.java
    │   │           │
    │   │           ├── repository/
    │   │           │   ├── UserRepository.java
    │   │           │   ├── TicketRepository.java
    │   │           │   └── CommentRepository.java
    │   │           │
    │   │           ├── entity/
    │   │           │   ├── User.java
    │   │           │   ├── Ticket.java
    │   │           │   └── Comment.java
    │   │           │
    │   │           ├── dto/
    │   │           │   ├── UserRequestDTO.java
    │   │           │   ├── UserResponseDTO.java
    │   │           │   ├── TicketRequestDTO.java
    │   │           │   ├── TicketResponseDTO.java
    │   │           │   ├── CommentRequestDTO.java
    │   │           │   ├── CommentResponseDTO.java
    │   │           │   └── TicketStatusUpdateDTO.java
    │   │           │
    │   │           ├── enums/
    │   │           │   ├── Role.java
    │   │           │   ├── TicketStatus.java
    │   │           │   └── Priority.java
    │   │           │
    │   │           └── exception/
    │   │               ├── ResourceNotFoundException.java
    │   │               ├── InvalidOperationException.java
    │   │               ├── ErrorResponse.java
    │   │               └── GlobalExceptionHandler.java
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/
            └── com/
                └── helpdesk/
                    └── HelpDeskApplicationTests.java
```

---

## ⚙️ Setup & Installation

### Step 1: Create the MySQL Database
Open your MySQL Command Line or MySQL Workbench and run:
```sql
CREATE DATABASE helpdesk_db;
```

### Step 2: Configure `application.properties`
Open `src/main/resources/application.properties` and configure your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```
> **Note**: Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL `root` password.

### Step 3: Run the Application
You can run the project using either of the following methods:

**Method A: Using IDE**
- Open the project in IntelliJ IDEA / Eclipse / VS Code.
- Navigate to `src/main/java/com/helpdesk/HelpDeskApplication.java`.
- Right-click and select **Run 'HelpDeskApplication'**.

**Method B: Using Maven in Terminal**
```bash
mvn spring-boot:run
```

The application will start on: `http://localhost:8080`

---

## 📌 REST API Endpoints Reference

### 1. User APIs (`/api/users`)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| `POST` | `/api/users` | Register/Create a new user | `201 Created` |
| `GET` | `/api/users` | Get list of all users | `200 OK` |
| `GET` | `/api/users/{id}` | Get user by ID | `200 OK` / `404` |
| `PUT` | `/api/users/{id}` | Update user details | `200 OK` / `404` |
| `DELETE`| `/api/users/{id}` | Delete user by ID | `204 No Content` |

### 2. Ticket APIs (`/api/tickets`)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| `POST` | `/api/tickets` | Create a new ticket (Status defaults to `OPEN`) | `201 Created` |
| `GET` | `/api/tickets` | Get all tickets | `200 OK` |
| `GET` | `/api/tickets/{id}` | Get ticket by ID | `200 OK` / `404` |
| `PUT` | `/api/tickets/{id}` | Update ticket (title, description, priority) | `200 OK` / `404` |
| `DELETE`| `/api/tickets/{id}` | Delete ticket by ID | `204 No Content` |
| `PUT` | `/api/tickets/{ticketId}/assign/{agentId}` | Assign ticket to an AGENT | `200 OK` / `400` / `404` |
| `PUT` | `/api/tickets/{ticketId}/status` | Update ticket status (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`) | `200 OK` / `400` / `404` |

### 3. Ticket Filter APIs
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| `GET` | `/api/tickets/status/{status}` | Filter tickets by status (e.g. `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`) | `200 OK` |
| `GET` | `/api/tickets/priority/{priority}` | Filter tickets by priority (e.g. `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) | `200 OK` |
| `GET` | `/api/tickets/user/{userId}` | Filter tickets created by a specific user | `200 OK` |
| `GET` | `/api/tickets/agent/{agentId}` | Filter tickets assigned to a specific agent | `200 OK` |

### 4. Comment APIs (`/api/tickets/{ticketId}/comments`)
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| `POST` | `/api/tickets/{ticketId}/comments` | Add comment to a ticket | `201 Created` |
| `GET` | `/api/tickets/{ticketId}/comments` | Get all comments for a ticket (chronological) | `200 OK` |

---

## 🧪 Postman Testing Order & Sample Requests

Follow this exact order to test the complete lifecycle in Postman:

### Step 1: Create a Normal USER
- **URL**: `POST http://localhost:8080/api/users`
- **Body** (raw JSON):
```json
{
  "name": "Ravi M",
  "email": "ravi123@gmail.com",
  "password": "password123",
  "role": "USER"
}
```
- **Response**: `201 Created`
```json
{
  "id": 1,
  "name": "Ravi M",
  "email": "ravi123@gmail.com",
  "role": "USER",
  "createdAt": "2026-09-13T15:00:00"
}
```

---

### Step 2: Create a Support AGENT
- **URL**: `POST http://localhost:8080/api/users`
- **Body** (raw JSON):
```json
{
  "name": "Support Agent",
  "email": "agent@example.com",
  "password": "agentPassword123",
  "role": "AGENT"
}
```
- **Response**: `201 Created` (with `id: 2`)

---

### Step 3: Get All Users
- **URL**: `GET http://localhost:8080/api/users`
- **Response**: `200 OK` (list containing both users)

---

### Step 4: USER Creates a Ticket
- **URL**: `POST http://localhost:8080/api/tickets`
- **Body** (raw JSON):
```json
{
  "title": "Unable to login to portal",
  "description": "Getting an 'Invalid credentials' error even with the correct password.",
  "priority": "HIGH",
  "createdByUserId": 1
}
```
- **Response**: `201 Created`
```json
{
  "id": 1,
  "title": "Unable to login to portal",
  "description": "Getting an 'Invalid credentials' error even with the correct password.",
  "status": "OPEN",
  "priority": "HIGH",
  "createdAt": "2026-09-13T15:05:00",
  "updatedAt": "2026-09-13T15:05:00",
  "createdById": 1,
  "createdByName": "Hariharan",
  "assignedToId": null,
  "assignedToName": null
}
```

---

### Step 5: Get Ticket by ID
- **URL**: `GET http://localhost:8080/api/tickets/1`
- **Response**: `200 OK`

---

### Step 6: Assign Ticket to Support Agent
- **URL**: `PUT http://localhost:8080/api/tickets/1/assign/2`
- **Response**: `200 OK`
```json
{
  "id": 1,
  "title": "Unable to login to portal",
  "description": "Getting an 'Invalid credentials' error even with the correct password.",
  "status": "OPEN",
  "priority": "HIGH",
  "createdAt": "2026-09-13T15:05:00",
  "updatedAt": "2026-09-13T15:10:00",
  "createdById": 1,
  "createdByName": "Hariharan",
  "assignedToId": 2,
  "assignedToName": "Support Agent"
}
```

---

### Step 7: Update Status to `IN_PROGRESS`
- **URL**: `PUT http://localhost:8080/api/tickets/1/status`
- **Body** (raw JSON):
```json
{
  "status": "IN_PROGRESS"
}
```
- **Response**: `200 OK` (Status updated to `IN_PROGRESS`)

---

### Step 8: Agent Posts a Comment
- **URL**: `POST http://localhost:8080/api/tickets/1/comments`
- **Body** (raw JSON):
```json
{
  "message": "We have received your ticket and are investigating your account login logs.",
  "userId": 2
}
```
- **Response**: `201 Created`
```json
{
  "id": 1,
  "message": "We have received your ticket and are investigating your account login logs.",
  "createdAt": "2026-09-13T15:12:00",
  "userId": 2,
  "userName": "Support Agent",
  "ticketId": 1
}
```

---

### Step 9: Get All Comments for the Ticket
- **URL**: `GET http://localhost:8080/api/tickets/1/comments`
- **Response**: `200 OK` (List of comments for ticket 1)

---

### Step 10: Update Status to `RESOLVED`
- **URL**: `PUT http://localhost:8080/api/tickets/1/status`
- **Body** (raw JSON):
```json
{
  "status": "RESOLVED"
}
```
- **Response**: `200 OK`

---

### Step 11: Update Status to `CLOSED`
- **URL**: `PUT http://localhost:8080/api/tickets/1/status`
- **Body** (raw JSON):
```json
{
  "status": "CLOSED"
}
```
- **Response**: `200 OK`

---

### Step 12: Test Filtering APIs
- Filter by status: `GET http://localhost:8080/api/tickets/status/CLOSED`
- Filter by priority: `GET http://localhost:8080/api/tickets/priority/HIGH`
- Filter by creator: `GET http://localhost:8080/api/tickets/user/1`
- Filter by agent: `GET http://localhost:8080/api/tickets/agent/2`

---

### Step 13: Test Exception Handling & Edge Cases

#### Case A: Test Invalid Ticket ID (404 Not Found)
- **URL**: `GET http://localhost:8080/api/tickets/999`
- **Response**: `404 Not Found`
```json
{
  "timestamp": "2026-09-13T15:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 999",
  "path": "/api/tickets/999"
}
```

#### Case B: Test Validation by Sending Blank Fields (400 Bad Request)
- **URL**: `POST http://localhost:8080/api/users`
- **Body** (raw JSON):
```json
{
  "name": "",
  "email": "invalid-email",
  "password": "123",
  "role": null
}
```
- **Response**: `400 Bad Request`
```json
{
  "timestamp": "2026-09-13T15:16:00",
  "status": 400,
  "error": "Validation Failed",
  "message": {
    "name": "Name cannot be blank",
    "email": "Email must be a valid email format",
    "password": "Password must have at least 6 characters",
    "role": "Role must not be null (USER, AGENT, ADMIN)"
  },
  "path": "/api/users"
}
```

#### Case C: Try Assigning a USER instead of an AGENT (400 Bad Request)
- **URL**: `PUT http://localhost:8080/api/tickets/1/assign/1` (User 1 has role `USER`)
- **Response**: `400 Bad Request`
```json
{
  "timestamp": "2026-09-13T15:17:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot assign ticket. User with id 1 is not an AGENT (Current role: USER)",
  "path": "/api/tickets/1/assign/1"
}
```

#### Case D: Try Invalid Status Transition from CLOSED to OPEN (400 Bad Request)
- **URL**: `PUT http://localhost:8080/api/tickets/1/status`
- **Body** (raw JSON):
```json
{
  "status": "OPEN"
}
```
- **Response**: `400 Bad Request`
```json
{
  "timestamp": "2026-09-13T15:18:00",
  "status": 400,
  "error": "Bad Request",
  "message": "A CLOSED ticket cannot be directly transitioned back to OPEN.",
  "path": "/api/tickets/1/status"
}
```

---

## 🛡 Error Handling & Responses
All errors are centralized via `@RestControllerAdvice` in `GlobalExceptionHandler.java`. It produces clean, standardized JSON objects matching:

```json
{
  "timestamp": "2026-09-13T15:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message or validation map",
  "path": "/api/endpoint"
}
```
