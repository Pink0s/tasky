package com.tasky.api.repositories;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
                () -> assertEquals(user.getFirstName(), retrievedUser.getFirstName()),
                () -> assertEquals(user.getLastName(), retrievedUser.getLastName()),
                () -> assertEquals(user.getPassword(), retrievedUser.getPassword()),
                () -> assertTrue(retrievedUser.isEnabled()),
                () -> assertTrue(retrievedUser.isAccountNonExpired()),
                () -> assertTrue(retrievedUser.getNeverConnected()),
                () -> assertTrue(retrievedUser.isAccountNonLocked()),
                () -> assertTrue(retrievedUser.isCredentialsNonExpired()),
                () -> assertEquals(user.getRole(), retrievedUser.getRole())
        );

    }

    /**
     * Unit test to check if a user exists by email in the repository.
     */
    @Test
    void isUserExistByEmail() {
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
        boolean resultUser = underTest.existsUserByEmail(user.getEmail());

        //THEN
        assertTrue(resultUser);

    }

    /**
     * Unit test to check if a user does not exist by email in the repository.
     */
    @Test
    void isUserDoesNotExistByEmail() {
        //GIVEN
        String email = "fake@test.com";

        //WHEN
        boolean resultUser = underTest.existsUserByEmail(email);

        //THEN
        assertFalse(resultUser);
    }

    /**
     * Unit test search users by email contains.
     */
    @Test
    void findAllByEmailContainsShouldReturnMatchingUsers() {
        // GIVEN
        User user1 = new User("John", "Doe", "john@example.com", "password");
        User user2 = new User("Jane", "Smith", "jane@example.com", "password");
        User user3 = new User("Robert", "Johnson", "robert@example.com", "password");

        underTest.save(user1);
        underTest.save(user2);
        underTest.save(user3);

        String searchPattern = "example";
        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        Page<User> resultPage = underTest.findAllByEmailContains(searchPattern, pageable);

        // THEN
        assertEquals(3, resultPage.getTotalElements());
        assertTrue(resultPage.stream().allMatch(user ->
                user.getEmail().contains(searchPattern)
        ));
    }

    /**
     * Unit test search users by firstname contains.
     */
    @Test
    void findAllByFirstNameContainsShouldReturnMatchingUsers() {
        // GIVEN
        User user1 = new User("John", "Doe", "john@example.com", "password");
        User user2 = new User("Jane", "Smith", "jane@example.com", "password");
        User user3 = new User("Robert", "Johnson", "robert@example.com", "password");

        underTest.save(user1);
        underTest.save(user2);
        underTest.save(user3);

        String searchPattern = "John";
        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        Page<User> resultPage = underTest.findUsersByFirstNameContaining(searchPattern, pageable);

        // THEN
        assertEquals(1, resultPage.getTotalElements());
        assertTrue(resultPage.stream().allMatch(user ->
                user.getFirstName().contains(searchPattern)
        ));
    }

    /**
     * Unit test search users by lastname contains.
     */
    @Test
    void findAllByLastNameContainsShouldReturnMatchingUsers() {
        // GIVEN
        User user1 = new User("John", "Doe", "john@example.com", "password");
        User user2 = new User("Jane", "Smith", "jane@example.com", "password");
        User user3 = new User("Robert", "Johnson", "robert@example.com", "password");

        underTest.save(user1);
        underTest.save(user2);
        underTest.save(user3);

        String searchPattern = "Doe";
        Pageable pageable = PageRequest.of(0, 10);

        // WHEN
        Page<User> resultPage = underTest.findUsersByLastNameContaining(searchPattern, pageable);

        // THEN
        assertEquals(1, resultPage.getTotalElements());
        assertTrue(resultPage.stream().allMatch(user ->
                user.getLastName().contains(searchPattern)
        ));
    }
}