package com.tasky.api.dto.user;

/**
 * Represents a Data Transfer Object (DTO) containing user information.
 */
public record UserDto(Long id, String first_name, String last_name, String email, String role, Boolean never_connected) {
}
