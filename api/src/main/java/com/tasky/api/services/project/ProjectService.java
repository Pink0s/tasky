package com.tasky.api.services.project;

import com.tasky.api.dto.project.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;

public interface ProjectService {

    /**
     * Creates a new project.
     *
     * @param authentication The authentication object representing the current user.
     * @param request        The CreateProjectRequest object containing project creation details.
     */
    void createProject(Authentication authentication, CreateProjectRequest request);

    /**
     * Updates an existing project.
     *
     * @param projectId The unique identifier of the project to be updated.
     * @param request   The UpdateProjectRequest object containing updated project information.
     */
    void updateProject(Long projectId, UpdateProjectRequest request);

    /**
     * Adds a user to a project.
     *
     * @param projectId The unique identifier of the project to which the user will be added.
     * @param request   The AddUserToProjectRequest object containing the user's identifier.
     */
    void addUser(Long projectId, AddUserToProjectRequest request);

    /**
     * Retrieves a project by its unique identifier.
     *
     * @param authentication The authentication object representing the current user.
     * @param projectId      The unique identifier of the project to retrieve.
     * @return A ProjectDto object containing project details.
     */
    ProjectDto findProjectById(Authentication authentication, Long projectId);

    /**
     * Searches for projects based on optional pattern and pagination.
     *
     * @param authentication The authentication object representing the current user.
     * @param pattern        The optional pattern to search for in project names or descriptions.
     * @param page           The optional page number for pagination.
     * @return A SearchProjectResponse object containing matching project details and pagination information.
     */
    SearchProjectResponse findProject(Authentication authentication,  @Nullable String pattern, @Nullable Integer page);

    /**
     * Deletes a project by its unique identifier.
     *
     * @param projectId The unique identifier of the project to be deleted.
     */
    void deleteProjectById(Long projectId);
}
