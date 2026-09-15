package com.helpdesk.service;

import com.helpdesk.dto.UserRequestDTO;
import com.helpdesk.dto.UserResponseDTO;
import com.helpdesk.entity.User;
import com.helpdesk.exception.InvalidOperationException;
import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling all business logic related to Users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // Constructor-based Dependency Injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user after verifying the email is unique.
     */
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidOperationException("Email is already registered: " + dto.getEmail());
        }

        User user = new User(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRole()
        );

        User savedUser = userRepository.save(user);
        return mapToUserResponseDTO(savedUser);
    }

    /**
     * Retrieves all users from the database.
     */
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> responseList = new ArrayList<>();
        for (User user : users) {
            responseList.add(mapToUserResponseDTO(user));
        }
        return responseList;
    }

    /**
     * Retrieves a single user by ID.
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponseDTO(user);
    }

    /**
     * Updates an existing user's information.
     */
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // If email is changed, ensure the new email is not taken by another user
        if (!existingUser.getEmail().equalsIgnoreCase(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidOperationException("Email is already registered: " + dto.getEmail());
        }

        existingUser.setName(dto.getName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPassword(dto.getPassword());
        existingUser.setRole(dto.getRole());

        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponseDTO(updatedUser);
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    /**
     * Helper method to fetch the User entity internally for other services.
     */
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Helper method to convert a User entity to a UserResponseDTO.
     */
    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
