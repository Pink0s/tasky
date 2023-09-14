package com.tasky.api.controllers;

import com.tasky.api.dto.run.CreateRunRequest;
import com.tasky.api.dto.run.RunDto;
import com.tasky.api.dto.run.SearchRunResponse;
import com.tasky.api.dto.run.UpdateRunRequest;
import com.tasky.api.services.run.RunService;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles HTTP requests related to runs in the Tasky API.
 */
@RestController
@RequestMapping("/api/v1/run")
public class RunController {
    private final Logger logger = LoggerFactory.getLogger(RunController.class);
    private final RunService runService;

    /**
     * Constructor for RunController.
     *
     * @param runService The service responsible for handling run-related operations.
     */
    public RunController(RunService runService) {
        this.runService = runService;
    }

    /**
     * Handles POST requests to create a new run within a project.
     *
     * @param authentication The authentication details of the user making the request.
     * @param projectId      The ID of the project in which the run will be created.
     * @param request        The request body containing the information of the new run.
     */
    @PostMapping("project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    void createRun(Authentication authentication, @PathVariable Long projectId, @RequestBody CreateRunRequest request) {

        logger.info("POST /api/v1/run/project/%s".formatted(projectId));

        runService.createRun(authentication, projectId, request);
    }

    /**
     * Handles GET requests to retrieve a run by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param runId          The ID of the run to retrieve.
     * @return The run DTO corresponding to the given ID.
     */
    @GetMapping("{runId}")
    RunDto getRunById(Authentication authentication, @PathVariable Long runId) {
        logger.info("GET /api/v1/run/%s".formatted(runId));
        return runService.findById(authentication,runId);
    }

    /**
     * Handles GET requests to search for runs within a project based on name pattern and page number.
     *
     * @param authentication The authentication details of the user making the request.
     * @param projectId      The ID of the project in which to search for runs.
     * @param page           The page number for pagination.
     * @param name           The search pattern for run names.
     * @return The search response containing a list of matching runs and pagination information.
     */
    @GetMapping("project/{projectId}")
    SearchRunResponse getAllRunByProjectIdAndByNameContaining(Authentication authentication, @PathVariable Long projectId, @PathParam("page") Integer page, @PathParam("name") String name) {
        logger.info("GET /api/v1/run/project/%s".formatted(projectId));
        return runService.findRunByProjectIdAndByNameContaining(authentication,projectId,name,page);
    }

    /**
     * Handles PATCH requests to update a run by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param runId          The ID of the run to update.
     * @param request        The request body containing the updated run information.
     */
    @PatchMapping("{runId}")
    void updateRunById(Authentication authentication, @PathVariable Long runId, @RequestBody UpdateRunRequest request) {
        logger.info("PATCH /api/v1/run/%s".formatted(runId));
        runService.updateRunById(authentication,request,runId);
    }

    /**
     * Handles DELETE requests to delete a run by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param runId          The ID of the run to delete.
     */
    @DeleteMapping("{runId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRunById(Authentication authentication, @PathVariable Long runId) {
        logger.info("DELETE /api/v1/run/%s".formatted(runId));
        runService.deleteRunById(authentication,runId);
    }
}
