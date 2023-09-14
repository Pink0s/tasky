package com.tasky.api.mappers;

import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.models.ToDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ToDoDtoMapperTest {

    private final ToDoDtoMapper underTest = new ToDoDtoMapper();

    @Test
    void apply() {
        ToDo toDo = new ToDo("name","task","description",null,null);
        toDo.setId(1L);
        TodoDto todoDto = underTest.apply(toDo);
        assertEquals(todoDto.toDoId(),toDo.getId());
        assertEquals(todoDto.description(),toDo.getDescription());
        assertEquals(todoDto.title(),toDo.getName());
        assertEquals(todoDto.type(),toDo.getType());
        assertEquals(todoDto.status(),toDo.getStatus());
    }
}