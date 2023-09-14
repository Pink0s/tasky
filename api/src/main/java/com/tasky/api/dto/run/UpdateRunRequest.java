package com.tasky.api.dto.run;

public record UpdateRunRequest(String name, String description, Long startDate, Long endDate, String status) {
}
