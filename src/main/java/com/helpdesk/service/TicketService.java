package com.helpdesk.service;

import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.exception.AgentNotFoundException;
import com.helpdesk.exception.InvalidStatusTransitionException;
import com.helpdesk.exception.InvalidTicketException;
import com.helpdesk.exception.TicketNotFoundException;
import com.helpdesk.model.*;
import com.helpdesk.repository.InMemoryDatabase;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing Ticket lifecycle, status workflow validation, and Streams-based queries.
 */
public class TicketService {
    private final InMemoryDatabase db;
    private final UserService userService;
    private final AgentService agentService;

    public TicketService(UserService userService, AgentService agentService) {
        this.db = InMemoryDatabase.getInstance();
        this.userService = userService;
        this.agentService = agentService;
    }

    /**
     * Creates a new support ticket.
     * Status is initialized to OPEN.
     */
    public Ticket createTicket(String title, String description, TicketPriority priority, int customerId) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidTicketException("Ticket title cannot be blank!");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidTicketException("Ticket description cannot be blank!");
        }

        Customer customer = userService.getCustomerById(customerId);
        int id = db.nextTicketId();
        Ticket ticket = new Ticket(id, title.trim(), description.trim(), priority, customer);
        
        customer.addCreatedTicket(ticket);
        db.getTickets().put(id, ticket);
        return ticket;
    }

    public Ticket getTicketById(int id) {
        Ticket ticket = db.getTickets().get(id);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket not found with ID: " + id);
        }
        return ticket;
    }

    public Collection<Ticket> getAllTickets() {
        return db.getTickets().values();
    }

    /**
     * Assigns a ticket to a support agent.
     * Automatically transitions status from OPEN to ASSIGNED.
     */
    public Ticket assignTicket(int ticketId, int agentId, User assignedBy) {
        Ticket ticket = getTicketById(ticketId);
        Agent agent = agentService.getAgentById(agentId);

        if (!agent.isAvailable()) {
            throw new AgentNotFoundException("Agent " + agent.getName() + " is currently marked as unavailable/offline.");
        }

        Agent previousAgent = ticket.getAssignedAgent();
        if (previousAgent != null) {
            previousAgent.removeAssignedTicket(ticket);
        }

        ticket.setAssignedAgent(agent);
        agent.assignTicket(ticket);

        TicketStatus oldStatus = ticket.getStatus();
        if (oldStatus == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.ASSIGNED);
            ticket.addHistory(new TicketHistory(oldStatus, TicketStatus.ASSIGNED, assignedBy,
                    "Assigned to agent: " + agent.getName()));
        } else {
            ticket.addHistory(new TicketHistory(oldStatus, oldStatus, assignedBy,
                    "Reassigned to agent: " + agent.getName()));
        }

        return ticket;
    }

    /**
     * Enforces the status workflow:
     * OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
     */
    public Ticket updateTicketStatus(int ticketId, TicketStatus newStatus, User changedBy, String remark) {
        Ticket ticket = getTicketById(ticketId);
        TicketStatus currentStatus = ticket.getStatus();

        if (currentStatus == newStatus) {
            return ticket; // No change
        }

        validateStatusTransition(ticket, currentStatus, newStatus);

        ticket.setStatus(newStatus);
        ticket.addHistory(new TicketHistory(currentStatus, newStatus, changedBy, remark));
        return ticket;
    }

    /**
     * Validates permissible status transitions.
     */
    private void validateStatusTransition(Ticket ticket, TicketStatus current, TicketStatus next) {
        boolean valid = switch (current) {
            case OPEN -> (next == TicketStatus.ASSIGNED || next == TicketStatus.IN_PROGRESS);
            case ASSIGNED -> (next == TicketStatus.IN_PROGRESS || next == TicketStatus.RESOLVED);
            case IN_PROGRESS -> (next == TicketStatus.RESOLVED);
            case RESOLVED -> (next == TicketStatus.CLOSED || next == TicketStatus.IN_PROGRESS);
            case CLOSED -> false; // Closed tickets cannot be altered
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(String.format(
                    "Invalid status transition: Cannot change ticket #%d from %s to %s. Expected sequential lifecycle: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED",
                    ticket.getId(), current, next));
        }

        if (next == TicketStatus.RESOLVED && ticket.getResolution() == null) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark ticket as RESOLVED without providing resolution details! Please use the 'Resolve Ticket' option.");
        }
    }

    /**
     * Resolves a ticket by recording resolution details and setting status to RESOLVED.
     */
    public Ticket resolveTicket(int ticketId, String resolutionNotes, Agent agent) {
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new InvalidStatusTransitionException("Cannot resolve a ticket that is already CLOSED.");
        }

        Resolution resolution = new Resolution(resolutionNotes, agent);
        ticket.setResolution(resolution);

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.addHistory(new TicketHistory(oldStatus, TicketStatus.RESOLVED, agent,
                "Resolved: " + resolutionNotes));

        return ticket;
    }

    /**
     * Closes a resolved ticket.
     */
    public Ticket closeTicket(int ticketId, User user, String closureNote) {
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidStatusTransitionException(
                    "Only tickets in RESOLVED status can be CLOSED. Current status is " + ticket.getStatus());
        }

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.addHistory(new TicketHistory(oldStatus, TicketStatus.CLOSED, user,
                closureNote != null ? closureNote : "Confirmed resolution and closed"));

        return ticket;
    }

    /**
     * Adds a discussion comment to a ticket.
     */
    public Comment addComment(int ticketId, String message, User author) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidTicketException("Comment message cannot be empty!");
        }

        Ticket ticket = getTicketById(ticketId);
        int commentId = db.nextCommentId();
        Comment comment = new Comment(commentId, message.trim(), author);
        ticket.addComment(comment);
        return comment;
    }

    // ==========================================
    // Java Streams API Operations
    // ==========================================

    /**
     * Search tickets by keyword in title or description.
     */
    public List<Ticket> searchByKeyword(String keyword) {
        String query = keyword.toLowerCase();
        return db.getTickets().values().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(query) ||
                             t.getDescription().toLowerCase().contains(query))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filter tickets by TicketStatus.
     */
    public List<Ticket> filterByStatus(TicketStatus status) {
        return db.getTickets().values().stream()
                .filter(t -> t.getStatus() == status)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filter tickets by TicketPriority.
     */
    public List<Ticket> filterByPriority(TicketPriority priority) {
        return db.getTickets().values().stream()
                .filter(t -> t.getPriority() == priority)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filter tickets assigned to a specific agent.
     */
    public List<Ticket> filterByAgent(int agentId) {
        return db.getTickets().values().stream()
                .filter(t -> t.getAssignedAgent() != null && t.getAssignedAgent().getId() == agentId)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filter tickets created by a specific customer.
     */
    public List<Ticket> filterByCustomer(int customerId) {
        return db.getTickets().values().stream()
                .filter(t -> t.getCreatedBy().getId() == customerId)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Group tickets by status and count occurrences.
     */
    public Map<TicketStatus, Long> getTicketCountByStatus() {
        return db.getTickets().values().stream()
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));
    }

    /**
     * Counts active unresolved tickets (OPEN, ASSIGNED, IN_PROGRESS).
     */
    public long countActiveTickets() {
        return db.getTickets().values().stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN ||
                             t.getStatus() == TicketStatus.ASSIGNED ||
                             t.getStatus() == TicketStatus.IN_PROGRESS)
                .count();
    }
}
