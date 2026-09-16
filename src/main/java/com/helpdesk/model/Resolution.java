package com.helpdesk.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Encapsulates resolution information when a ticket is resolved.
 */

public class Resolution {
    private final String resolutionDescription;
    private final Agent resolvedBy;
    private final LocalDateTime resolvedAt;
    public Resolution(String resolutionDescription, Agent resolvedBy) {
        this.resolutionDescription = resolutionDescription;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = LocalDateTime.now();
    }
    public String getResolutionDescription() {
        return resolutionDescription;
    }
    public Agent getResolvedBy() {
        return resolvedBy;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Resolved on %s by %s: %s",
                resolvedAt.format(formatter), resolvedBy.getName(), resolutionDescription);
    }
}
