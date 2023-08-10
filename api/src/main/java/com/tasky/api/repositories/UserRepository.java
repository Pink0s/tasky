package com.tasky.api.repositories;

import com.tasky.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 */
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * Find a user by their email address.
     *
     * @param email The email address to search for.
     * @return An Optional containing the found user or empty if not found.
     */
    Optional<User> findUserByEmail(String email);
}
