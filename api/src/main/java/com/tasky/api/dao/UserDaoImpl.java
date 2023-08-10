package com.tasky.api.dao;

import com.tasky.api.models.User;
import com.tasky.api.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of the UserDao interface using JPA and UserRepository for database operations.
 */
@Repository("JPA")
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    /**
     * Constructs a UserDaoImpl with the specified UserRepository.
     *
     * @param userRepository The UserRepository implementation.
     */
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their email address using UserRepository.
     *
     * @param email The email address of the user.
     * @return An Optional containing the retrieved user, or an empty Optional if not found.
     */
    @Override
    public Optional<User> selectUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * Inserts a new user into the database using UserRepository.
     *
     * @param user The User object to be inserted.
     * @return The inserted User object.
     */
    @Override
    public User insertUser(User user) {
        return userRepository.save(user);
    }
}
