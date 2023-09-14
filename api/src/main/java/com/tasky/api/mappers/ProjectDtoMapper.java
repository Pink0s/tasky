package com.tasky.api.mappers;

import com.tasky.api.dto.project.ProjectDto;
import com.tasky.api.models.Project;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Mapper class to convert a Project entity to a ProjectDto.
 */
@Service
public class ProjectDtoMapper implements Function<Project, ProjectDto> {
    private final UserDtoMapper userDtoMapper = new UserDtoMapper();

    /**
     * Converts a Project entity to a ProjectDto.
     *
     * @param project The Project entity to be mapped.
     * @return A ProjectDto object containing the mapped attributes from the Project entity.
     */
    @Override
    public ProjectDto apply(Project project) {

        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDueDate(),
                project.getDescription(),
                project.getUser().getEmail(),
                project.getUsers().stream().map(userDtoMapper).toList()
        );
    }
}
