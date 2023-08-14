package com.tasky.api.repositories;

import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Boolean existsUserByEmail(String email);

    /**
     * Retrieves a page of users whose email addresses match the provided search pattern.
     *
     * @param email The search pattern to match against email addresses.
     * @return A {@link Page} containing the users whose email addresses match the search pattern.
     */
    Page<User> findAllByEmailContains(String email, Pageable page);

    /**
     * Retrieves a page of users whose firstname match the provided search pattern.
     *
     * @param firstName The search pattern to match against firstname.
     * @return A {@link Page} containing the users whose firstname match the search pattern.
     */
    Page<User> findUsersByFirstNameContaining(String firstName, Pageable page);

    /**
     * Retrieves a page of users whose lastName match the provided search pattern.
     *
     * @param lastName The search pattern to match against lastName.
     * @return A {@link Page} containing the users whose lastName match the search pattern.
     */
    Page<User> findUsersByLastNameContaining(String lastName, Pageable page);

}
