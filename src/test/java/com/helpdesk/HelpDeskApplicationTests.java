package com.helpdesk;

import com.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.dto.TicketResponseDTO;
import com.helpdesk.dto.TicketStatusUpdateDTO;
import com.helpdesk.dto.UserRequestDTO;
import com.helpdesk.dto.UserResponseDTO;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.enums.Priority;
import com.helpdesk.enums.Role;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.exception.InvalidOperationException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpDeskApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private TicketService ticketService;

    private User sampleUser;
    private User sampleAgent;
    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Hariharan", "hari@example.com", "123456", Role.USER, null);
        sampleAgent = new User(2L, "Support Agent", "agent@example.com", "123456", Role.AGENT, null);
        sampleTicket = new Ticket(10L, "Login Issue", "Cannot login", TicketStatus.OPEN, Priority.HIGH, null, null, sampleUser, null);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        UserRequestDTO dto = new UserRequestDTO("Hariharan", "hari@example.com", "123456", Role.USER);
        when(userRepository.existsByEmail("hari@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponseDTO response = userService.createUser(dto);

        assertNotNull(response);
        assertEquals("Hariharan", response.getName());
        assertEquals("hari@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserDuplicateEmail() {
        UserRequestDTO dto = new UserRequestDTO("Hariharan", "hari@example.com", "123456", Role.USER);
        when(userRepository.existsByEmail("hari@example.com")).thenReturn(true);

        assertThrows(InvalidOperationException.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName("Should create ticket with OPEN status and null agent")
    void testCreateTicket() {
        TicketRequestDTO dto = new TicketRequestDTO("Login Issue", "Cannot login", Priority.HIGH, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(sampleTicket);

        TicketResponseDTO response = ticketService.createTicket(dto);

        assertNotNull(response);
        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertEquals("Login Issue", response.getTitle());
        assertNull(response.getAssignedToId());
    }

    @Test
    @DisplayName("Should successfully assign ticket to AGENT")
    void testAssignTicketToAgent() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(sampleAgent));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO response = ticketService.assignTicket(10L, 2L);

        assertNotNull(response);
        assertEquals(2L, response.getAssignedToId());
        assertEquals("Support Agent", response.getAssignedToName());
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when assigning ticket to non-AGENT user")
    void testAssignTicketToNonAgent() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(InvalidOperationException.class, () -> ticketService.assignTicket(10L, 1L));
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when transitioning CLOSED ticket to OPEN")
    void testClosedTicketCannotBeReopenedToOpen() {
        sampleTicket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateDTO statusDto = new TicketStatusUpdateDTO(TicketStatus.OPEN);

        assertThrows(InvalidOperationException.class, () -> ticketService.updateTicketStatus(10L, statusDto));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent ticket")
    void testTicketNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(999L));
    }
}
