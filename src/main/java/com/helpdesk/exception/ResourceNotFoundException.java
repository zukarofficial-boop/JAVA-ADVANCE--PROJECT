package com.helpdesk.exception;

/**
 * Exception thrown when a requested resource (User, Ticket, Comment) is not found in the database.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
