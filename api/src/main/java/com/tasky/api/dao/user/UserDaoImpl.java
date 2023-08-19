package com.tasky.api.dao.user;

import com.tasky.api.models.User;
import com.tasky.api.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Checks whether a user with the specified email exists.
     *
     * @param email The email address of the user to be checked.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    @Override
    public Boolean isUserExists(String email) {
        return userRepository.existsUserByEmail(email);
    }

    /**
     * Retrieves a page of users whose email addresses match the provided search pattern.
     *
     * @param email The search pattern to match against email addresses.
     * @param page  The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose email addresses match the search pattern.
     */
    @Override
    public Page<User> selectAllUsersByEmail(String email, Pageable page) {
        return userRepository.findAllByEmailContains(email,page);
    }

    /**
     * Retrieves a page of users whose last names match the provided search pattern.
     *
     * @param lastName The search pattern to match against last names.
     * @param page     The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose last names match the search pattern.
     */
    @Override
    public Page<User> selectAllUsersByLastName(String lastName, Pageable page) {
        return userRepository.findUsersByLastNameContaining(lastName,page);
    }

    /**
     * Retrieves a page of users whose first names match the provided search pattern.
     *
     * @param firstName The search pattern to match against first names.
     * @param page      The pageable settings for retrieving the result.
     * @return A {@link Page} containing the users whose first names match the search pattern.
     */
    @Override
    public Page<User> selectAllUsersByFirstName(String firstName, Pageable page) {
        return userRepository.findUsersByFirstNameContaining(firstName,page);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to be deleted.
     */
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Updates the information of a user with the provided user object.
     *
     * @param user The user object containing updated information.
     * @return The updated user object after the changes have been applied.
     */
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The retrieved {@link Optional<User>} object if found, or {@code null} if no user with the given ID exists.
     */
    @Override
    public Optional<User> selectUserById(Long id) {
        return userRepository.findById(id);
    }


}
