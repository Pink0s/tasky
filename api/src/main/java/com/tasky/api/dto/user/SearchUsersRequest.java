package com.tasky.api.dto.user;

public record SearchUsersRequest(String type, String pattern, Integer page) {
}
