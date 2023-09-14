package com.tasky.api.dao.run;

import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import com.tasky.api.repositories.RunRepository;
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
class RunDaoImplTest {

    @Mock RunRepository runRepository;
    @InjectMocks RunDaoImpl underTest;

    @Test
    void createRun() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Run run = new Run(
                "runName",
                "Description",
                Timestamp.from(Instant.now().plus(4,ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(8,ChronoUnit.DAYS)),
                project
        );

        underTest.createRun(run);

        verify(runRepository).save(run);
    }

    @Test
    void findRunById() {
        Long runId = 1L;
        underTest.findRunById(runId);
        verify(runRepository).findById(runId);
    }

    @Test
    void findAllRunWhereProjectIsAndNameContaining() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);
        String name = "name";
        Pageable pageable = PageRequest.of(1,10);

        underTest.findAllRunWhereProjectIsAndNameContaining(project,name,pageable);

        verify(runRepository).findAllByProjectIsAndNameContaining(project,name,pageable);
    }

    @Test
    void updateRun() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Run run = new Run(
                "runName",
                "Description",
                Timestamp.from(Instant.now().plus(4,ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(8,ChronoUnit.DAYS)),
                project
        );

        underTest.updateRun(run);

        verify(runRepository).save(run);
    }

    @Test
    void deleteRunById() {
        Long runId = 1L;
        underTest.deleteRunById(runId);
        verify(runRepository).deleteById(runId);
    }
}