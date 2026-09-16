package com.helpdesk.model;
import com.helpdesk.enums.UserRole;
import java.time.LocalDateTime;
/**
 * Abstract base class representing a generic User in the Help Desk system.
 * Demonstrates:
 * 1. Abstraction: Defines common properties and contract without direct instantiation.
 * 2. Encapsulation: All state fields are private with accessors/mutators.
 */

public abstract class User {
    private final int id;
    private String name;
    private String email;
    private final UserRole role;
    private final LocalDateTime createdAt;
    public User(int id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
    // Abstract method demonstrating Polymorphism - implemented differently in Customer and Agent
    public abstract String getDetails();
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public UserRole getRole() {
        return role;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    @Override
    public String toString() {
        return String.format("[%s #%d] %s (%s)", role, id, name, email);
    }
}
