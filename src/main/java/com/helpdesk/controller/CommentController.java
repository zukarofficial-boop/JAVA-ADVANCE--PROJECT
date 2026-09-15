package com.helpdesk.controller;

import com.helpdesk.dto.CommentRequestDTO;
import com.helpdesk.dto.CommentResponseDTO;
import com.helpdesk.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing endpoints for Comments on Tickets.
 * Base URL: /api/tickets/{ticketId}/comments
 */
@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    // Constructor-based Dependency Injection
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a specific ticket.
     * POST /api/tickets/{ticketId}/comments
     * Returns HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long ticketId,
                                                         @Valid @RequestBody CommentRequestDTO dto) {
        CommentResponseDTO createdComment = commentService.addComment(ticketId, dto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * Retrieve all comments for a specific ticket.
     * GET /api/tickets/{ticketId}/comments
     * Returns HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTicket(@PathVariable Long ticketId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByTicket(ticketId);
        return ResponseEntity.ok(comments);
    }
}
