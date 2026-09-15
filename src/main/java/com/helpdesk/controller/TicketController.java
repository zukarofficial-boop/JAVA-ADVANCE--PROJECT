package com.helpdesk.controller;

import com.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.dto.TicketResponseDTO;
import com.helpdesk.dto.TicketStatusUpdateDTO;
import com.helpdesk.enums.Priority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing endpoints for Ticket management.
 * Base URL: /api/tickets
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    // Constructor-based Dependency Injection
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Create a new ticket.
     * POST /api/tickets
     * Returns HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO createdTicket = ticketService.createTicket(dto);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    /**
     * Get all tickets.
     * GET /api/tickets
     * Returns HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get a ticket by ID.
     * GET /api/tickets/{id}
     * Returns HTTP 200 OK or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Update an existing ticket (title, description, priority).
     * PUT /api/tickets/{id}
     * Returns HTTP 200 OK or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable Long id,
                                                          @Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO updatedTicket = ticketService.updateTicket(id, dto);
        return ResponseEntity.ok(updatedTicket);
    }

    /**
     * Delete a ticket by ID.
     * DELETE /api/tickets/{id}
     * Returns HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign a ticket to a support agent.
     * PUT /api/tickets/{ticketId}/assign/{agentId}
     * Returns HTTP 200 OK.
     */
    @PutMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<TicketResponseDTO> assignTicket(@PathVariable Long ticketId,
                                                          @PathVariable Long agentId) {
        TicketResponseDTO assignedTicket = ticketService.assignTicket(ticketId, agentId);
        return ResponseEntity.ok(assignedTicket);
    }

    /**
     * Update a ticket's lifecycle status.
     * PUT /api/tickets/{ticketId}/status
     * Returns HTTP 200 OK.
     */
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(@PathVariable Long ticketId,
                                                                @Valid @RequestBody TicketStatusUpdateDTO dto) {
        TicketResponseDTO updatedTicket = ticketService.updateTicketStatus(ticketId, dto);
        return ResponseEntity.ok(updatedTicket);
    }

    /**
     * Filter tickets by status (OPEN, IN_PROGRESS, RESOLVED, CLOSED).
     * GET /api/tickets/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByStatus(@PathVariable TicketStatus status) {
        List<TicketResponseDTO> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Filter tickets by priority (LOW, MEDIUM, HIGH, CRITICAL).
     * GET /api/tickets/priority/{priority}
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByPriority(@PathVariable Priority priority) {
        List<TicketResponseDTO> tickets = ticketService.getTicketsByPriority(priority);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Filter tickets created by a specific user ID.
     * GET /api/tickets/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByUser(@PathVariable Long userId) {
        List<TicketResponseDTO> tickets = ticketService.getTicketsByUser(userId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Filter tickets assigned to a specific agent ID.
     * GET /api/tickets/agent/{agentId}
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByAgent(@PathVariable Long agentId) {
        List<TicketResponseDTO> tickets = ticketService.getTicketsByAgent(agentId);
        return ResponseEntity.ok(tickets);
    }
}
