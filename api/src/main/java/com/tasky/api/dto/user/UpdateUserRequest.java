package com.tasky.api.dto.user;

public record UpdateUserRequest(Boolean passwordReset, String role){}
