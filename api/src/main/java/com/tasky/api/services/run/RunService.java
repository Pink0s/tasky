package com.tasky.api.services.run;

import com.tasky.api.dto.run.CreateRunRequest;
import com.tasky.api.dto.run.RunDto;
import com.tasky.api.dto.run.SearchRunResponse;
import com.tasky.api.dto.run.UpdateRunRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

/**
 * Service interface for managing run-related operations.
 */
public interface RunService {

    /**
     * Creates a new run for a project.
     *
     * @param authentication The authentication object representing the current user.
     * @param projectId      The unique identifier of the project associated with the run.
     * @param request        The CreateRunRequest object containing run creation details.
     */
    void createRun(Authentication authentication, Long projectId, CreateRunRequest request);

    /**
     * Retrieves a run by its unique identifier.
     *
     * @param authentication The authentication object representing the current user.
     * @param runId          The unique identifier of the run to retrieve.
     * @return A RunDto object containing run details.
     */
    RunDto findById(Authentication authentication, Long runId);

    /**
     * Searches for runs within a project based on name pattern and pagination.
     *
     * @param authentication The authentication object representing the current user.
     * @param projectId      The unique identifier of the project associated with the runs.
     * @param name           The pattern to search for in run names.
     * @param page           The optional page number for pagination.
     * @return A SearchRunResponse object containing matching run details and pagination information.
     */
    SearchRunResponse findRunByProjectIdAndByNameContaining(Authentication authentication, Long projectId, String name, Integer page);

    /**
     * Updates an existing run by its unique identifier.
     *
     * @param authentication The authentication object representing the current user.
     * @param request        The UpdateRunRequest object containing updated run information.
     * @param runId          The unique identifier of the run to be updated.
     */
    void updateRunById(Authentication authentication, UpdateRunRequest request, Long runId);

    /**
     * Deletes a run by its unique identifier.
     *
     * @param authentication The authentication object representing the current user.
     * @param runId          The unique identifier of the run to be deleted.
     */
    void deleteRunById(Authentication authentication, Long runId);
}
