package com.tasky.api.dto.project;

import java.sql.Timestamp;

public record ProjectDto(Long projectID, String name, Timestamp dueDate, String description, String creator) {
}
