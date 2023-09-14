package com.tasky.api.dto.run;

import java.sql.Timestamp;

public record RunDto(Long runId, String name, String description, String status, Timestamp startDate, Timestamp endDate) {
}
