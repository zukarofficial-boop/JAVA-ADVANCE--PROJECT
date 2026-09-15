package com.helpdesk.dto;

import com.helpdesk.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating a Ticket's status.
 */
public class TicketStatusUpdateDTO {

    @NotNull(message = "Status must not be null (OPEN, IN_PROGRESS, RESOLVED, CLOSED)")
    private TicketStatus status;

    public TicketStatusUpdateDTO() {
    }

    public TicketStatusUpdateDTO(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
