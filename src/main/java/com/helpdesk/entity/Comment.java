package com.helpdesk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a Comment posted on a Ticket.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Many comments can be authored by one user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many comments belong to one ticket
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Default Constructor
    public Comment() {
    }

    // Parameterized Constructor
    public Comment(Long id, String message, LocalDateTime createdAt, User user, Ticket ticket) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.ticket = ticket;
    }

    public Comment(String message, User user, Ticket ticket) {
        this.message = message;
        this.user = user;
        this.ticket = ticket;
    }

    /**
     * Automatically sets the comment creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
