package com.helpdesk.model;

import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core Ticket model entity.
 * Demonstrates Encapsulation & Composition.
 */
public class Ticket implements Comparable<Ticket> {
    private final int id;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private final Customer createdBy;
    private Agent assignedAgent;
    private final List<Comment> comments;
    private final List<TicketHistory> history;
    private Resolution resolution;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(int id, String title, String description, TicketPriority priority, Customer createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = TicketStatus.OPEN;
        this.createdBy = createdBy;
        this.assignedAgent = null;
        this.comments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.resolution = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Initial history record
        this.history.add(new TicketHistory(null, TicketStatus.OPEN, createdBy, "Ticket created by customer"));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public Customer getCreatedBy() {
        return createdBy;
    }

    public Agent getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(Agent assignedAgent) {
        this.assignedAgent = assignedAgent;
        this.updatedAt = LocalDateTime.now();
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
    }

    public List<TicketHistory> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addHistory(TicketHistory historyEntry) {
        this.history.add(historyEntry);
        this.updatedAt = LocalDateTime.now();
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Default comparison orders tickets by Priority (URGENT down to LOW), then by creation date.
     */
    @Override
    public int compareTo(Ticket other) {
        int priorityCompare = Integer.compare(other.priority.getLevel(), this.priority.getLevel());
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return this.createdAt.compareTo(other.createdAt);
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String agentName = (assignedAgent != null) ? assignedAgent.getName() : "Unassigned";
        return String.format("[#%d] %s | Priority: %s | Status: %s | CreatedBy: %s | AssignedTo: %s | Created: %s",
                id, title, priority, status, createdBy.getName(), agentName, createdAt.format(dtf));
    }
}
