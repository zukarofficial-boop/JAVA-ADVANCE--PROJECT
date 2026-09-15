# Help Desk / Ticket Management System - Viva & Project Explanation Guide

A comprehensive, student-friendly revision guide explaining every fundamental concept, annotation, and design choice used in this project. Use this guide to excel in your project viva, presentation, and technical interviews.

---

## 📚 Table of Contents
1. [Spring Boot & Core Framework](#1-spring-boot--core-framework)
2. [REST APIs & HTTP Methods](#2-rest-apis--http-methods)
3. [JPA, Hibernate & Database ORM](#3-jpa-hibernate--database-orm)
4. [Spring Annotations Explained](#4-spring-annotations-explained)
5. [DTOs & Validation](#5-dtos--validation)
6. [Enums & Persistence](#6-enums--persistence)
7. [Database Relationships in this Project](#7-database-relationships-in-this-project)
8. [Exception Handling & Status Codes](#8-exception-handling--status-codes)
9. [Architecture & Workflow](#9-architecture--workflow)

---

## 1. Spring Boot & Core Framework

### Q1: What is Spring Boot?
**Answer**:
Spring Boot is an extension of the Spring framework that simplifies Java application development by providing:
1. **Auto-Configuration**: Automatically configures beans and components based on jar dependencies on the classpath.
2. **Embedded Server**: Includes built-in Tomcat/Jetty web servers, eliminating the need to install and configure an external server.
3. **Starter Dependencies (`starter-poms`)**: Bundles common dependencies (like `spring-boot-starter-web`, `spring-boot-starter-data-jpa`) into single easy-to-import modules.
4. **Opinionated Defaults**: Minimizes boilerplate XML configuration in favor of annotations and simple properties files.

---

### Q2: What is Dependency Injection (DI) and Inversion of Control (IoC)?
**Answer**:
- **Inversion of Control (IoC)** is a software engineering principle where the control of object creation and lifecycle management is transferred from the application developer to a container (the Spring IoC Container).
- **Dependency Injection (DI)** is the pattern used to implement IoC. Instead of a class instantiating its dependencies directly using `new Object()`, the container supplies ("injects") those dependencies at runtime (via constructor or setter).

---

### Q3: What is `@Autowired` vs Constructor Injection?
**Answer**:
- `@Autowired` tells Spring to automatically resolve and inject a collaborating bean into a field, setter, or constructor.
- In modern Spring Boot (since Spring 4.3+), when a class has a **single constructor**, Spring automatically injects dependencies through constructor injection even without the `@Autowired` annotation. Constructor injection is preferred because it makes components immutable (`final` fields) and easy to unit test with mocks.

---

## 2. REST APIs & HTTP Methods

### Q4: What is a REST API?
**Answer**:
**REST** stands for *Representational State Transfer*. A REST API is an architectural style for communication between client (e.g., Postman, web browser, mobile app) and server over HTTP using standard formats like **JSON**. Key characteristics include:
- **Stateless**: The server does not store client session state between requests.
- **Uniform Interface**: Resource-based URLs (`/api/tickets`, `/api/users`).
- **Standard HTTP Methods**: `GET`, `POST`, `PUT`, `DELETE`.

---

### Q5: What is the difference between POST, GET, PUT, and DELETE?
**Answer**:
| HTTP Method | Purpose | Idempotent? | Project Example |
|-------------|---------|-------------|-----------------|
| **`POST`** | Creates a new resource | No | `POST /api/tickets` (Creates a ticket) |
| **`GET`** | Retrieves an existing resource | Yes | `GET /api/tickets/1` (Fetches ticket details) |
| **`PUT`** | Updates/Replaces an existing resource | Yes | `PUT /api/tickets/1` (Updates ticket info) |
| **`DELETE`**| Removes a resource | Yes | `DELETE /api/users/1` (Deletes a user) |

*(An operation is **idempotent** if calling it multiple times with the same parameters produces the exact same end state as calling it once.)*

---

### Q6: What are HTTP Status Codes?
**Answer**:
Standard 3-digit numeric codes returned by the server to describe the outcome of an HTTP request:
- **`200 OK`**: Request succeeded (used for successful `GET` and `PUT`).
- **`201 Created`**: Resource was successfully created (used for `POST`).
- **`204 No Content`**: Action succeeded with no response body to return (used for `DELETE`).
- **`400 Bad Request`**: Client sent invalid data, violated validation rules, or attempted an illegal operation.
- **`404 Not Found`**: The requested resource ID was not found in the database.
- **`500 Internal Server Error`**: Unexpected server-side failure.

---

### Q7: What is `ResponseEntity`?
**Answer**:
`ResponseEntity<T>` is a Spring MVC class that represents the complete HTTP response. It allows developers to customize:
1. **HTTP Status Code** (e.g., `HttpStatus.CREATED`, `HttpStatus.OK`, `HttpStatus.NO_CONTENT`).
2. **HTTP Headers** (e.g., `Content-Type`, custom headers).
3. **Response Body** (the payload data returned to the client as JSON).

---

## 3. JPA, Hibernate & Database ORM

### Q8: What is JPA?
**Answer**:
**JPA** (*Jakarta Persistence API*, formerly Java Persistence API) is a standard Java specification that defines how Java objects (Entities) are mapped to relational database tables (ORM - Object-Relational Mapping). JPA is just an **interface/specification**, not an implementation.

---

### Q9: What is Hibernate?
**Answer**:
**Hibernate** is an open-source ORM tool that serves as the actual **implementation provider** of the JPA specification. Hibernate handles SQL generation, connection pooling, dirty checking, caching, and entity lifecycle state transitions under the hood.

---

### Q10: What is the difference between JPA and Hibernate?
**Answer**:
- **JPA is the standard blueprint/contract** (interfaces, annotations like `@Entity`, `@Id`, `@ManyToOne`).
- **Hibernate is the engine/implementation** that reads those annotations and executes the actual SQL statements against MySQL.

---

### Q11: What is `JpaRepository`?
**Answer**:
`JpaRepository<T, ID>` is a Spring Data JPA interface that provides ready-to-use CRUD (Create, Read, Update, Delete) and pagination methods (`save()`, `findById()`, `findAll()`, `delete()`, `existsById()`) out of the box without writing any SQL queries. It also automatically derives custom SQL queries based on method names (e.g., `findByEmail()`, `findByStatus()`).

---

### Q12: Why do we use MySQL?
**Answer**:
MySQL is a robust, reliable, open-source Relational Database Management System (RDBMS). It enforces data integrity via ACID properties, supports primary/foreign keys, unique constraints, and integrates seamlessly with Spring Data JPA.

---

## 4. Spring Annotations Explained

### Q13: Core Stereotype Annotations
- **`@RestController`**: Combines `@Controller` and `@ResponseBody`. Tells Spring this class handles HTTP REST requests and automatically serializes return values directly into JSON format.
- **`@Service`**: Marks a class as a service component in the business logic layer.
- **`@Repository`**: Marks a class as a Data Access Object (DAO) for database operations and translates database exceptions into Spring's DataAccessException hierarchy.
- **`@SpringBootApplication`**: Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

---

### Q14: JPA Entity Annotations
- **`@Entity`**: Marks a Java class as a persistent database entity managed by Hibernate.
- **`@Table(name = "users")`**: Specifies the database table name. In our project, `users` is used because `user` is a reserved keyword in many SQL dialects.
- **`@Id`**: Denotes the primary key property of the entity.
- **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Instructs MySQL to auto-increment the primary key column.
- **`@Column(nullable = false, unique = true)`**: Defines column constraints such as NOT NULL and UNIQUE.
- **`@PrePersist` & `@PreUpdate`**: Entity lifecycle callback annotations that trigger methods right before the entity is saved (`INSERT`) or updated (`UPDATE`).

---

## 5. DTOs & Validation

### Q15: What is a DTO (Data Transfer Object)?
**Answer**:
A DTO is a plain Java class designed specifically to carry data between the client (API consumer) and the server without containing any business logic.

---

### Q16: Why don't we return the Entity directly in API responses?
**Answer**:
Returning entities directly causes serious software design and security flaws:
1. **Security / Sensitive Data Exposure**: Entities contain internal fields like `password` or internal audit flags that should never be sent to the client.
2. **Circular / Infinite JSON Recursion**: Bidirectional JPA relationships (e.g., `Ticket` has `Comment`, and `Comment` references `Ticket`) cause infinite serialization loops and `StackOverflowError`.
3. **Decoupling**: Decouples database schema changes from external API contracts.
4. **Tailored Payloads**: DTOs allow returning convenient flattened fields (e.g., `createdById`, `createdByName`, `assignedToName`) directly.

---

### Q17: What is `@Valid` and Jakarta Validation?
**Answer**:
- `@Valid` triggers automatic validation of incoming request body DTOs before the controller method executes.
- **Validation Annotations**:
  - `@NotBlank`: Checks that a String is not null and trimmed length is greater than 0.
  - `@NotNull`: Checks that an Object/Enum/Long is not null.
  - `@Email`: Validates that the String matches a valid email format.
  - `@Size(min = 6)`: Enforces string length boundaries (e.g., minimum password length).

---

## 6. Enums & Persistence

### Q18: What is an Enum in Java?
**Answer**:
An `enum` (enumeration) is a special Java type that represents a fixed set of predefined constants (e.g., `Role`, `TicketStatus`, `Priority`). It provides strong compile-time type safety.

---

### Q19: Why use `@Enumerated(EnumType.STRING)` instead of default `ORDINAL`?
**Answer**:
- By default, JPA stores enums as integer ordinals (`0, 1, 2...`). If the order of enum constants in code changes or a new constant is inserted in between, existing database records will become corrupted.
- `@Enumerated(EnumType.STRING)` stores the enum as a human-readable text string (`'OPEN'`, `'IN_PROGRESS'`, `'CLOSED'`), making the database self-documenting and safe against code refactoring.

---

## 7. Database Relationships in this Project

```
     ┌───────────┐ 1           * ┌────────────┐ 1           * ┌─────────────┐
     │   User    │───────────────│   Ticket   │───────────────│   Comment   │
     │  (users)  │ (CreatedBy)   │  (tickets) │ (Contains)    │  (comments) │
     └─────┬─────┘               └────────────┘               └─────────────┘
           │ 1                         ▲
           │                           │ * (AssignedTo)
           └───────────────────────────┘
```

### Q20: What is `@ManyToOne` and `@OneToMany`?
**Answer**:
- **`@ManyToOne`**: Many child records reference one parent record. (e.g., Many `Ticket` records belong to one `createdBy` User). In the database, the child table contains the Foreign Key column (`created_by_user_id`).
- **`@OneToMany`**: One parent record contains a collection of child records. (e.g., One `Ticket` has a list of `comments`).

---

### Q21: Explain the relationships in the Help Desk project:
1. **User ➔ Ticket (Creator)**: `@ManyToOne` on `Ticket.createdBy`. One user can create multiple tickets. Foreign Key: `created_by_user_id`.
2. **Agent (User) ➔ Ticket (Assignee)**: `@ManyToOne` on `Ticket.assignedTo`. One support agent can be assigned multiple tickets. Nullable when ticket is new. Foreign Key: `assigned_to_user_id`.
3. **Ticket ➔ Comment**: `@OneToMany(mappedBy = "ticket")` on Ticket and `@ManyToOne` on `Comment.ticket`. One ticket can have multiple discussion comments. Foreign Key: `ticket_id`.
4. **User ➔ Comment**: `@ManyToOne` on `Comment.user`. One user can write multiple comments across various tickets. Foreign Key: `user_id`.

---

## 8. Exception Handling & Status Codes

### Q22: How does Global Exception Handling work in Spring Boot?
**Answer**:
- **`@RestControllerAdvice`**: An interceptor that catches exceptions thrown by any controller in the application.
- **`@ExceptionHandler(ExceptionType.class)`**: Defines a specific method to handle a particular exception type and format a structured `ErrorResponse` JSON object with the appropriate HTTP status code.

### Q23: Custom Exceptions in this Project:
1. **`ResourceNotFoundException`**: Thrown when a User ID, Ticket ID, or Comment ID does not exist in the database ➔ returns `404 NOT FOUND`.
2. **`InvalidOperationException`**: Thrown when a business rule is violated (e.g., assigning a ticket to a user whose role is not `AGENT`, duplicate email, or invalid ticket status transition) ➔ returns `400 BAD REQUEST`.
3. **`MethodArgumentNotValidException`**: Caught when `@Valid` fails on incoming request DTOs ➔ returns `400 BAD REQUEST` with a map of field-level validation errors.

---

## 9. Architecture & Workflow

### Q24: Explain the complete end-to-end request flow:
**Example**: User posts a new ticket (`POST /api/tickets`):
1. **Postman / Client** sends an HTTP POST request with JSON payload to `http://localhost:8080/api/tickets`.
2. **`TicketController`**: Intercepts the request, validates the `TicketRequestDTO` using `@Valid`.
3. **`TicketService`**:
   - Executes business logic: verifies the creator user ID exists in MySQL.
   - Sets the default status to `OPEN` and `assignedTo` to `null`.
   - Converts the DTO into a `Ticket` entity.
4. **`TicketRepository`**: Uses Spring Data JPA to invoke `save(ticket)`.
5. **Hibernate**: Translates the Java object into an SQL `INSERT INTO tickets ...` statement.
6. **MySQL Database**: Inserts the row into `tickets` table and returns generated ID.
7. **`TicketService`**: Converts the saved `Ticket` entity into a `TicketResponseDTO`.
8. **`TicketController`**: Wraps the DTO in a `ResponseEntity` with status `201 CREATED` and sends JSON back to Postman.
