package com.helpdesk.model;
import com.helpdesk.enums.UserRole;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a Support Agent who resolves support tickets.
 * Demonstrates:
 * 1. Inheritance: Extends User.
 * 2. Polymorphism: Customizes getDetails().
 */

public class Agent extends User {
    private String specialization;
    private boolean isAvailable;
    private final List<Ticket> assignedTickets;
    public Agent(int id, String name, String email, String specialization) {
        super(id, name, email, UserRole.AGENT);
        this.specialization = specialization;
        this.isAvailable = true;
        this.assignedTickets = new ArrayList<>();
    }
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    public List<Ticket> getAssignedTickets() {
        return assignedTickets;
    }
    public void assignTicket(Ticket ticket) {
        this.assignedTickets.add(ticket);
    }
    public void removeAssignedTicket(Ticket ticket) {
        this.assignedTickets.remove(ticket);
    }
    public long getActiveTicketCount() {
        return assignedTickets.stream()
                .filter(t -> t.getStatus().name().equals("ASSIGNED") || t.getStatus().name().equals("IN_PROGRESS"))
                .count();
    }
    @Override
    public String getDetails() {
        return String.format("Agent Profile: %s | Spec: %s | Status: %s | Active Assigned Tickets: %d",
                getName(), specialization, isAvailable ? "Available" : "Busy/Offline", getActiveTicketCount());
    }
}
