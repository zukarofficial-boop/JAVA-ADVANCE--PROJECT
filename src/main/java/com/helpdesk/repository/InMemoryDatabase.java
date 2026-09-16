package com.helpdesk.repository;
import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * In-Memory Data Store utilizing Java Collections:
 * - HashMap for O(1) key-value lookups by ID.
 * - HashSet for uniqueness checking of email addresses.
 * - ArrayLists inside entities for ordered audit logs and comment threads.
 */

public class InMemoryDatabase {
    private static InMemoryDatabase instance;
    private final Map<Integer, User> userMap;
    private final Map<Integer, Agent> agentMap;
    private final Map<Integer, Ticket> ticketMap;
    private final Set<String> emailSet;
    private final AtomicInteger userIdCounter;
    private final AtomicInteger ticketIdCounter;
    private final AtomicInteger commentIdCounter;
    private InMemoryDatabase() {
        this.userMap = new HashMap<>();
        this.agentMap = new HashMap<>();
        this.ticketMap = new HashMap<>();
        this.emailSet = new HashSet<>();
        this.userIdCounter = new AtomicInteger(1);
        this.ticketIdCounter = new AtomicInteger(1);
        this.commentIdCounter = new AtomicInteger(1);
        preloadSampleData();
    }
    public static synchronized InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }
    public int nextUserId() {
        return userIdCounter.getAndIncrement();
    }
    public int nextTicketId() {
        return ticketIdCounter.getAndIncrement();
    }
    public int nextCommentId() {
        return commentIdCounter.getAndIncrement();
    }
    public Map<Integer, User> getUsers() {
        return userMap;
    }
    public Map<Integer, Agent> getAgents() {
        return agentMap;
    }
    public Map<Integer, Ticket> getTickets() {
        return ticketMap;
    }
    public Set<String> getEmailSet() {
        return emailSet;
    }
    /**
     * Seeds initial realistic demo data so the user can immediately test searching, filtering, and assignment.
     */

    private void preloadSampleData() {
        // Preload Customers
        Customer c1 = new Customer(nextUserId(), "Alice Johnson", "alice@company.com", "Engineering", "+1-555-0101");
        Customer c2 = new Customer(nextUserId(), "Bob Williams", "bob@company.com", "Finance", "+1-555-0102");
        userMap.put(c1.getId(), c1);
        userMap.put(c2.getId(), c2);
        emailSet.add(c1.getEmail().toLowerCase());
        emailSet.add(c2.getEmail().toLowerCase());
        // Preload Agents
        Agent a1 = new Agent(nextUserId(), "Sarah Connor", "sarah@helpdesk.com", "Network & VPN Systems");
        Agent a2 = new Agent(nextUserId(), "David Miller", "david@helpdesk.com", "Database & Infrastructure");
        userMap.put(a1.getId(), a1);
        userMap.put(a2.getId(), a2);
        agentMap.put(a1.getId(), a1);
        agentMap.put(a2.getId(), a2);
        emailSet.add(a1.getEmail().toLowerCase());
        emailSet.add(a2.getEmail().toLowerCase());
        // Preload Sample Tickets
        // Ticket 1: In Progress
        Ticket t1 = new Ticket(nextTicketId(), "Cannot connect to Cisco AnyConnect VPN",
                "Receiving gateway timeout error 504 when attempting to connect from home.",
                TicketPriority.URGENT, c1);
        t1.setAssignedAgent(a1);
        t1.setStatus(TicketStatus.IN_PROGRESS);
        t1.addHistory(new TicketHistory(TicketStatus.OPEN, TicketStatus.ASSIGNED, a1, "Auto-assigned on creation"));
        t1.addHistory(new TicketHistory(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, a1, "Diagnosing network firewall routing"));
        t1.addComment(new Comment(nextCommentId(), "Investigating firewall gateway logs for your IP.", a1));
        t1.addComment(new Comment(nextCommentId(), "Thanks Sarah, let me know if you need traceroute output.", c1));
        a1.assignTicket(t1);
        c1.addCreatedTicket(t1);
        ticketMap.put(t1.getId(), t1);
        // Ticket 2: Open
        Ticket t2 = new Ticket(nextTicketId(), "September billing invoice tax discrepancy",
                "Invoice #INV-2026-904 shows 28% tax instead of 18% enterprise tier rate.",
                TicketPriority.HIGH, c2);
        c2.addCreatedTicket(t2);
        ticketMap.put(t2.getId(), t2);
        // Ticket 3: Resolved
        Ticket t3 = new Ticket(nextTicketId(), "Password reset verification email not arriving",
                "Requested password reset link 3 times but no email received in inbox or spam.",
                TicketPriority.MEDIUM, c1);
        t3.setAssignedAgent(a2);
        t3.setStatus(TicketStatus.RESOLVED);
        t3.addHistory(new TicketHistory(TicketStatus.OPEN, TicketStatus.ASSIGNED, a2, "Assigned to David"));
        t3.addHistory(new TicketHistory(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, a2, "Checking SMTP mail queue"));
        t3.addHistory(new TicketHistory(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, a2, "Resent activation token manually"));
        t3.setResolution(new Resolution("Flushed SMTP queue and resent token directly to user.", a2));
        t3.addComment(new Comment(nextCommentId(), "I have flushed the queue and resent your token.", a2));
        t3.addComment(new Comment(nextCommentId(), "Received and reset successfully! Thank you!", c1));
        a2.assignTicket(t3);
        c1.addCreatedTicket(t3);
        ticketMap.put(t3.getId(), t3);
        // Ticket 4: Closed
        Ticket t4 = new Ticket(nextTicketId(), "Request for second monitor on Desk 4B",
                "Need an additional HDMI monitor for multi-screen data analytics.",
                TicketPriority.LOW, c2);
        t4.setAssignedAgent(a1);
        t4.setStatus(TicketStatus.CLOSED);
        t4.addHistory(new TicketHistory(TicketStatus.OPEN, TicketStatus.ASSIGNED, a1, "Assigned to Sarah"));
        t4.addHistory(new TicketHistory(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS, a1, "Dispatched hardware from IT storage"));
        t4.addHistory(new TicketHistory(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, a1, "Monitor delivered and installed"));
        t4.addHistory(new TicketHistory(TicketStatus.RESOLVED, TicketStatus.CLOSED, c2, "Confirmed working, ticket closed"));
        t4.setResolution(new Resolution("Delivered and set up Dell 27-inch 4K monitor.", a1));
        a1.assignTicket(t4);
        c2.addCreatedTicket(t4);
        ticketMap.put(t4.getId(), t4);
    }
}
