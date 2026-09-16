package com.helpdesk.enums;

/**
 * Represents the urgency level of a ticket.
 */
public enum TicketPriority {
    LOW(1, "Low Priority"),
    MEDIUM(2, "Medium Priority"),
    HIGH(3, "High Priority"),
    URGENT(4, "Urgent / Critical Priority");

    private final int level;
    private final String description;

    TicketPriority(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }
}
