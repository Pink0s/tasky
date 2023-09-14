package com.tasky.api.dto.feature;

import com.tasky.api.dto.PageableDto;

import java.util.List;

public record SearchFeatureResponse(List<FeatureDto> features, PageableDto pageable) {
}
