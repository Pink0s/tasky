package com.tasky.api.dto.toDo;

public record UpdateTodoRequest(String name, String type, String description, Long userId, String status) {
}
