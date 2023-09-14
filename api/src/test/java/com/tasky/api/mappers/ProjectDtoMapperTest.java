package com.tasky.api.mappers;

import com.tasky.api.dto.project.ProjectDto;
import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectDtoMapperTest {

    private ProjectDtoMapper underTest = new ProjectDtoMapper();

    @Test
    void apply() {
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        String password = "password";
        String projectName = "project";
        String projectDescription = "Short desc";
        Timestamp dueDate = Timestamp.from(Instant.now());

        User user = new User(
                "firstName",
                "firstname",
                "email@email.com",
                "password"
        );

        user.setId(1L);

        Project project = new Project(
                projectName,
                dueDate,
                projectDescription,
                user
        );

        ProjectDto projectDto = underTest.apply(project);

        assertEquals(projectDto.creator(), user.getEmail());
        assertEquals(projectDto.description(), project.getDescription());
        assertEquals(projectDto.name(), project.getName());
        assertEquals(projectDto.dueDate(), project.getDueDate());
    }
}