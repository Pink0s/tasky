package com.tasky.api.services.project;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.project.ProjectDao;
import com.tasky.api.dao.user.UserDao;
import com.tasky.api.dto.project.*;
import com.tasky.api.dto.user.UserDto;
import com.tasky.api.mappers.ProjectDtoMapper;
import com.tasky.api.models.Comment;
import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock private ProjectDao projectDao;
    @Mock private UserDao userDao;
    @Mock private ProjectDtoMapper projectDtoMapper;

    @InjectMocks private ProjectServiceImpl underTest;

    @Test
    void createProject() {
        // GIVEN
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        String projectName = "Project Name";
        Long projectDueDate =  1692881836L;
        String projectDescription = "Project Description";

        CreateProjectRequest request = new CreateProjectRequest(projectName, projectDueDate, projectDescription);

        // WHEN
        underTest.createProject(authentication, request);

        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        Mockito.verify(projectDao).insertProject(projectArgumentCaptor.capture());
        Project projectCaptured = projectArgumentCaptor.getValue();

        assertEquals(projectCaptured.getName(),projectName);
        assertEquals(projectCaptured.getDescription(),projectDescription);
        assertEquals(projectCaptured.getUser().getId(),user.getId());

    }

    @Test
    void createProjectWithoutDescription() {
        // GIVEN
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        String projectName = "Project Name";
        Long projectDueDate = 1692881836L;

        CreateProjectRequest request = new CreateProjectRequest(projectName, projectDueDate, null);

        // WHEN
        underTest.createProject(authentication, request);

        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        Mockito.verify(projectDao).insertProject(projectArgumentCaptor.capture());
        Project projectCaptured = projectArgumentCaptor.getValue();

        assertEquals(projectCaptured.getName(),projectName);
        assertEquals(projectCaptured.getUser().getId(),user.getId());

    }

    @Test void createProjectShouldThrowBadRequestExceptionWhenMissingProjectName(){
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        Long projectDueDate =  1692881836L;
        String projectDescription = "Project Description";

        CreateProjectRequest request = new CreateProjectRequest(null, projectDueDate, projectDescription);

        assertThrows(BadRequestException.class,() -> underTest.createProject(authentication, request));
    }

    @Test void createProjectShouldThrowBadRequestExceptionWhenMissingProjectDueDate(){
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        String projectName = "Project Name";
        String projectDescription = "Project Description";

        CreateProjectRequest request = new CreateProjectRequest(projectName, null, projectDescription);

        assertThrows(BadRequestException.class,() -> underTest.createProject(authentication, request));
    }
    @Test void createProjectShouldThrowBadRequestExceptionWhenMissingBody(){
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);
        assertThrows(BadRequestException.class,() -> underTest.createProject(authentication, null));
    }

    @Test
    void updateProject() {
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        UpdateProjectRequest request = new UpdateProjectRequest("earerzer","faezfafzezf","In progress",1692881832L);
        underTest.updateProject(project.getId(),request);
        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectDao).updateProject(argumentCaptor.capture());
        Project capturedProject = argumentCaptor.getValue();
        assertEquals(capturedProject.getName(),request.name());
        assertEquals(capturedProject.getDescription(),request.description());
        assertEquals(capturedProject.getStatus(),request.status());
    }

    @Test
    void updateProjectShouldThrowBadRequestWhenStatusIsWrong() {
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        UpdateProjectRequest request = new UpdateProjectRequest("earerzer","faezfafzezf","In aezrazer",1692881832L);
        assertThrows(BadRequestException.class, () -> underTest.updateProject(project.getId(),request));

    }

    @Test
    void updateProjectShouldThrowBadRequestIfNotChangeFound() {
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        UpdateProjectRequest request = new UpdateProjectRequest(project.getName(),project.getDescription(),project.getStatus(),null);
        assertThrows(BadRequestException.class, () -> underTest.updateProject(project.getId(),request));
    }


    @Test
    void updateProjectShouldThrowNotFoundIfProjectDoesNotExists() {
        assertThrows(NotFoundException.class,() -> underTest.updateProject(1L,new UpdateProjectRequest("name","desc","In progress",1692881836L)));
    }


    @Test
    void addUser() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        AddUserToProjectRequest request = new AddUserToProjectRequest(user.getId());

        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        when(userDao.selectUserById(user.getId())).thenReturn(Optional.of(user));

        underTest.addUser(project.getId(),request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDao).updateUser(userArgumentCaptor.capture());

        User userCaptured = userArgumentCaptor.getValue();

        assertTrue(userCaptured.getProjects().contains(project));
    }

    @Test
    void addUserShouldThrowBadRequestIfUserIdIsMissing() {
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        AddUserToProjectRequest request = new AddUserToProjectRequest(null);
        assertThrows(BadRequestException.class, () -> underTest.addUser(project.getId(),request));
    }

    @Test
    void addUserShouldThrowNotFoundExceptionIfUserIsNotFound() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        AddUserToProjectRequest request = new AddUserToProjectRequest(user.getId());

        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));

        assertThrows(NotFoundException.class, () -> underTest.addUser(project.getId(),request));

    }

    @Test
    void findProjectById() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(any())).thenReturn(Optional.of(project));

        underTest.findProjectById(authentication,project.getId());

        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectDtoMapper).apply(projectArgumentCaptor.capture());
        Project projectCaptured = projectArgumentCaptor.getValue();

        assertEquals(projectCaptured,project);


    }

    @Test
    void findProjectByIdShouldThrowUnauthorizedException() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(projectManager));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(any())).thenReturn(Optional.of(project));

        assertThrows(UnauthorizedException.class, () -> underTest.findProjectById(authentication,project.getId()));

    }



    @Test
    void findProject() {
        User projectManager = createFakeProjectManager();
        Project project1 = createFakeProject(projectManager);
        Project project2 = createFakeProject(projectManager);
        project2.setId(2L);
        Project project3 = createFakeProject(projectManager);
        project3.setId(3L);
        List<Project> projects = List.of(project1,project2,project3);
        Page<Project> projectPage = new PageImpl<>(projects);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(projectDao.selectAllProject(any(),any())).thenReturn(projectPage);
        when(projectDtoMapper.apply(any())).thenReturn(new ProjectDto(project1.getId(), project1.getName(),project1.getDueDate(),project1.getDescription(),project1.getUser().getEmail(),null));

       SearchProjectResponse response = underTest.findProject(authentication , "",0);
       assertNotNull(response);
    }

    @Test
    void findProjectWithUser() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project1 = createFakeProject(projectManager);
        project1.setUsers(List.of(user));
        List<Project> projects = List.of(project1);
        Page<Project> projectPage = new PageImpl<>(projects);
        String pattern = "";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        Pageable pageable = PageRequest.of(0,5);

        when(projectDao.selectAllProjectForUser(pattern,user,pageable)).thenReturn(projectPage);
        when(projectDtoMapper.apply(any())).thenReturn(new ProjectDto(project1.getId(), project1.getName(),project1.getDueDate(),project1.getDescription(),project1.getUser().getEmail(),List.of(new UserDto(user.getId(), user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole(),user.getNeverConnected()))));

        SearchProjectResponse response = underTest.findProject(authentication , pattern,0);
        assertNotNull(response);
    }

    @Test
    void findProjectWithUserShouldThrowBadRequestIfPageDoesNotExist() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project1 = createFakeProject(projectManager);
        project1.setUsers(List.of(user));
        List<Project> projects = List.of(project1);
        Page<Project> projectPage = new PageImpl<>(projects);
        String pattern = "";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        Pageable pageable = PageRequest.of(8,5);

        when(projectDao.selectAllProjectForUser(pattern,user,pageable)).thenReturn(projectPage);
        when(projectDtoMapper.apply(any())).thenReturn(new ProjectDto(project1.getId(), project1.getName(),project1.getDueDate(),project1.getDescription(),project1.getUser().getEmail(),List.of(new UserDto(user.getId(), user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole(),user.getNeverConnected()))));

       assertThrows(BadRequestException.class, () -> underTest.findProject(authentication , pattern,8));

    }

    @Test
    void findProjectShouldThrowBadRequest() {
        User projectManager = createFakeProjectManager();
        Project project1 = createFakeProject(projectManager);
        Project project2 = createFakeProject(projectManager);
        project2.setId(2L);
        Project project3 = createFakeProject(projectManager);
        project3.setId(3L);
        List<Project> projects = List.of(project1,project2,project3);
        Page<Project> projectPage = new PageImpl<>(projects);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(projectManager);
        when(projectDao.selectAllProject(any(),any())).thenReturn(projectPage);

        assertThrows(BadRequestException.class, () ->  underTest.findProject(authentication , "",32));

    }

    @Test
    void deleteProjectById() {
        Long projectId = 1L;
        when(projectDao.isProjectExistsWithId(projectId)).thenReturn(true);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        underTest.deleteProjectById(projectId);
        verify(projectDao).deleteProjectById(argumentCaptor.capture());
        Long idCaptured = argumentCaptor.getValue();
        assertEquals(projectId,idCaptured);
    }

    @Test
    void deleteProjectByIdShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> underTest.deleteProjectById(1L));
    }

    private Project createFakeProject(User user) {
        var date = Instant.ofEpochSecond(1692881838L).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Project project = new Project("name",Timestamp.valueOf(date),"description",user);
        project.setId(1L);
        return project;
    }

    private User createFakeUser() {
        User user = new User("firstname","lastname","email@com.com","password");
        user.setId(2L);
        return user;
    }

    private User createFakeProjectManager() {
        User user = new User("firstname","lastname","email@crrm.com","password");
        user.setId(1L);
        user.setRole("PROJECT_MANAGER");
        return user;
    }

}