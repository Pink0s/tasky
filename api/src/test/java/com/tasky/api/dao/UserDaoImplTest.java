package com.tasky.api.dao;

import com.tasky.api.models.User;
import com.tasky.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for the {@link UserDaoImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {

    @Mock private UserRepository userRepository;
    private UserDaoImpl underTest;

    /**
     * Sets up the test environment by creating an instance of {@link UserDaoImpl} with a mocked {@link UserRepository}.
     */
    @BeforeEach
    void setUp() {
        underTest = new UserDaoImpl(userRepository);
    }

    /**
     * Tests the behavior of selecting a user by email.
     */
    @Test
    void selectUserByEmail() {
        //GIVEN
        String email = "test@test.test";

        //WHEN
        underTest.selectUserByEmail(email);

        //THEN
        Mockito.verify(userRepository).findUserByEmail(email);
    }

    /**
     * Tests the behavior of inserting a user.
     */
    @Test
    void insertUser() {
        //GIVEN
        User user = new User(
                "test",
                "test",
                "test@test.test",
                "password123452435134513"
        );

        //WHEN
        underTest.insertUser(user);

        //THEN
        Mockito.verify(userRepository).save(user);
    }

    /**
     * Tests the behavior of checking if user exists by email.
     */
    @Test
    void isExistUser() {
        // GIVEN
        String email = "test@test.com";

        //WHEN
        underTest.isUserExists(email);

        //THEN
        Mockito.verify(userRepository).existsUserByEmail(email);
    }

    /**
     * Test case to verify that {@link UserDao#selectAllUsersByEmail(String, Pageable)} retrieves users whose email addresses match the provided search pattern.
     */
    @Test
    void selectAllUsersByEmail() {

        //GIVEN
        Pageable pageable = PageRequest.of(0,10);
        String pattern = "test@test.com";

        //WHEN
        underTest.selectAllUsersByEmail(pattern,pageable);

        //THEN
        Mockito.verify(userRepository).findAllByEmailContains(pattern,pageable);
    }

    /**
     * Test case to verify that {@link UserDao#selectAllUsersByLastName(String, Pageable)} retrieves users whose last names match the provided search pattern.
     */
    @Test
    void selectAllUsersByLastName() {

        //GIVEN
        Pageable pageable = PageRequest.of(0,10);
        String pattern = "test";

        //WHEN
        underTest.selectAllUsersByLastName(pattern,pageable);

        //THEN
        Mockito.verify(userRepository).findUsersByLastNameContaining(pattern,pageable);
    }
    /**
     * Test case to verify that {@link UserDao#selectAllUsersByFirstName(String, Pageable)} retrieves users whose first names match the provided search pattern.
     */
    @Test
    void selectAllUsersByFirstName() {

        //GIVEN
        Pageable pageable = PageRequest.of(0,10);
        String pattern = "test";

        //WHEN
        underTest.selectAllUsersByFirstName(pattern,pageable);

        //THEN
        Mockito.verify(userRepository).findUsersByFirstNameContaining(pattern,pageable);
    }

    /**
     * Test case to verify that {@link UserDao#deleteUserById(Long)} deletes a user by their ID.
     */
    @Test
    void deleteUserById() {
        //GIVEN
        Long id = 1L;
        //WHEN
        underTest.deleteUserById(id);
        //THEN
        Mockito.verify(userRepository).deleteById(id);
    }
    /**
     * Test case to verify that {@link UserDao#updateUser(User)} updates user information and saves it in the repository.
     */
    @Test
    void updateUser() {

        //GIVEN
        User user = new User(
                "test",
                "test",
                "test@test.test",
                "password123452435134513"
        );
        //WHEN
        underTest.updateUser(user);
        //THEN
        Mockito.verify(userRepository).save(user);
    }

    /**
     * Test case to verify the functionality of retrieving a user by their ID using {@link UserDao#selectUserById(Long)}.
     */
    @Test
    void selectUserById() {
        // GIVEN
        Long userId = 1L;

        //WHEN
        underTest.selectUserById(userId);

        //THEN

        Mockito.verify(userRepository).findById(userId);
    }
}