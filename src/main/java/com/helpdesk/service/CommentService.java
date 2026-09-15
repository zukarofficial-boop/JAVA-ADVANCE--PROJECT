package com.helpdesk.service;

import com.helpdesk.dto.CommentRequestDTO;
import com.helpdesk.dto.CommentResponseDTO;
import com.helpdesk.entity.Comment;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.repository.CommentRepository;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling all business logic related to Comments on Tickets.
 */
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    // Constructor-based Dependency Injection
    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a comment to an existing ticket authored by a specific user.
     */
    public CommentResponseDTO addComment(Long ticketId, CommentRequestDTO dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Comment comment = new Comment(
                dto.getMessage(),
                user,
                ticket
        );

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponseDTO(savedComment);
    }

    /**
     * Retrieves all comments for a specific ticket in chronological order.
     */
    public List<CommentResponseDTO> getCommentsByTicket(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }

        List<Comment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        List<CommentResponseDTO> responseList = new ArrayList<>();
        for (Comment comment : comments) {
            responseList.add(mapToCommentResponseDTO(comment));
        }
        return responseList;
    }

    /**
     * Helper method to convert a Comment entity to a CommentResponseDTO.
     */
    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        Long userId = comment.getUser() != null ? comment.getUser().getId() : null;
        String userName = comment.getUser() != null ? comment.getUser().getName() : null;
        Long ticketId = comment.getTicket() != null ? comment.getTicket().getId() : null;

        return new CommentResponseDTO(
                comment.getId(),
                comment.getMessage(),
                comment.getCreatedAt(),
                userId,
                userName,
                ticketId
        );
    }
}
