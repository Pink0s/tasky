package com.tasky.api.repositories;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link UserRepository} class using the {@link DataJpaTest} annotation.
 * This class tests database interactions related to user entities.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractTestContainer {

    @Autowired private UserRepository underTest;

    /**
     * Clears the database before each test method.
     */
    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    /**
     * Test case to verify that attempting to find a user by a non-existent email should result in an empty optional.
     */
    @Test
    void cannotFindUserByEmail() {
        //GIVEN
        String email = "notexists@test.com";
        //WHEN
        Optional<User> user = underTest.findUserByEmail(email);
        //THEN
        assertFalse(user.isPresent());
    }

    /**
     * Test case to verify that a user can be found by their email, and their properties match the expected values.
     */
    @Test
    void canFoundUserByEmail() {

        //GIVEN
        User user = new User(
                "test",
                "test",
                "test@test.test",
                "password123452435134513"
        );

        user.setRole("ADMIN");

        underTest.save(user);

        //WHEN
        Optional<User> resultUser = underTest.findUserByEmail(user.getEmail());

        //THEN
        User retrievedUser = resultUser.get();

        assertAll("User properties",
                () -> assertEquals(user.getEmail(), retrievedUser.getEmail()),
                () -> assertEquals(user.getFirst_name(), retrievedUser.getFirst_name()),
                () -> assertEquals(user.getLast_name(), retrievedUser.getLast_name()),
                () -> assertEquals(user.getPassword(), retrievedUser.getPassword()),
                () -> assertTrue(retrievedUser.isEnabled()),
                () -> assertTrue(retrievedUser.isAccountNonExpired()),
                () -> assertTrue(retrievedUser.getNever_connected()),
                () -> assertTrue(retrievedUser.isAccountNonLocked()),
                () -> assertTrue(retrievedUser.isCredentialsNonExpired()),
                () -> assertEquals(user.getRole(), retrievedUser.getRole())
        );

    }

}