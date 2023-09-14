package com.tasky.api.services.comment;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.comment.CommentDao;
import com.tasky.api.dao.toDo.ToDoDao;
import com.tasky.api.dto.comment.*;
import com.tasky.api.mappers.CommentDtoMapper;
import com.tasky.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock ToDoDao toDoDao;
    @Mock private CommentDao commentDao;
    @Mock private CommentDtoMapper commentDtoMapper;
    @InjectMocks private CommentServiceImpl underTest;

    private User createFakeProjectManager() {
        String projectManageFirstname = "John";
        String projectManageLastname = "Doe";
        String projectManagerEmail = "john@doe.com";
        String projectPassword = "password";
        String projectManagerRole = "PROJECT_MANAGER";
        Long projectManagerId = 1L;

        User projectManager = new User(
                projectManageFirstname,
                projectManageLastname,
                projectManagerEmail,
                projectPassword
        );

        projectManager.setRole(projectManagerRole);
        projectManager.setId(projectManagerId);

        return projectManager;
    }

    private User createFakeUser() {
        String projectManageFirstname = "John";
        String projectManageLastname = "Doe";
        String projectManagerEmail = "john2@doe.com";
        String projectPassword = "password";
        String projectManagerRole = "USER";
        Long projectManagerId = 2L;

        User projectManager = new User(
                projectManageFirstname,
                projectManageLastname,
                projectManagerEmail,
                projectPassword
        );

        projectManager.setRole(projectManagerRole);
        projectManager.setId(projectManagerId);

        return projectManager;
    }

    private Project createFakeProject(User creator) {
        String projectName = "project";

        Timestamp projectDueDate = Timestamp.from(
                Instant.now()
                        .plus(5, ChronoUnit.DAYS)
        );

        Project project = new Project(
                projectName,
                projectDueDate,
                creator
        );

        Long projectId = 1L;

        project.setId(projectId);

        return project;
    }

    private Feature createFakeFeature(Project project) {
        String featureName = "Feature";
        String featureDescription = "Feature description";
        Long featureId = 1L;

        Feature feature = new Feature(
                featureName,
                featureDescription,
                project
        );

        feature.setId(featureId);

        return feature;
    }

    private ToDo createFakeTodo(User creator, Feature feature) {
        String toDoName = "name";
        String toDoType = "BUG";
        String toDoDescription = "description";

        Long toDoId = 1L;
        ToDo toDo = new ToDo(toDoName,toDoType,toDoDescription,feature,creator);
        toDo.setId(toDoId);

        return toDo;
    }

    private Comment createFakeComment(ToDo toDo) {
        Comment comment = new Comment("name","content",toDo);
        comment.setId(1L);
        return comment;
    }

    @Test
    void createCommentAsProjectManager() {
        Authentication authentication = mock(Authentication.class);

        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);

        ToDo toDo = createFakeTodo(projectManager,feature);

        String commentName = "commentName";
        String commentContent = "comment content";

        CreateCommentRequest request = new CreateCommentRequest(commentName,commentContent);

        Mockito.when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(projectManager);
        underTest.createComment(authentication,toDo.getId(),request);

        ArgumentCaptor<Comment> commentArgumentCaptor= ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(commentDao).createComment(commentArgumentCaptor.capture());
        Comment commentCaptured = commentArgumentCaptor.getValue();
        assertEquals(commentCaptured.getContent(),commentContent);
        assertEquals(commentCaptured.getName(),commentName);
        assertEquals(commentCaptured.getToDo(),toDo);
    }

    @Test
    void createCommentAsProjectManagerShouldThrowBadRequest() {
        Authentication authentication = mock(Authentication.class);

        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);

        String commentContent = "comment content";

        CreateCommentRequest request = new CreateCommentRequest(null,commentContent);

        Mockito.when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(projectManager);
        assertThrows(BadRequestException.class, () -> underTest.createComment(authentication,toDo.getId(),request));
    }

    @Test
    void createCommentAsProjectManagerShouldThrowBadRequest2() {
        Authentication authentication = mock(Authentication.class);
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);

        String commentName = "comment name";
        CreateCommentRequest request = new CreateCommentRequest(commentName,null);

        Mockito.when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(projectManager);
        assertThrows(BadRequestException.class, () -> underTest.createComment(authentication,toDo.getId(),request));
    }

    @Test
    void createCommentUserAllowed() {
        Authentication authentication = mock(Authentication.class);

        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);


        String commentName = "commentName";
        String commentContent = "comment content";

        CreateCommentRequest request = new CreateCommentRequest(commentName,commentContent);

        Mockito.when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(user);
        underTest.createComment(authentication, toDo.getId(), request);

        ArgumentCaptor<Comment> commentArgumentCaptor= ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(commentDao).createComment(commentArgumentCaptor.capture());
        Comment commentCaptured = commentArgumentCaptor.getValue();
        assertEquals(commentCaptured.getContent(),commentContent);
        assertEquals(commentCaptured.getName(),commentName);
        assertEquals(commentCaptured.getToDo(),toDo);
    }

    @Test
    void createCommentShouldThrowNotFoundException() {
        Long toDoId = 1L;
        Authentication authentication = mock(Authentication.class);

        String commentName = "commentName";
        String commentContent = "comment content";

        CreateCommentRequest request = new CreateCommentRequest(commentName,commentContent);
        assertThrows(NotFoundException.class,() -> underTest.createComment(authentication,toDoId,request));
    }

    @Test
    void createCommentUserShouldThrowUnauthorized() {
        Authentication authentication = mock(Authentication.class);

        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);

        String commentName = "commentName";
        String commentContent = "comment content";

        CreateCommentRequest request = new CreateCommentRequest(commentName,commentContent);

        Mockito.when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(UnauthorizedException.class, () -> underTest.createComment(authentication,toDo.getId(),request));

    }

    @Test
    void selectCommentByIdShouldThrowNotFoundExceptionWhenTodoIdDoesNotExists() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);

        assertThrows(NotFoundException.class, ()-> underTest.selectCommentById(authentication,commentId));
    }

    @Test
    void selectCommentByIdShouldThrowUnauthorizedIfNotInProject() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedException.class,() -> underTest.selectCommentById(authentication,commentId));
    }

    @Test
    void selectCommentByIdAsProjectManager() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));
        when(commentDtoMapper.apply(comment)).thenReturn(new CommentDto(comment.getId(),comment.getName(),comment.getContent(),comment.getCreatedAt()));
        CommentDto result = underTest.selectCommentById(authentication,commentId);
        assertEquals(result.name(),comment.getName());
        assertEquals(result.content(),comment.getContent());
        assertEquals(result.creationDate(),comment.getCreatedAt());

    }

    @Test
    void updateCommentShouldThrowNotFoundExceptionIfCommentDoesNotExists() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        UpdateCommentRequest request = new UpdateCommentRequest("test","test");

        assertThrows(NotFoundException.class, ()-> underTest.updateComment(authentication,commentId,request));
    }

    @Test
    void updateCommentShouldThrowBadRequestIfNoChangesFound() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        project.setUsers(List.of(projectManager,user));

        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));
        when(authentication.getPrincipal()).thenReturn(user);

        UpdateCommentRequest request = new UpdateCommentRequest(comment.getName(),comment.getContent());
        assertThrows(BadRequestException.class, ()-> underTest.updateComment(authentication,commentId,request));
    }

    @Test
    void updateCommentShouldWork() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        project.setUsers(List.of(projectManager,user));

        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));
        when(authentication.getPrincipal()).thenReturn(user);
        String newName = "comment.getName()";
        String newContent = "comment.getContent()";

        UpdateCommentRequest request = new UpdateCommentRequest(newName,newContent);
        underTest.updateComment(authentication,commentId,request);

        ArgumentCaptor<Comment> commentArgumentCaptor= ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(commentDao).updateComment(commentArgumentCaptor.capture());
        Comment commentCaptured = commentArgumentCaptor.getValue();

        assertEquals(commentCaptured.getName(),newName);
        assertEquals(commentCaptured.getContent(),newContent);
    }

    @Test
    void updateCommentShouldThrowUnauthorizedException() {
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        project.setUsers(List.of(projectManager));

        when(commentDao.selectCommentById(commentId)).thenReturn(Optional.of(comment));
        when(authentication.getPrincipal()).thenReturn(user);
        String newName = "comment.getName()";
        String newContent = "comment.getContent()";

        UpdateCommentRequest request = new UpdateCommentRequest(newName,newContent);
        assertThrows(UnauthorizedException.class,()->underTest.updateComment(authentication,commentId,request));
    }

    @Test
    void deleteCommentShouldWorkAsUser() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentDao.selectCommentById(comment.getId())).thenReturn(Optional.of(comment));

        underTest.deleteComment(authentication, comment.getId());

        ArgumentCaptor<Long> commentIdCaptor= ArgumentCaptor.forClass(Long.class);

        Mockito.verify(commentDao).deleteCommentById(commentIdCaptor.capture());
        Long LongId = commentIdCaptor.getValue();

        assertEquals(LongId,comment.getId());

    }

    @Test
    void deleteCommentShouldWorkAsProjectManager() {

        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);

        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(commentDao.selectCommentById(comment.getId())).thenReturn(Optional.of(comment));

        underTest.deleteComment(authentication, comment.getId());

        ArgumentCaptor<Long> commentIdCaptor= ArgumentCaptor.forClass(Long.class);

        Mockito.verify(commentDao).deleteCommentById(commentIdCaptor.capture());
        Long LongId = commentIdCaptor.getValue();

        assertEquals(LongId,comment.getId());
    }

    @Test
    void deleteCommentShouldThrowNotFoundException() {
        User user = createFakeUser();
        Long commentId = 1L;
        Authentication authentication = mock(Authentication.class);


        assertThrows(NotFoundException.class,() -> underTest.deleteComment(authentication, commentId));
    }

    @Test
    void deleteCommentShouldThrowUnauthorizedException() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager,feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentDao.selectCommentById(comment.getId())).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedException.class,() -> underTest.deleteComment(authentication, comment.getId()));

    }

    @Test
    void getAllCommentWhereNameContainsAndTodoIsShouldReturnNotFoundIfToDoDoesNotExist() {
        Authentication authentication = mock(Authentication.class);
        assertThrows(
                NotFoundException.class,
                () -> underTest
                        .getAllCommentWhereNameContainsAndToDoIs(
                                authentication,
                                1L,
                                "",
                                null
                        )
        );
    }

    @Test
    void getAllCommentWhereNameContainsAndTodoIsShouldThrowUnauthorizedIfUserIsNotInProject() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager, feature);
        Authentication authentication = mock(Authentication.class);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(user);
        assertThrows(
                UnauthorizedException.class,
                () -> underTest
                        .getAllCommentWhereNameContainsAndToDoIs(
                                authentication,
                                toDo.getId(),
                                "",
                                null
                        )
        );

    }

    @Test
    void getAllCommentWhereNameContainsAndTodoIsShouldWorkIfIsProjectManager() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager, feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        CommentDto commentDto = new CommentDto(comment.getId(),comment.getName(),comment.getContent(),comment.getCreatedAt());

        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));

        when(authentication.getPrincipal()).thenReturn(projectManager);

        when(commentDao.selectAllCommentForTodoWhereNameContains(any(),any(),any())).thenReturn(commentPage);

        when(commentDtoMapper.apply(any()))
                .thenReturn(
                        commentDto
                );

        SearchCommentsResponse response = underTest
                .getAllCommentWhereNameContainsAndToDoIs(
                        authentication,
                        toDo.getId(),
                        "",
                        null);

        assertNotNull(response);
    }

    @Test
    void getAllCommentWhereNameContainsAndToDoIsShouldWorkIfUserIsInProject() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager, feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        CommentDto commentDto = new CommentDto(comment.getId(),comment.getName(),comment.getContent(),comment.getCreatedAt());

        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));

        when(authentication.getPrincipal()).thenReturn(projectManager);

        when(commentDao.selectAllCommentForTodoWhereNameContains(any(),any(),any())).thenReturn(commentPage);

        when(commentDtoMapper.apply(any()))
                .thenReturn(
                        commentDto
                );

        SearchCommentsResponse response = underTest
                .getAllCommentWhereNameContainsAndToDoIs(
                        authentication,
                        toDo.getId(),
                        "",
                        null);

        assertNotNull(response);
    }

    @Test
    void getAllCommentWhereNameContainsAndToDoIsShouldWorkIfUserIsInProjectAndNameIsNull() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager, feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        CommentDto commentDto = new CommentDto(comment.getId(),comment.getName(),comment.getContent(),comment.getCreatedAt());

        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));

        when(authentication.getPrincipal()).thenReturn(projectManager);

        when(commentDao.selectAllCommentForTodoWhereNameContains(any(),any(),any())).thenReturn(commentPage);

        when(commentDtoMapper.apply(any()))
                .thenReturn(
                        commentDto
                );

        SearchCommentsResponse response = underTest
                .getAllCommentWhereNameContainsAndToDoIs(
                        authentication,
                        toDo.getId(),
                        null,
                        null);

        assertNotNull(response);
    }

    @Test
    void getAllCommentWhereNameContainsAndToDoIsShouldThrowBadRequestIfPageDoesNotExists() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Feature feature = createFakeFeature(project);
        ToDo toDo = createFakeTodo(projectManager, feature);
        Comment comment = createFakeComment(toDo);
        Authentication authentication = mock(Authentication.class);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));

        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(commentDao.selectAllCommentForTodoWhereNameContains(any(),any(),any())).thenReturn(commentPage);

        assertThrows(BadRequestException.class, () -> underTest
                .getAllCommentWhereNameContainsAndToDoIs(
                        authentication,
                        toDo.getId(),
                        null,
                        8));
    }
}