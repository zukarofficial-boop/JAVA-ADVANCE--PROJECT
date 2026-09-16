# Help Desk & Ticket Management System (Java 26 Console)

> A Pure Core Java 26 Object-Oriented Console Application featuring In-Memory Collections, Streams API, Custom Exception Handling, and an Audited Ticket Lifecycle Workflow.

---

## 📌 Project Overview

This project is a console-based **Help Desk / Ticket Management System** developed in **Java 26** without external frameworks or databases. It demonstrates:
* **Object-Oriented Programming (OOP):** Encapsulation, Inheritance, Polymorphism, Abstraction, and Composition.
* **Java Collections Framework:** `HashMap`, `HashSet`, `ArrayList` for in-memory persistence and $O(1)$ fast lookups.
* **Java Streams API:** Complex queries, filtering, sorting, and aggregate analytics.
* **Custom Exception Handling:** Domain-specific exceptions for invalid transitions and entity lookup failures.
* **Audited Ticket Lifecycle:** Complete transition audit logs (`TicketHistory`) and resolution tracking (`Resolution`).

---

## 🚀 How to Run the Application in Terminal

### Step 1: Set JAVA_HOME to Java 26
In your PowerShell terminal:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"
```

### Step 2: Run the Application
You can run it using either of the following commands:

#### Option A: Run Prebuilt Executable JAR (Instant)
```powershell
& "C:\Program Files\Java\jdk-26.0.2.1\bin\java.exe" -jar "target\helpdesk-ticket-system-1.0.0.jar"
```

#### Option B: Run via Maven Wrapper
```powershell
./mvnw.cmd compile exec:java
```

---

## 🎮 Interactive Console Menu Walkthrough

When you start the application, you will see the interactive menu:

```text
=========================================================================
       HELP DESK & TICKET MANAGEMENT SYSTEM (Java 26 Console)            
=========================================================================
 * Features: OOP, Collections, Streams API, Custom Exceptions           
 * Status Lifecycle: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
=========================================================================

----------------------------- MAIN MENU -----------------------------
  [1] Register Customer (User)       [9]  Search Tickets by Keyword
  [2] Register Support Agent         [10] Filter Tickets (Status/Priority/Agent)
  [3] Create Support Ticket          [11] View Ticket Audit History
  [4] View All Tickets               [12] Resolve Ticket (Add Resolution)
  [5] View Ticket Details by ID      [13] Close Resolved Ticket
  [6] Assign Ticket to Agent         [14] System Statistics & Metrics
  [7] Update Ticket Status           [15] List All Registered Users
  [8] Add Comment to Ticket          [16] Exit
---------------------------------------------------------------------
Enter your choice [1-16]:
```

---

## 🔍 How to Test the Key Features

| Step | Menu Option | Action to Perform |
| :---: | :---: | :--- |
| **1** | **`[4]` View All Tickets** | View the preloaded tickets in a clean formatted table. |
| **2** | **`[3]` Create Ticket** | Select Customer `1`, enter title & description, choose Priority (`URGENT`/`HIGH`). |
| **3** | **`[6]` Assign Ticket** | Enter your new Ticket ID and assign to Agent `3` (Sarah) or `4` (David). Status becomes `ASSIGNED`. |
| **4** | **`[8]` Add Comment** | Add diagnostic notes or customer inquiries to the ticket thread. |
| **5** | **`[12]` Resolve Ticket** | Attach resolution root-cause notes and mark status as `RESOLVED`. |
| **6** | **`[11]` View Audit History**| See the timestamped log of who changed the status and why. |
| **7** | **`[14]` System Statistics** | View Java Streams breakdown by status, active tickets, and busiest agent. |

---

## 🛡️ Status Workflow & Transition Rules

```
OPEN ──► ASSIGNED ──► IN_PROGRESS ──► RESOLVED ──► CLOSED
```

* **Valid:** `OPEN` $\rightarrow$ `ASSIGNED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` $\rightarrow$ `CLOSED`
* **Invalid:** Attempting `OPEN` $\rightarrow$ `CLOSED` or resolving without providing a `Resolution` triggers an `InvalidStatusTransitionException` without crashing the application.

---

## 🧪 Unit Tests

Run the JUnit 5 test suite:
```powershell
./mvnw.cmd test
```
Result: **5 Tests Passed (0 Failures, 0 Errors)**.
