package com.helpdesk.repository;

import com.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for database operations on User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email address.
     */
    boolean existsByEmail(String email);
}
