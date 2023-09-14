package com.tasky.api.dto.project;

import com.tasky.api.dto.PageableDto;

import java.util.List;

public record SearchProjectResponse(List<ProjectDto> projects, PageableDto pageable) {
}
