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
     * {@inheritDoc}
     */
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> selectUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User insertUser(User user) {
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isUserExists(String email) {
        return userRepository.existsUserByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<User> selectAllUsersByEmail(String email, Pageable page) {
        return userRepository.findAllByEmailContains(email,page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<User> selectAllUsersByLastName(String lastName, Pageable page) {
        return userRepository.findUsersByLastNameContaining(lastName,page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<User> selectAllUsersByFirstName(String firstName, Pageable page) {
        return userRepository.findUsersByFirstNameContaining(firstName,page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> selectUserById(Long id) {
        return userRepository.findById(id);
    }


}
