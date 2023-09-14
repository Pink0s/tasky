package com.tasky.api.mappers;

import com.tasky.api.dto.comment.CommentDto;
import com.tasky.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentDtoMapperTest {

    CommentDtoMapper underTest = new CommentDtoMapper();


    @Test
    void apply() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Feature feature = new Feature("feature","desc",project);

        feature.setId(1L);

        ToDo toDo = new ToDo("name","BUG","description",feature,user);
        toDo.setId(1L);

        Comment comment = new Comment("name","content",toDo);

        CommentDto result = underTest.apply(comment);

        assertEquals(result.content(),comment.getContent());
        assertEquals(result.name(),comment.getName());
        assertEquals(result.creationDate(),comment.getCreatedAt());
    }
}