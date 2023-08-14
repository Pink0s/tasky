package com.tasky.api.dao;

import com.tasky.api.models.User;
import com.tasky.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
}