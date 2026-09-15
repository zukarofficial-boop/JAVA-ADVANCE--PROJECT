package com.helpdesk.repository;

import com.helpdesk.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for database operations on Comment entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds all comments for a given ticket ID.
     */
    List<Comment> findByTicketId(Long ticketId);

    /**
     * Finds all comments for a given ticket ID ordered chronologically.
     */
    List<Comment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
