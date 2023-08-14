package com.tasky.api.mappers;

import com.tasky.api.dto.user.UserDto;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link UserDtoMapper} class.
 */
class UserDtoMapperTest {

    /**
     * Test the mapping functionality of the {@link UserDtoMapper#apply(User)} method.
     * It should map the fields of a {@link User} entity to a {@link UserDto} object correctly.
     */
    @Test
    void apply() {
        // GIVEN

        User user = new User(
                "test",
                "test",
                "test@test.test",
                "password123452435134513"
        );

        user.setRole("ADMIN");

        UserDtoMapper userDtoMapper = new UserDtoMapper();

        // WHEN
        UserDto userDto = userDtoMapper.apply(user);

        // THEN
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getFirstName(), userDto.firstName());
        assertEquals(user.getLastName(), userDto.lastName());
        assertEquals(user.getEmail(), userDto.email());
        assertEquals(user.getRole(), userDto.role());
        assertEquals(user.getNeverConnected(), userDto.neverConnected());
    }
}