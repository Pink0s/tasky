package com.tasky.api.controllers;

import com.tasky.api.dto.project.*;
import com.tasky.api.services.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles HTTP requests related to projects in the Tasky API.
 */
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    /**
     * Constructor for ProjectController.
     *
     * @param projectService The service responsible for handling project-related operations.
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Handles POST requests to create a new project.
     *
     * @param authentication The authentication details of the user making the request.
     * @param request        The request body containing the information of the new project.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(Authentication authentication, @RequestBody  CreateProjectRequest request) {
        logger.info("POST /api/v1/project");
        projectService.createProject(authentication,request);
    }

    /**
     * Handles PATCH requests to update a project by its ID.
     *
     * @param projectId The ID of the project to update.
     * @param request   The request body containing the updated project information.
     */
    @PatchMapping("{projectId}")
    public void updateProject(@PathVariable Long projectId, @RequestBody UpdateProjectRequest request) {
        logger.info("PATCH /api/v1/project/"+projectId);
        projectService.updateProject(projectId,request);
    }

    /**
     * Handles PATCH requests to add a user to a project.
     *
     * @param projectId The ID of the project to which the user will be added.
     * @param request   The request body containing the user ID to be added to the project.
     */
    @PatchMapping("{projectId}/addUser")
    public void addUser(@PathVariable Long projectId, @RequestBody AddUserToProjectRequest request) {
        logger.info("PATCH /api/v1/project/"+projectId+"/addUser");
        projectService.addUser(projectId, request);
    }

    /**
     * Handles GET requests to retrieve a project by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param projectId      The ID of the project to retrieve.
     * @return The project DTO corresponding to the given ID.
     */
    @GetMapping("{projectId}")
    public ProjectDto getProjectById(Authentication authentication, @PathVariable Long projectId) {
        logger.info("GET /api/v1/project/"+projectId);
        return projectService.findProjectById(authentication, projectId);
    }

    /**
     * Handles GET requests to search for projects based on name pattern and page number.
     *
     * @param authentication The authentication details of the user making the request.
     * @param pattern        The search pattern for project names.
     * @param page           The page number for pagination.
     * @return The search response containing a list of matching projects and pagination information.
     */
    @GetMapping
    public SearchProjectResponse searchProjects(Authentication authentication, @RequestParam(required = false) String pattern, @RequestParam(required = false) Integer page) {
       logger.info("GET /api/v1/project");
       return projectService.findProject(authentication, pattern, page);
    }

    /**
     * Handles DELETE requests to delete a project by its ID.
     *
     * @param projectId The ID of the project to delete.
     */
    @DeleteMapping("{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId) {
        logger.info("DELETE /api/v1/project"+projectId);
        projectService.deleteProjectById(projectId);
    }
}
