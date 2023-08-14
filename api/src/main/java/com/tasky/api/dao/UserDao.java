package com.tasky.api.dao;

import com.tasky.api.models.User;

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
}
