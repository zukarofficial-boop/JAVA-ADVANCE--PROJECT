package com.helpdesk;

import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.exception.DuplicateUserException;
import com.helpdesk.exception.InvalidStatusTransitionException;
import com.helpdesk.model.Agent;
import com.helpdesk.model.Customer;
import com.helpdesk.model.Ticket;
import com.helpdesk.service.AgentService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Unit Tests for the Help Desk System.
 */
class HelpDeskTest {

    private UserService userService;
    private AgentService agentService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        agentService = new AgentService();
        ticketService = new TicketService(userService, agentService);
    }

    @Test
    @DisplayName("Should create ticket and set initial status to OPEN")
    void testTicketCreation() {
        Customer customer = userService.registerCustomer("Test User", "test.user@company.com", "IT", "1234567890");
        Ticket ticket = ticketService.createTicket("Printer Offline", "Floor 3 printer not responding", TicketPriority.HIGH, customer.getId());

        assertNotNull(ticket);
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals("Printer Offline", ticket.getTitle());
        assertEquals(customer.getId(), ticket.getCreatedBy().getId());
    }

    @Test
    @DisplayName("Should prevent duplicate user registration by email")
    void testDuplicateUserRegistration() {
        userService.registerCustomer("Alice", "alice.duplicate@test.com", "HR", "111");

        assertThrows(DuplicateUserException.class, () -> {
            userService.registerCustomer("Alice Copy", "alice.duplicate@test.com", "Sales", "222");
        });
    }

    @Test
    @DisplayName("Should allow valid sequential status transition: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED")
    void testValidStatusWorkflow() {
        Customer customer = userService.registerCustomer("Charlie", "charlie@workflow.com", "DevOps", "333");
        Agent agent = agentService.registerAgent("Agent Smith", "smith@workflow.com", "DevOps Systems");

        Ticket ticket = ticketService.createTicket("Server 500 Error", "API returning 500", TicketPriority.URGENT, customer.getId());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());

        // 1. Assign (OPEN -> ASSIGNED)
        ticketService.assignTicket(ticket.getId(), agent.getId(), customer);
        assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());

        // 2. Start Work (ASSIGNED -> IN_PROGRESS)
        ticketService.updateTicketStatus(ticket.getId(), TicketStatus.IN_PROGRESS, agent, "Started debugging logs");
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());

        // 3. Resolve (IN_PROGRESS -> RESOLVED)
        ticketService.resolveTicket(ticket.getId(), "Restarted crashed process and applied hotfix", agent);
        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
        assertNotNull(ticket.getResolution());

        // 4. Close (RESOLVED -> CLOSED)
        ticketService.closeTicket(ticket.getId(), customer, "Verified server is up");
        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    @DisplayName("Should reject invalid direct transition from OPEN to CLOSED")
    void testInvalidStatusTransition() {
        Customer customer = userService.registerCustomer("Diana", "diana@test.com", "Support", "444");
        Ticket ticket = ticketService.createTicket("Minor Glitch", "UI icon offset", TicketPriority.LOW, customer.getId());

        assertThrows(InvalidStatusTransitionException.class, () -> {
            ticketService.updateTicketStatus(ticket.getId(), TicketStatus.CLOSED, customer, "Direct closing");
        });
    }

    @Test
    @DisplayName("Should filter tickets by priority using Java Streams")
    void testStreamsFiltering() {
        Customer c = userService.registerCustomer("Evan", "evan@test.com", "QA", "555");
        ticketService.createTicket("Urgent Crash", "App crashes on launch", TicketPriority.URGENT, c.getId());

        List<Ticket> urgentTickets = ticketService.filterByPriority(TicketPriority.URGENT);
        assertFalse(urgentTickets.isEmpty());
        assertTrue(urgentTickets.stream().allMatch(t -> t.getPriority() == TicketPriority.URGENT));
    }
}
