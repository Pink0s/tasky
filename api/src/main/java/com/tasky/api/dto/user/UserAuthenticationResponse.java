package com.tasky.api.dto.user;

/**
 * Represents a user authentication response containing a token and user information.
 */
public record UserAuthenticationResponse(String token, UserDto userDto) {
}
