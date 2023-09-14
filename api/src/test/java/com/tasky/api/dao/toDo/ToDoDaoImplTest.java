package com.tasky.api.dao.toDo;

import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ToDoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ToDoDaoImplTest {

    @Mock ToDoRepository toDoRepository;
    @InjectMocks ToDoDaoImpl underTest;

    @Test
    void createTodo() {
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

        underTest.createTodo(toDo);

        verify(toDoRepository).save(toDo);
    }

    @Test
    void selectToDoById() {
        Long toDoId = 1L;

        underTest.selectToDoById(toDoId);

        verify(toDoRepository).findById(toDoId);
    }
    @Test
    void selectToDosWhereUserIsAndNameContaining() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        String name = "test";
        Pageable pageable = PageRequest.of(0,1);

        underTest.selectToDosWhereUserIsAndNameContaining(user,name,pageable);

        verify(toDoRepository).findAllByUserAndAndNameContaining(user,name,pageable);
    }
    @Test
    void selectAllToDoWhereFeatureIdIsAndNameContaining() {
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

        String name = "test";
        Pageable pageable = PageRequest.of(0,1);

        underTest.selectAllToDoWhereFeatureIdIsAndNameContaining(feature,name,pageable);

        verify(toDoRepository).findAllByFeatureIsAndNameContaining(feature,name,pageable);
    }

    @Test
    void updateTodo() {
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

        underTest.updateTodo(toDo);

        verify(toDoRepository).save(toDo);
    }

    @Test
    void deleteTodoById() {
        Long toDoId = 1L;

        underTest.deleteTodoById(toDoId);

        verify(toDoRepository).deleteById(toDoId);
    }


}