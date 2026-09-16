package com.helpdesk;

import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.exception.*;
import com.helpdesk.model.*;
import com.helpdesk.service.AgentService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import com.helpdesk.util.InputValidator;

import java.util.*;

/**
 * Main interactive Console Application for Help Desk / Ticket Management System.
 */
public class Main {

    private final UserService userService;
    private final AgentService agentService;
    private final TicketService ticketService;
    private final Scanner scanner;

    public Main() {
        this.userService = new UserService();
        this.agentService = new AgentService();
        this.ticketService = new TicketService(userService, agentService);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            int choice = InputValidator.readInt(scanner, "Enter your choice [1-16]: ", 1, 16);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> registerAgent();
                    case 3 -> createTicket();
                    case 4 -> viewAllTickets();
                    case 5 -> viewTicketById();
                    case 6 -> assignTicket();
                    case 7 -> updateTicketStatus();
                    case 8 -> addComment();
                    case 9 -> searchTickets();
                    case 10 -> filterTickets();
                    case 11 -> viewTicketHistory();
                    case 12 -> resolveTicket();
                    case 13 -> closeTicket();
                    case 14 -> viewStatistics();
                    case 15 -> listUsersAndAgents();
                    case 16 -> {
                        System.out.println("=================================================");
                        System.out.println("  Thank you for using Help Desk Management CLI!  ");
                        System.out.println("=================================================");
                        running = false;
                    }
                }
            } catch (UserNotFoundException | TicketNotFoundException | AgentNotFoundException |
                     InvalidStatusTransitionException | DuplicateUserException | InvalidTicketException e) {
                System.out.println("\n [ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n [UNEXPECTED ERROR] " + e.getMessage());
            }

            if (running) {
                InputValidator.pressEnterToContinue(scanner);
            }
        }
    }

    private void printBanner() {
        System.out.println("=========================================================================");
        System.out.println("       HELP DESK & TICKET MANAGEMENT SYSTEM (Java 26 Console)            ");
        System.out.println("=========================================================================");
        System.out.println(" * Features: OOP, Collections, Streams API, Custom Exceptions           ");
        System.out.println(" * Status Lifecycle: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED");
        System.out.println("=========================================================================\n");
    }

    private void printMenu() {
        System.out.println("\n----------------------------- MAIN MENU -----------------------------");
        System.out.println("  [1] Register Customer (User)       [9]  Search Tickets by Keyword");
        System.out.println("  [2] Register Support Agent         [10] Filter Tickets (Status/Priority/Agent)");
        System.out.println("  [3] Create Support Ticket          [11] View Ticket Audit History");
        System.out.println("  [4] View All Tickets               [12] Resolve Ticket (Add Resolution)");
        System.out.println("  [5] View Ticket Details by ID      [13] Close Resolved Ticket");
        System.out.println("  [6] Assign Ticket to Agent         [14] System Statistics & Metrics");
        System.out.println("  [7] Update Ticket Status           [15] List All Registered Users");
        System.out.println("  [8] Add Comment to Ticket          [16] Exit");
        System.out.println("---------------------------------------------------------------------");
    }

    // 1. Register Customer
    private void registerUser() {
        System.out.println("--- Register Customer ---");
        String name = InputValidator.readNonEmptyString(scanner, "Enter Name: ");
        String email = InputValidator.readEmail(scanner, "Enter Email: ");
        String dept = InputValidator.readNonEmptyString(scanner, "Enter Department: ");
        String phone = InputValidator.readNonEmptyString(scanner, "Enter Phone Number: ");

        Customer c = userService.registerCustomer(name, email, dept, phone);
        System.out.printf("%n [SUCCESS] Customer registered successfully! ID: #%d (%s)%n", c.getId(), c.getName());
    }

    // 2. Register Agent
    private void registerAgent() {
        System.out.println("--- Register Support Agent ---");
        String name = InputValidator.readNonEmptyString(scanner, "Enter Agent Name: ");
        String email = InputValidator.readEmail(scanner, "Enter Email: ");
        String spec = InputValidator.readNonEmptyString(scanner, "Enter Technical Specialization: ");

        Agent a = agentService.registerAgent(name, email, spec);
        System.out.printf("%n [SUCCESS] Support Agent registered successfully! ID: #%d (%s)%n", a.getId(), a.getName());
    }

    // 3. Create Ticket
    private void createTicket() {
        System.out.println("--- Create Support Ticket ---");
        listCustomersBrief();
        int customerId = InputValidator.readInt(scanner, "Enter Customer ID creating this ticket: ", 1, Integer.MAX_VALUE);
        String title = InputValidator.readNonEmptyString(scanner, "Enter Ticket Title: ");
        String desc = InputValidator.readNonEmptyString(scanner, "Enter Issue Description: ");

        System.out.println("Select Priority: [1] LOW  [2] MEDIUM  [3] HIGH  [4] URGENT");
        int pChoice = InputValidator.readInt(scanner, "Choose Priority [1-4]: ", 1, 4);
        TicketPriority priority = switch (pChoice) {
            case 1 -> TicketPriority.LOW;
            case 2 -> TicketPriority.MEDIUM;
            case 3 -> TicketPriority.HIGH;
            default -> TicketPriority.URGENT;
        };

        Ticket ticket = ticketService.createTicket(title, desc, priority, customerId);
        System.out.printf("%n [SUCCESS] Ticket created successfully!%n");
        System.out.println("  " + ticket);
    }

    // 4. View All Tickets
    private void viewAllTickets() {
        System.out.println("--- All Support Tickets ---");
        Collection<Ticket> tickets = ticketService.getAllTickets();
        if (tickets.isEmpty()) {
            System.out.println("  No tickets found.");
            return;
        }
        printTicketsTable(tickets);
    }

    // 5. View Ticket by ID
    private void viewTicketById() {
        int id = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        Ticket t = ticketService.getTicketById(id);

        System.out.println("\n=========================== TICKET #" + t.getId() + " ===========================");
        System.out.println(" Title:       " + t.getTitle());
        System.out.println(" Priority:    " + t.getPriority() + " (" + t.getPriority().getDescription() + ")");
        System.out.println(" Status:      " + t.getStatus() + " (" + t.getStatus().getDescription() + ")");
        System.out.println(" Created By:  " + t.getCreatedBy().getName() + " (" + t.getCreatedBy().getEmail() + ")");
        System.out.println(" Assigned To: " + (t.getAssignedAgent() != null ? t.getAssignedAgent().getName() + " [" + t.getAssignedAgent().getSpecialization() + "]" : "Unassigned"));
        System.out.println(" Created At:  " + t.getCreatedAt());
        System.out.println(" Updated At:  " + t.getUpdatedAt());
        System.out.println("\n Description:\n  " + t.getDescription());

        if (t.getResolution() != null) {
            System.out.println("\n Resolution Details:\n  " + t.getResolution());
        }

        System.out.println("\n Comments Thread (" + t.getComments().size() + "):");
        if (t.getComments().isEmpty()) {
            System.out.println("  (No comments yet)");
        } else {
            t.getComments().forEach(c -> System.out.println("  * " + c));
        }
        System.out.println("==================================================================");
    }

    // 6. Assign Ticket
    private void assignTicket() {
        System.out.println("--- Assign Ticket to Support Agent ---");
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        listAgentsBrief();
        int agentId = InputValidator.readInt(scanner, "Enter Agent ID: ", 1, Integer.MAX_VALUE);
        
        listUsersBrief();
        int actorId = InputValidator.readInt(scanner, "Enter Actor User ID (Admin/Agent performing action): ", 1, Integer.MAX_VALUE);
        User actor = userService.getUserById(actorId);

        Ticket updated = ticketService.assignTicket(ticketId, agentId, actor);
        System.out.printf("%n [SUCCESS] Ticket #%d assigned to %s (Status: %s)%n",
                updated.getId(), updated.getAssignedAgent().getName(), updated.getStatus());
    }

    // 7. Update Ticket Status
    private void updateTicketStatus() {
        System.out.println("--- Update Ticket Status ---");
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        Ticket t = ticketService.getTicketById(ticketId);
        System.out.printf("Current Status: %s%n", t.getStatus());

        System.out.println("Target Status: [1] OPEN  [2] ASSIGNED  [3] IN_PROGRESS  [4] RESOLVED  [5] CLOSED");
        int sChoice = InputValidator.readInt(scanner, "Choose Target Status [1-5]: ", 1, 5);
        TicketStatus newStatus = switch (sChoice) {
            case 1 -> TicketStatus.OPEN;
            case 2 -> TicketStatus.ASSIGNED;
            case 3 -> TicketStatus.IN_PROGRESS;
            case 4 -> TicketStatus.RESOLVED;
            default -> TicketStatus.CLOSED;
        };

        listUsersBrief();
        int actorId = InputValidator.readInt(scanner, "Enter Your User ID: ", 1, Integer.MAX_VALUE);
        User actor = userService.getUserById(actorId);
        String remark = InputValidator.readNonEmptyString(scanner, "Enter Transition Remark / Note: ");

        Ticket updated = ticketService.updateTicketStatus(ticketId, newStatus, actor, remark);
        System.out.printf("%n [SUCCESS] Ticket #%d status updated to %s%n", updated.getId(), updated.getStatus());
    }

    // 8. Add Comment
    private void addComment() {
        System.out.println("--- Add Comment to Ticket ---");
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        listUsersBrief();
        int authorId = InputValidator.readInt(scanner, "Enter Your User ID (Author): ", 1, Integer.MAX_VALUE);
        User author = userService.getUserById(authorId);
        String message = InputValidator.readNonEmptyString(scanner, "Enter Comment Message: ");

        Comment comment = ticketService.addComment(ticketId, message, author);
        System.out.printf("%n [SUCCESS] Comment posted!%n  %s%n", comment);
    }

    // 9. Search Tickets (Streams)
    private void searchTickets() {
        System.out.println("--- Search Tickets by Keyword ---");
        String keyword = InputValidator.readNonEmptyString(scanner, "Enter search keyword: ");
        List<Ticket> results = ticketService.searchByKeyword(keyword);

        System.out.printf("%nFound %d matching ticket(s):%n", results.size());
        printTicketsTable(results);
    }

    // 10. Filter Tickets (Streams)
    private void filterTickets() {
        System.out.println("--- Filter Tickets ---");
        System.out.println(" [1] Filter by Status");
        System.out.println(" [2] Filter by Priority");
        System.out.println(" [3] Filter by Assigned Agent");
        System.out.println(" [4] Filter by Customer Creator");
        int fChoice = InputValidator.readInt(scanner, "Choose filter type [1-4]: ", 1, 4);

        List<Ticket> results = switch (fChoice) {
            case 1 -> {
                System.out.println("Select Status: [1] OPEN  [2] ASSIGNED  [3] IN_PROGRESS  [4] RESOLVED  [5] CLOSED");
                int sc = InputValidator.readInt(scanner, "Choice: ", 1, 5);
                TicketStatus status = switch (sc) {
                    case 1 -> TicketStatus.OPEN;
                    case 2 -> TicketStatus.ASSIGNED;
                    case 3 -> TicketStatus.IN_PROGRESS;
                    case 4 -> TicketStatus.RESOLVED;
                    default -> TicketStatus.CLOSED;
                };
                yield ticketService.filterByStatus(status);
            }
            case 2 -> {
                System.out.println("Select Priority: [1] LOW  [2] MEDIUM  [3] HIGH  [4] URGENT");
                int pc = InputValidator.readInt(scanner, "Choice: ", 1, 4);
                TicketPriority p = switch (pc) {
                    case 1 -> TicketPriority.LOW;
                    case 2 -> TicketPriority.MEDIUM;
                    case 3 -> TicketPriority.HIGH;
                    default -> TicketPriority.URGENT;
                };
                yield ticketService.filterByPriority(p);
            }
            case 3 -> {
                listAgentsBrief();
                int agentId = InputValidator.readInt(scanner, "Enter Agent ID: ", 1, Integer.MAX_VALUE);
                yield ticketService.filterByAgent(agentId);
            }
            default -> {
                listCustomersBrief();
                int customerId = InputValidator.readInt(scanner, "Enter Customer ID: ", 1, Integer.MAX_VALUE);
                yield ticketService.filterByCustomer(customerId);
            }
        };

        System.out.printf("%nFiltered Result (%d tickets):%n", results.size());
        printTicketsTable(results);
    }

    // 11. View History
    private void viewTicketHistory() {
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        Ticket t = ticketService.getTicketById(ticketId);

        System.out.println("\n--- Audit History for Ticket #" + t.getId() + " (" + t.getTitle() + ") ---");
        List<TicketHistory> history = t.getHistory();
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("  %d. %s%n", (i + 1), history.get(i));
        }
    }

    // 12. Resolve Ticket
    private void resolveTicket() {
        System.out.println("--- Resolve Ticket ---");
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        listAgentsBrief();
        int agentId = InputValidator.readInt(scanner, "Enter Resolving Agent ID: ", 1, Integer.MAX_VALUE);
        Agent agent = agentService.getAgentById(agentId);
        String notes = InputValidator.readNonEmptyString(scanner, "Enter Resolution Summary & Root Cause: ");

        Ticket resolved = ticketService.resolveTicket(ticketId, notes, agent);
        System.out.printf("%n [SUCCESS] Ticket #%d marked as RESOLVED!%n", resolved.getId());
    }

    // 13. Close Ticket
    private void closeTicket() {
        System.out.println("--- Close Resolved Ticket ---");
        int ticketId = InputValidator.readInt(scanner, "Enter Ticket ID: ", 1, Integer.MAX_VALUE);
        listUsersBrief();
        int userId = InputValidator.readInt(scanner, "Enter Confirming User ID: ", 1, Integer.MAX_VALUE);
        User user = userService.getUserById(userId);
        String note = InputValidator.readNonEmptyString(scanner, "Enter Closure Confirmation Note: ");

        Ticket closed = ticketService.closeTicket(ticketId, user, note);
        System.out.printf("%n [SUCCESS] Ticket #%d officially CLOSED!%n", closed.getId());
    }

    // 14. System Statistics (Streams)
    private void viewStatistics() {
        System.out.println("==================== SYSTEM STATISTICS & METRICS ====================");
        Collection<Ticket> all = ticketService.getAllTickets();
        System.out.println(" Total Tickets Created:     " + all.size());
        System.out.println(" Active Unresolved Tickets: " + ticketService.countActiveTickets());

        System.out.println("\n Breakdown by Status (Java Streams Grouping):");
        Map<TicketStatus, Long> byStatus = ticketService.getTicketCountByStatus();
        for (TicketStatus status : TicketStatus.values()) {
            System.out.printf("   - %-12s: %d%n", status, byStatus.getOrDefault(status, 0L));
        }

        System.out.println("\n Support Agent Workload:");
        Optional<Agent> busiest = agentService.getBusiestAgent();
        agentService.getAllAgents().forEach(a -> {
            System.out.printf("   - Agent #%d %-18s | Spec: %-25s | Total Assigned: %d%n",
                    a.getId(), a.getName(), a.getSpecialization(), a.getAssignedTickets().size());
        });

        busiest.ifPresent(agent -> System.out.printf("%n ⭐ Busiest Support Agent: %s (%d tickets assigned)%n",
                agent.getName(), agent.getAssignedTickets().size()));
        System.out.println("=====================================================================");
    }

    // 15. List Users
    private void listUsersAndAgents() {
        System.out.println("======================= REGISTERED DIRECTORY =======================");
        System.out.println(" Customers (Users):");
        userService.getAllCustomers().forEach(c -> System.out.println("   * " + c.getDetails()));

        System.out.println("\n Support Agents:");
        agentService.getAllAgents().forEach(a -> System.out.println("   * " + a.getDetails()));
        System.out.println("====================================================================");
    }

    // Helpers
    private void printTicketsTable(Collection<Ticket> tickets) {
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-4s | %-10s | %-11s | %-16s | %-16s | %s%n",
                "ID", "Priority", "Status", "Created By", "Assigned To", "Title");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        for (Ticket t : tickets) {
            String agent = (t.getAssignedAgent() != null) ? t.getAssignedAgent().getName() : "Unassigned";
            System.out.printf(" #%-3d | %-10s | %-11s | %-16s | %-16s | %s%n",
                    t.getId(), t.getPriority(), t.getStatus(), t.getCreatedBy().getName(), agent, t.getTitle());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    private void listCustomersBrief() {
        System.out.println(" Available Customers:");
        userService.getAllCustomers().forEach(c ->
                System.out.printf("   [ID: %d] %s (%s - %s)%n", c.getId(), c.getName(), c.getDepartment(), c.getEmail()));
    }

    private void listAgentsBrief() {
        System.out.println(" Available Support Agents:");
        agentService.getAllAgents().forEach(a ->
                System.out.printf("   [ID: %d] %s (Spec: %s, %s)%n",
                        a.getId(), a.getName(), a.getSpecialization(), a.isAvailable() ? "Available" : "Offline"));
    }

    private void listUsersBrief() {
        System.out.println(" Users Directory:");
        userService.getAllUsers().forEach(u ->
                System.out.printf("   [ID: %d] %s (%s)%n", u.getId(), u.getName(), u.getRole()));
    }
}
