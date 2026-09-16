package com.helpdesk.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a discussion comment posted on a Ticket.
 */

public class Comment {
    private final int id;
    private final String content;
    private final User author;
    private final LocalDateTime createdAt;
    public Comment(int id, String content, User author) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public User getAuthor() {
        return author;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%s] %s (%s): %s",
                createdAt.format(formatter), author.getName(), author.getRole(), content);
    }
}
