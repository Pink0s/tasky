package com.tasky.api.services.toDo;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.feature.FeatureDao;
import com.tasky.api.dao.toDo.ToDoDao;
import com.tasky.api.dao.user.UserDao;
import com.tasky.api.dto.toDo.CreateToDoRequest;
import com.tasky.api.dto.toDo.SearchToDoResponse;
import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.dto.toDo.UpdateTodoRequest;
import com.tasky.api.mappers.ToDoDtoMapper;
import com.tasky.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock private ToDoDao toDoDao;
    @Mock private FeatureDao featureDao;
    @Mock private ToDoDtoMapper toDoDtoMapper;
    @Mock private UserDao userDao;
    @InjectMocks private TodoServiceImpl underTest;

    @Test
    void createTodoShouldThrowAnErrorIfFeatureIsNotFound() {
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name","task","description");
        Long featureId = 1L;
        assertThrows(NotFoundException.class, () -> underTest.createTodo(authentication,featureId,request));
    }

    @Test
    void createTodoShouldThrowUnauthorizedExceptionIfUserIsNotInProject() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name","task","description");
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));
        assertThrows(UnauthorizedException.class,() -> underTest.createTodo(authentication,feature.getId(),request));

    }

    @Test
    void createTodoShouldWorkIfUserIsInProject() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name","task","description");

        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        ArgumentCaptor<ToDo> toDoArgumentCaptor = ArgumentCaptor.forClass(ToDo.class);
        underTest.createTodo(authentication,feature.getId(),request);

        verify(toDoDao).createTodo(toDoArgumentCaptor.capture());
        ToDo toDo = toDoArgumentCaptor.getValue();

        assertEquals(toDo.getDescription(),request.description());
        assertEquals(toDo.getName(),request.name());
        assertEquals(toDo.getType(),request.type());

    }

    @Test
    void createTodoShouldThrowBadRequestIfTypeIsNotCorrect() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name","test","description");

        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        assertThrows(BadRequestException.class,() ->underTest.createTodo(authentication,feature.getId(),request));
    }

    @Test
    void createTodoShouldThrowBadRequestIfNameIsNull() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest(null,"task","description");

        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        assertThrows(BadRequestException.class,() ->underTest.createTodo(authentication,feature.getId(),request));
    }

    @Test
    void createTodoShouldThrowBadRequestIfDescriptionIsNull() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name","task",null);

        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        assertThrows(BadRequestException.class,() ->underTest.createTodo(authentication,feature.getId(),request));
    }

    @Test
    void createTodoShouldThrowBadRequestIfTypeIsNull() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        Authentication authentication = mock(Authentication.class);
        CreateToDoRequest request = new CreateToDoRequest("name",null,"null");

        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        assertThrows(BadRequestException.class,() ->underTest.createTodo(authentication,feature.getId(),request));
    }

    @Test
    void findToDoByIdShouldThrowNotFoundExceptionWhenTodoDoesNotExists() {
        Authentication authentication = mock(Authentication.class);
        assertThrows(NotFoundException.class,() ->underTest.findToDoById(authentication,1L));
    }

    @Test
    void findToDoByIdShouldThrowUnauthorizedExceptionIfUserHasNoRightOnResource() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);
        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));
        assertThrows(UnauthorizedException.class,() -> underTest.findToDoById(authentication,toDo.getId()));
    }

    @Test
    void findToDoByIdShouldWork() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDoById(toDo.getId())).thenReturn(Optional.of(toDo));

        ArgumentCaptor<ToDo> todoArgumentCaptor = ArgumentCaptor.forClass(ToDo.class);
        underTest.findToDoById(authentication,toDo.getId());

        verify(toDoDtoMapper).apply(todoArgumentCaptor.capture());

        ToDo toDoCaptured = todoArgumentCaptor.getValue();

        assertEquals(toDoCaptured,toDo);
    }

    @Test
    void updateTodoShouldThrowNotExceptionIfToDoDoesNotExists() {
        Authentication authentication = mock(Authentication.class);
        Long toDoId = 1L;
        UpdateTodoRequest request = new UpdateTodoRequest("name","task","description", 1L, "New");
        assertThrows(NotFoundException.class, () -> underTest.updateTodo(authentication,toDoId,request));
    }

    @Test
    void updateTodoShouldThrowUnauthorizedIfUserHasNoAccessToResource() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));

        UpdateTodoRequest request = new UpdateTodoRequest("name","task","description", 1L,"In Progress");

        assertThrows(UnauthorizedException.class, () -> underTest.updateTodo(authentication,toDo.getId(),request));
    }

    @Test
    void updateTodoShouldThrowBadRequestIfNoChanges() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));

        UpdateTodoRequest request = new UpdateTodoRequest(toDo.getName(),toDo.getType(),toDo.getDescription(), null, null);

        assertThrows(BadRequestException.class, () -> underTest.updateTodo(authentication,toDo.getId(),request));
    }


    @Test
    void updateTodoShouldThrowBadRequestIfTypeIsWrong() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));

        UpdateTodoRequest request = new UpdateTodoRequest(toDo.getName(),"toDo.getType()",toDo.getDescription(), null, "In progress");

        assertThrows(BadRequestException.class, () -> underTest.updateTodo(authentication,toDo.getId(),request));
    }

    @Test
    void updateTodoShouldWork() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,projectManager);

        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(userDao.selectUserById(any())).thenReturn(Optional.of(user));
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));

        UpdateTodoRequest request = new UpdateTodoRequest("new","bug","ezrazerazerze", 1L, "In progress");

        ArgumentCaptor<ToDo> toDoArgumentCaptor = ArgumentCaptor.forClass(ToDo.class);

        underTest.updateTodo(authentication,toDo.getId(),request);
        verify(toDoDao).updateTodo(toDoArgumentCaptor.capture());

        ToDo capturedTodo = toDoArgumentCaptor.getValue();

        assertEquals(request.description(), capturedTodo.getDescription());
        assertEquals(request.type(), capturedTodo.getType());
        assertEquals(request.name(), capturedTodo.getName());
        assertEquals(user,capturedTodo.getUser());
    }

    @Test
    void updateTodoShouldThrowBadRequestWhenStatusIsWrong() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,projectManager);

        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(userDao.selectUserById(any())).thenReturn(Optional.of(user));
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));

        UpdateTodoRequest request = new UpdateTodoRequest("new","bug","ezrazerazerze", 1L, "azer");
        assertThrows(BadRequestException.class,() ->underTest.updateTodo(authentication,toDo.getId(),request));
    }

    @Test
    void deleteToDoByIdShouldThrowNotFoundExceptionIfToDoIdDoesNotExists() {
        Authentication authentication = mock(Authentication.class);
        Long toDoId = 1L;
        assertThrows(NotFoundException.class, () -> underTest.deleteToDoById(authentication,toDoId));
    }

    @Test
    void deleteToDoByIdShouldThrowUnauthorizedExceptionWhenUserHasNoAccess() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(user);
        assertThrows(UnauthorizedException.class, () -> underTest.deleteToDoById(authentication,toDo.getId()));
    }

    @Test
    void deleteToDoByIdShouldWork() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager,user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature,user);
        when(toDoDao.selectToDoById(any())).thenReturn(Optional.of(toDo));
        when(authentication.getPrincipal()).thenReturn(user);

        ArgumentCaptor<Long> toDoIdArgCaptor = ArgumentCaptor.forClass(Long.class);
        underTest.deleteToDoById(authentication,toDo.getId());
        verify(toDoDao).deleteTodoById(toDoIdArgCaptor.capture());
        Long toDoId = toDoIdArgCaptor.getValue();

        assertEquals(toDoId,toDo.getId());
    }

    private User createFakeUser() {
        User user = new User("John","Doe","test@test.com","password");
        user.setId(2L);
        return user;
    }

    private User createFakeProjectManager() {
        User user = new User("John","Doe","testus@test.com","password");
        user.setId(1L);
        user.setRole("PROJECT_MANAGER");
        return user;
    }

    private Project createFakeProject(User owner) {
        Project project = new Project(
                "name",
                Timestamp.from(
                        Instant.now()
                                .plus(5, ChronoUnit.DAYS)
                ),
                owner
        );

        project.setId(1L);
        project.setUsers(List.of(owner));
        return project;
    }

    private Feature createFakeFeature(Run run, Project project) {
        Feature feature = new Feature(
                "name",
                "description",
                run,
                project
        );

        feature.setId(1L);

        return feature;
    }

    private Run createFakeRun(Project project) {
        Run run = new Run(
                "name",
                "description",
                Timestamp.from(
                        Instant.now()
                ),
                Timestamp.from(
                        Instant.now().plus(5,ChronoUnit.DAYS)
                ),
                project
        );

        run.setId(1L);

        return run;
    }

    private ToDo createFakeTodo(Feature feature, User user) {
        ToDo toDo = new ToDo("name","task","description",feature,user);
        toDo.setId(1L);
        return  toDo;
    }

    @Test
    void findTodosWhereFeatureIsAndNameContains() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature, user);
        List<ToDo> toDos = List.of(toDo);
        Page<ToDo> runPage = new PageImpl<>(toDos);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectAllToDoWhereFeatureIdIsAndNameContaining(any(),any(),any())).thenReturn(runPage);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));
        SearchToDoResponse response = underTest.findTodosWhereFeatureIsAndNameContains(authentication,feature.getId(),"aezra",0);
        assertNotNull(response);
    }

    @Test
    void findTodosWhereFeatureIsAndNameContainsShouldThrowBadRequest() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature, user);
        List<ToDo> toDos = List.of(toDo);
        Page<ToDo> runPage = new PageImpl<>(toDos);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectAllToDoWhereFeatureIdIsAndNameContaining(any(),any(),any())).thenReturn(runPage);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));
        assertThrows(BadRequestException.class, () -> underTest.findTodosWhereFeatureIsAndNameContains(authentication,feature.getId(),"aezra",23));

    }

    @Test
    void findTodosWhereUserIsAnNameContains() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature, user);
        List<ToDo> toDos = List.of(toDo);
        Page<ToDo> runPage = new PageImpl<>(toDos);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDosWhereUserIsAndNameContaining(any(),any(),any())).thenReturn(runPage);
        SearchToDoResponse response = underTest.findTodosWhereUserIsAnNameContains(authentication,"nameeazr",0);

        assertNotNull(response);
    }

    @Test
    void findTodosWhereUserIsAnNameContainsShouldThrowBadRequest() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run,project);
        ToDo toDo = createFakeTodo(feature, user);
        List<ToDo> toDos = List.of(toDo);
        Page<ToDo> runPage = new PageImpl<>(toDos);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(toDoDao.selectToDosWhereUserIsAndNameContaining(any(),any(),any())).thenReturn(runPage);
        assertThrows(BadRequestException.class,() -> underTest.findTodosWhereUserIsAnNameContains(authentication,"nameeazr",23));


    }
}