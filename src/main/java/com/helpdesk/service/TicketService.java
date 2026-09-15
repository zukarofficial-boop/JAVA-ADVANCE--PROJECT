package com.helpdesk.service;

import com.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.dto.TicketResponseDTO;
import com.helpdesk.dto.TicketStatusUpdateDTO;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.enums.Priority;
import com.helpdesk.enums.Role;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.exception.InvalidOperationException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling all business logic related to Tickets.
 */
@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    // Constructor-based Dependency Injection
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new ticket.
     * Default status is set to OPEN, and assignedTo is initially null.
     */
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        User creator = userRepository.findById(dto.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getCreatedByUserId()));

        Ticket ticket = new Ticket(
                dto.getTitle(),
                dto.getDescription(),
                dto.getPriority(),
                creator
        );
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedTo(null);

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(savedTicket);
    }

    /**
     * Retrieves all tickets.
     */
    public List<TicketResponseDTO> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        List<TicketResponseDTO> responseList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responseList.add(mapToTicketResponseDTO(ticket));
        }
        return responseList;
    }

    /**
     * Retrieves a single ticket by its ID.
     */
    public TicketResponseDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToTicketResponseDTO(ticket);
    }

    /**
     * Updates an existing ticket's title, description, and priority.
     * Preserves creation timestamp, creator, and assigned agent.
     */
    public TicketResponseDTO updateTicket(Long id, TicketRequestDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(updatedTicket);
    }

    /**
     * Deletes a ticket by ID.
     */
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(ticket);
    }

    /**
     * Assigns a ticket to a support agent.
     * Validates that the assigned user exists and has the role AGENT.
     */
    public TicketResponseDTO assignTicket(Long ticketId, Long agentId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + agentId));

        if (agent.getRole() != Role.AGENT) {
            throw new InvalidOperationException(
                    "Cannot assign ticket. User with id " + agentId + " is not an AGENT (Current role: " + agent.getRole() + ")"
            );
        }

        ticket.setAssignedTo(agent);
        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(savedTicket);
    }

    /**
     * Updates the status of a ticket with sensible business validation.
     */
    public TicketResponseDTO updateTicketStatus(Long ticketId, TicketStatusUpdateDTO dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        TicketStatus currentStatus = ticket.getStatus();
        TicketStatus newStatus = dto.getStatus();

        // Business Rule: A CLOSED ticket cannot be transitioned directly back to OPEN
        if (currentStatus == TicketStatus.CLOSED && newStatus == TicketStatus.OPEN) {
            throw new InvalidOperationException("A CLOSED ticket cannot be directly transitioned back to OPEN.");
        }

        ticket.setStatus(newStatus);
        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToTicketResponseDTO(savedTicket);
    }

    /**
     * Filters tickets by status.
     */
    public List<TicketResponseDTO> getTicketsByStatus(TicketStatus status) {
        List<Ticket> tickets = ticketRepository.findByStatus(status);
        List<TicketResponseDTO> responseList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responseList.add(mapToTicketResponseDTO(ticket));
        }
        return responseList;
    }

    /**
     * Filters tickets by priority.
     */
    public List<TicketResponseDTO> getTicketsByPriority(Priority priority) {
        List<Ticket> tickets = ticketRepository.findByPriority(priority);
        List<TicketResponseDTO> responseList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responseList.add(mapToTicketResponseDTO(ticket));
        }
        return responseList;
    }

    /**
     * Filters tickets created by a specific user.
     */
    public List<TicketResponseDTO> getTicketsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<Ticket> tickets = ticketRepository.findByCreatedById(userId);
        List<TicketResponseDTO> responseList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responseList.add(mapToTicketResponseDTO(ticket));
        }
        return responseList;
    }

    /**
     * Filters tickets assigned to a specific agent.
     */
    public List<TicketResponseDTO> getTicketsByAgent(Long agentId) {
        if (!userRepository.existsById(agentId)) {
            throw new ResourceNotFoundException("Agent not found with id: " + agentId);
        }
        List<Ticket> tickets = ticketRepository.findByAssignedToId(agentId);
        List<TicketResponseDTO> responseList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responseList.add(mapToTicketResponseDTO(ticket));
        }
        return responseList;
    }

    /**
     * Helper method to fetch the Ticket entity internally for comments.
     */
    public Ticket findTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    /**
     * Helper method to convert a Ticket entity to a TicketResponseDTO.
     */
    private TicketResponseDTO mapToTicketResponseDTO(Ticket ticket) {
        Long createdById = ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null;
        String createdByName = ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : null;

        Long assignedToId = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null;
        String assignedToName = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : null;

        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                createdById,
                createdByName,
                assignedToId,
                assignedToName
        );
    }
}
