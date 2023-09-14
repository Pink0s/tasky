package com.tasky.api.dto.toDo;

import com.tasky.api.dto.PageableDto;

import java.util.List;

public record SearchToDoResponse(List<TodoDto> toDos, PageableDto pageable) {
}
