package com.tasky.api.dto.project;

import com.tasky.api.dto.user.UserDto;

import java.sql.Timestamp;
import java.util.List;

public record ProjectDto(Long projectID, String name, Timestamp dueDate, String description, String creator, List<UserDto> users) {
}
