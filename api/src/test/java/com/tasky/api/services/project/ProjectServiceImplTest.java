package com.tasky.api.services.project;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.dao.project.ProjectDao;
import com.tasky.api.dao.project.ProjectDaoImpl;
import com.tasky.api.dto.project.CreateProjectRequest;
import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock private ProjectDao projectDao;
    @InjectMocks private ProjectServiceImpl underTest;

    @Test
    void createProject() {
        // GIVEN
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        String projectName = "Project Name";
        Timestamp projectDueDate = Timestamp.from(Instant.now());
        String projectDescription = "Project Description";

        CreateProjectRequest request = new CreateProjectRequest(projectName, projectDueDate, projectDescription);

        // WHEN
        underTest.createProject(authentication, request);

        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        Mockito.verify(projectDao).insertProject(projectArgumentCaptor.capture());
        Project projectCaptured = projectArgumentCaptor.getValue();

        assertEquals(projectCaptured.getName(),projectName);
        assertEquals(projectCaptured.getDescription(),projectDescription);
        assertEquals(projectCaptured.getDueDate(),projectDueDate);
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
        Timestamp projectDueDate = Timestamp.from(Instant.now());

        CreateProjectRequest request = new CreateProjectRequest(projectName, projectDueDate, null);

        // WHEN
        underTest.createProject(authentication, request);

        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        Mockito.verify(projectDao).insertProject(projectArgumentCaptor.capture());
        Project projectCaptured = projectArgumentCaptor.getValue();

        assertEquals(projectCaptured.getName(),projectName);
        assertEquals(projectCaptured.getDueDate(),projectDueDate);
        assertEquals(projectCaptured.getUser().getId(),user.getId());

    }

    @Test void createProjectShouldThrowBadRequestExceptionWhenMissingProjectName(){
        Authentication authentication = mock(Authentication.class);
        User user = new User("test","test","test@test.com","password");
        user.setId(1L);
        when(authentication.getPrincipal()).thenReturn(user);

        Timestamp projectDueDate = Timestamp.from(Instant.now());
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
    }

    @Test
    void addUser() {
    }

    @Test
    void findProjectById() {
    }

    @Test
    void findProject() {
    }

    @Test
    void deleteProjectById() {
    }
}