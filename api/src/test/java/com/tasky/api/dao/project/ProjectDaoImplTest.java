package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}