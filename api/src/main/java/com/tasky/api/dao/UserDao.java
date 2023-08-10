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
}
