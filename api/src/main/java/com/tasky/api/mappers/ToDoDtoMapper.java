package com.tasky.api.mappers;

import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.models.ToDo;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Mapper class to convert a ToDo entity to a ToDoDto.
 */
@Service
public class ToDoDtoMapper implements Function<ToDo, TodoDto> {
    /**
     * Converts a ToDo entity to a ToDoDto.
     *
     * @param toDo The ToDo entity to be mapped.
     * @return A ToDoDto object containing the mapped attributes from the ToDo entity.
     */
    @Override
    public TodoDto apply(ToDo toDo) {
        return new TodoDto(
                toDo.getId(),
                toDo.getName(),
                toDo.getType(),
                toDo.getDescription(),
                toDo.getStatus(),
                toDo.getCreatedAt(),
                toDo.getUpdatedAt()
        );
    }
}
