package com.tasky.api.dto.project;

public record UpdateProjectRequest(String name, String description, String status, Long DueDate) {
}
