package com.tasky.api.dto.run;

import com.tasky.api.models.Project;

import java.sql.Timestamp;

public record CreateRunRequest(String name, String description, Long startDate, Long endDate) {
}
