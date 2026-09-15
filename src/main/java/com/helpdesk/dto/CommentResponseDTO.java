package com.helpdesk.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning Comment details.
 */
public class CommentResponseDTO {

    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long ticketId;

    public CommentResponseDTO() {
    }

    public CommentResponseDTO(Long id, String message, LocalDateTime createdAt, Long userId, String userName, Long ticketId) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.ticketId = ticketId;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
