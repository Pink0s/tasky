package com.tasky.api.dto.project;

import java.sql.Timestamp;

public record CreateProjectRequest(String name, Timestamp dueDate, String description) {
}
