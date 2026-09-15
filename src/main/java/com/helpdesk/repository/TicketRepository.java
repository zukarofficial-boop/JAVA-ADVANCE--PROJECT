package com.helpdesk.repository;

import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.Priority;
import com.helpdesk.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for database operations on Ticket entities.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Finds all tickets matching the given status (OPEN, IN_PROGRESS, RESOLVED, CLOSED).
     */
    List<Ticket> findByStatus(TicketStatus status);

    /**
     * Finds all tickets matching the given priority (LOW, MEDIUM, HIGH, CRITICAL).
     */
    List<Ticket> findByPriority(Priority priority);

    /**
     * Finds all tickets created by a specific user ID.
     */
    List<Ticket> findByCreatedById(Long userId);

    /**
     * Finds all tickets assigned to a specific agent user ID.
     */
    List<Ticket> findByAssignedToId(Long agentId);
}
