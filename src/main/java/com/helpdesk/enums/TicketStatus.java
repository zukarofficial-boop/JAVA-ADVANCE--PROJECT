package com.helpdesk.enums;

/**
 * Represents the lifecycle stages of a support ticket.
 * Enforced Status Workflow:
 * OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
 */
public enum TicketStatus {
    OPEN("Open", "Ticket created and waiting for assignment"),
    ASSIGNED("Assigned", "Assigned to a support agent"),
    IN_PROGRESS("In Progress", "Agent is actively working on resolution"),
    RESOLVED("Resolved", "Issue has been resolved and pending closure confirmation"),
    CLOSED("Closed", "Ticket is permanently closed");

    private final String displayName;
    private final String description;

    TicketStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
