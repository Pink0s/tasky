package com.tasky.api.dao.comment;

import com.tasky.api.models.*;
import com.tasky.api.repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CommentDaoImplTest {

    @Mock private CommentRepository commentRepository;
    @InjectMocks private CommentDaoImpl underTest;

    @Test
    void createComment() {
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

        underTest.createComment(comment);

        verify(commentRepository).save(comment);
    }

    @Test
    void selectCommentById() {
        Long commentId = 1L;
        underTest.selectCommentById(commentId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void updateComment() {
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

        underTest.updateComment(comment);

        verify(commentRepository).save(comment);
    }

    @Test
    void deleteCommentById() {
        Long commentId = 1L;
        underTest.deleteCommentById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void selectAllCommentForTodoWhereNameContains() {
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

        String pattern = "tzateztzt";
        Pageable pageable = PageRequest.of(1,2);

        underTest.selectAllCommentForTodoWhereNameContains(toDo,pattern,pageable);

        verify(commentRepository).getAllByToDoIsAndNameContaining(toDo,pattern,pageable);
    }

    @Test
    void selectAllCommentForTodoWhereContentContains() {
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

        String pattern = "tzateztzt";
        Pageable pageable = PageRequest.of(1,2);

        underTest.selectAllCommentForTodoWhereContentContains(toDo,pattern,pageable);

        verify(commentRepository).getAllByToDoIsAndContentContaining(toDo,pattern,pageable);

    }
}