package com.tasky.api.dto.user;

public record UpdatePasswordRequest(String oldPassword, String newPassword) {
}
