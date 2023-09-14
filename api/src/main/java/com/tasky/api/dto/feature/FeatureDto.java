package com.tasky.api.dto.feature;

import java.sql.Timestamp;

public record FeatureDto(Long id ,String name, String description, String status, Timestamp createdAt, Timestamp updatedAt) {
}
