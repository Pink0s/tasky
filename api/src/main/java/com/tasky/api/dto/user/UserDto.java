package com.tasky.api.dto.user;

/**
 * Represents a Data Transfer Object (DTO) containing user information.
 */
public record UserDto(Long id, String firstName, String lastName, String email, String role, Boolean neverConnected) {
}
