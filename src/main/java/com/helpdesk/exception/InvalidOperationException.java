package com.helpdesk.exception;

/**
 * Exception thrown when a business rule or operation is invalid.
 * (e.g., assigning a ticket to a non-agent, illegal status transitions, duplicate email).
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
