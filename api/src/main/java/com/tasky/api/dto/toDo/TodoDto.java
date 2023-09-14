package com.tasky.api.dto.toDo;

import java.sql.Timestamp;

public record TodoDto(
        Long toDoId,
        String title,
        String type,
        String description,
        String status,
        Timestamp createdAt ,
        Timestamp updatedAt
) {}