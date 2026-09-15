package com.helpdesk.dto;

import com.helpdesk.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a Ticket.
 * Status is not required from client as it is automatically set to OPEN.
 */
public class TicketRequestDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Priority must not be null (LOW, MEDIUM, HIGH, CRITICAL)")
    private Priority priority;

    @NotNull(message = "createdByUserId must not be null")
    private Long createdByUserId;

    public TicketRequestDTO() {
    }

    public TicketRequestDTO(String title, String description, Priority priority, Long createdByUserId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.createdByUserId = createdByUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
