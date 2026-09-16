package com.helpdesk.model;
import com.helpdesk.enums.UserRole;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a Customer who creates support tickets.
 * Demonstrates:
 * 1. Inheritance: Extends the abstract User base class.
 * 2. Polymorphism: Overrides getDetails() with customer-specific information.
 */

public class Customer extends User {
    private String department;
    private String phoneNumber;
    private final List<Ticket> ticketsCreated;
    public Customer(int id, String name, String email, String department, String phoneNumber) {
        super(id, name, email, UserRole.CUSTOMER);
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.ticketsCreated = new ArrayList<>();
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public List<Ticket> getTicketsCreated() {
        return ticketsCreated;
    }
    public void addCreatedTicket(Ticket ticket) {
        this.ticketsCreated.add(ticket);
    }
    @Override
    public String getDetails() {
        return String.format("Customer Profile: %s | Email: %s | Dept: %s | Phone: %s | Total Tickets Created: %d",
                getName(), getEmail(), department, phoneNumber, ticketsCreated.size());
    }
}
