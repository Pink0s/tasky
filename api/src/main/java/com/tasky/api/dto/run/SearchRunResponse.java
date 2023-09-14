package com.tasky.api.dto.run;

import com.tasky.api.dto.PageableDto;

import java.util.List;

public record SearchRunResponse(List<RunDto> runs, PageableDto pageable) {
}
