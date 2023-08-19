package com.tasky.api.controllers;

import com.tasky.api.dto.project.*;
import com.tasky.api.services.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(Authentication authentication, @RequestBody  CreateProjectRequest request) {
        logger.info("POST /api/v1/project");
        projectService.createProject(authentication,request);
    }

    @PatchMapping("{projectId}")
    public void updateProject(@PathVariable Long projectId, @RequestBody UpdateProjectRequest request) {
        logger.info("PATCH /api/v1/project/"+projectId);
        projectService.updateProject(projectId,request);
    }

    @PatchMapping("{projectId}/addUser")
    public void addUser(@PathVariable Long projectId, @RequestBody AddUserToProjectRequest request) {
        logger.info("PATCH /api/v1/project/"+projectId);
        projectService.addUser(projectId, request);
    }

    @GetMapping("{projectId}")
    public ProjectDto getProjectById(Authentication authentication, @PathVariable Long projectId) {
        logger.info("GET /api/v1/project/"+projectId);
        return projectService.findProjectById(authentication, projectId);
    }

    @GetMapping
    public SearchProjectResponse searchProjects(Authentication authentication, @RequestParam(required = false) String pattern, @RequestParam(required = false) Integer page) {
       logger.info("GET /api/v1/project");
       return projectService.findProject(authentication, pattern, page);
    }

    @DeleteMapping("{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        logger.info("DELETE /api/v1/project"+projectId);
        projectService.deleteProjectById(projectId);
    }
}
