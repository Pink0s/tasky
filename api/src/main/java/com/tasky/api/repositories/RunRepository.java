package com.tasky.api.repositories;

import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Run entities.
 */
public interface RunRepository extends JpaRepository<Run,Long> {

    /**
     * Retrieves a page of runs associated with a specific project, where the run names contain the specified pattern.
     *
     * @param project  The Project associated with the runs to retrieve.
     * @param name     The pattern to search for in run names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing runs associated with the specified project and matching the name pattern.
     */
    Page<Run> findAllByProjectIsAndNameContaining(Project project, String name, Pageable pageable);
}
