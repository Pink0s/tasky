package com.tasky.api.mappers;

import com.tasky.api.dto.user.UserDto;
import com.tasky.api.models.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Service class responsible for mapping User entities to UserDto objects.
 */
@Service
public class UserDtoMapper implements Function<User, UserDto> {
    /**
     * Maps a User entity to a UserDto object.
     *
     * @param user The User entity to be mapped.
     * @return A UserDto object containing the mapped user information.
     */
    @Override
    public UserDto apply(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getNeverConnected()
        );
    }
}
