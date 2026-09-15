package com.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a Comment on a Ticket.
 * Ticket ID is obtained from the URL path variable.
 */
public class CommentRequestDTO {

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "userId must not be null")
    private Long userId;

    public CommentRequestDTO() {
    }

    public CommentRequestDTO(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
