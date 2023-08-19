package com.tasky.api.services.user;

import com.tasky.api.dao.user.UserDao;
import com.tasky.api.models.User;
import com.tasky.api.services.user.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link UserDetailsService} class.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    private UserDetailsService underTest;
    @Mock private UserDao userDao;

    /**
     * Sets up the test environment by creating an instance of {@link UserDetailsService} with a mocked {@link UserDao}.
     */
    @BeforeEach
    void setUp() {
        underTest = new UserDetailsService(userDao);
    }

    /**
     * Tests the behavior of loading a user by username.
     */
    @Test
    void canLoadUserByUsername() {

        //GIVEN
        String email = "test@test.com";

        User user = new User(
                "test",
                "test",
                "test@test.test",
                "password123452435134513"
        );

        Mockito.when(userDao.selectUserByEmail(email)).thenReturn(Optional.of(user));

        //WHEN
        User retrievedUser = (User) underTest.loadUserByUsername(email);

        //THEN
        assertEquals(retrievedUser,user);
    }

    /**
     * Tests the behavior when loading a user by a non-existing username.
     */
    @Test
    void cannotLoadUserByUsername() {
        //GIVEN
        String email = "test@test.com";
        Mockito.when(userDao.selectUserByEmail(email)).thenReturn(Optional.empty());

        //WHEN THEN
        assertThrows(UsernameNotFoundException.class,() -> {
            underTest.loadUserByUsername(email);
        });
    }
}