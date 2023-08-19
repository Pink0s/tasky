package com.tasky.api.mappers;

import com.tasky.api.dto.project.ProjectDto;
import com.tasky.api.models.Project;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ProjectDtoMapper implements Function<Project, ProjectDto> {
    /**
     * @param project the function argument
     * @return
     */
    @Override
    public ProjectDto apply(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDueDate(),
                project.getDescription(),
                project.getUser().getEmail()
        );
    }
}
