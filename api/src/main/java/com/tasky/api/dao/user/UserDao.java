package com.tasky.api.dao.user;

import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing user-related database operations.
 */

public interface UserDao {
    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user.
     * @return An Optional containing the retrieved user, or an empty Optional if not found.
     */
    Optional<User> selectUserByEmail(String email);

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object to be inserted.
     * @return The inserted User object.
     */
    User insertUser(User user);

    /**
     * Checks whether a user with the specified email exists.
     *
     * @param email The email address of the user to be checked.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    Boolean isUserExists(String email);

    /**
     * Retrieves a page of users whose email addresses match the provided search pattern.
     *
     * @param email The search pattern to match against email addresses.
     * @param page The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose email addresses match the search pattern.
     */
    Page<User> selectAllUsersByEmail(String email, Pageable page);

    /**
     * Retrieves a page of users whose last names match the provided search pattern.
     *
     * @param lastName The search pattern to match against last names.
     * @param page The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose last names match the search pattern.
     */
    Page<User> selectAllUsersByLastName(String lastName, Pageable page);

    /**
     * Retrieves a page of users whose first names match the provided search pattern.
     *
     * @param firstName The search pattern to match against first names.
     * @param page The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose first names match the search pattern.
     */
    Page<User> selectAllUsersByFirstName(String firstName, Pageable page);

    /**
     * Deletes a user with the specified ID.
     *
     * @param id The ID of the user to be deleted.
     */
    void deleteUserById(Long id);

    /**
     * Updates the information of a user with the provided user object.
     *
     * @param user The user object containing updated information.
     * @return The updated user object after the changes have been applied.
     *
     */
    User updateUser(User user);

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The retrieved {@link Optional<User>} object if found, or {@code null} if no user with the given ID exists.
     */
    Optional<User> selectUserById(Long id);

}
