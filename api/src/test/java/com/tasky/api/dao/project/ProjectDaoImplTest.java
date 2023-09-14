package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ProjectDaoImplTest {

    @Mock
    ProjectRepository projectRepository;
    @InjectMocks private ProjectDaoImpl underTest;

    @Test
    void insertProject() {
        //GIVEN
        User user = new User(
                "test",
                "test",
                "test@test.com",
                "test"
        );

        user.setId(1L);

        Project project = new Project(
                "test",
                Timestamp.from(Instant.now()),
                "description",
                user);

        //WHEN
        underTest.insertProject(project);

        //THEN
        verify(projectRepository).save(project);

    }

    @Test
    void selectProjectById() {
        Long id = 1L;

        underTest.selectProjectById(id);

        verify(projectRepository).findById(id);
    }

    @Test
    void selectAllProject() {
        String name="test";
        Pageable pageable = PageRequest.of(0,10);

        underTest.selectAllProject(name,pageable);

        verify(projectRepository).findProjectByNameContaining(name,pageable);
    }

    @Test
    void selectAllProjectForUser() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);
        String name = "name";

        Pageable pageable = PageRequest.of(0,10);
        underTest.selectAllProjectForUser(name,user,pageable);

        verify(projectRepository).findProjectByNameContainingAndUsersContains(name,user,pageable);
    }

    @Test
    void isProjectExistsWithId() {
        Long projectId = 1L;
        underTest.isProjectExistsWithId(projectId);
        verify(projectRepository).existsProjectById(projectId);
    }

    @Test
    void deleteProjectById() {
        Long projectId = 1L;
        underTest.deleteProjectById(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void updateProject() {
        User user = new User(
                "test",
                "test",
                "test@test.com",
                "test"
        );

        user.setId(1L);

        Project project = new Project(
                "test",
                Timestamp.from(Instant.now()),
                "description",
                user);

        //WHEN
        underTest.updateProject(project);

        //THEN
        verify(projectRepository).save(project);
    }
}