package com.tasky.api.mappers;

import com.tasky.api.dto.run.RunDto;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RunDtoMapperTest {

    private RunDtoMapper underTest = new RunDtoMapper();

    @Test
    void apply() {
        User projectManager = new User(
                "firstname",
                "lastname",
                "email@email.com",
                "password"
        );
        projectManager.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(
                        Instant.now()
                ),
                "descr",
                projectManager
        );

        project.setId(1L);

        Run run = new Run(
                "name",
                "description",
                Timestamp.from(
                        Instant.now()
                ),
                Timestamp.from(
                        Instant.now()
                ),
                project
        );

        run.setId(1L);

        RunDto runDto = underTest.apply(run);

        assertEquals(runDto.runId(),run.getId());
        assertEquals(runDto.endDate(),run.getEndDate());
        assertEquals(runDto.startDate(),run.getStartDate());
        assertEquals(runDto.name(),run.getName());
        assertEquals(runDto.status(),run.getStatus());
        assertEquals(runDto.description(),run.getDescription());
    }

}