package com.tasky.api.dao.run;

import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing run entities.
 */
public interface RunDao {

    /**
     * Creates a new run.
     *
     * @param run The Run object to be created.
     */
    void createRun(Run run);

    /**
     * Retrieves a run by its unique identifier (ID).
     *
     * @param runId The unique identifier (ID) of the run to retrieve.
     * @return An Optional containing the retrieved Run object, or an empty Optional if not found.
     */
    Optional<Run> findRunById(Long runId);

    /**
     * Retrieves a page of runs associated with a specific project, where the run names contain the specified pattern.
     *
     * @param project  The Project associated with the runs to retrieve.
     * @param name     The pattern to search for in run names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing runs associated with the specified project.
     */
    Page<Run> findAllRunWhereProjectIsAndNameContaining(Project project, String name, Pageable pageable);

    /**
     * Updates an existing run.
     *
     * @param run The Run object containing updated information to be saved.
     */
    void updateRun(Run run);

    /**
     * Deletes a run by its unique identifier (ID).
     *
     * @param runId The unique identifier (ID) of the run to be deleted.
     */
    void deleteRunById(Long runId);

}
