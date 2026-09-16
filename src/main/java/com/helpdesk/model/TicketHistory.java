package com.helpdesk.model;

import com.helpdesk.enums.TicketStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Audit log recording status transitions for a ticket.
 */
public class TicketHistory {
    private final TicketStatus oldStatus;
    private final TicketStatus newStatus;
    private final User changedBy;
    private final LocalDateTime timestamp;
    private final String remark;

    public TicketHistory(TicketStatus oldStatus, TicketStatus newStatus, User changedBy, String remark) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.timestamp = LocalDateTime.now();
        this.remark = remark;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] Status changed from %s -> %s by %s (%s). Note: %s",
                timestamp.format(formatter), oldStatus, newStatus, changedBy.getName(), changedBy.getRole(), remark);
    }
}
