package com.tasky.api.dto.user;

/**
 * Represents a user authentication request containing username and password.
 */
public record UserAuthenticationRequest(String username, String password) {
}
