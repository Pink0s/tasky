package com.tasky.api.dto.project;

public record CreateProjectRequest(String name, Long dueDate, String description) {
}
