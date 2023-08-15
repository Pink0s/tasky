package com.tasky.api.dto.user;

import java.util.Optional;

public record UpdateUserResponse(UserDto user, Optional<String> password) {
}
