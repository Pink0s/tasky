package com.tasky.api.repositories;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * Repository interface for managing Project entities.
 */
public interface ProjectRepository extends JpaRepository<Project,Long> {

    /**
     * Retrieves a page of projects whose names contain the specified pattern.
     *
     * @param name     The pattern to search for in project names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing projects matching the specified name pattern.
     */
    Page<Project> findProjectByNameContaining(String name, Pageable pageable);

    /**
     * Retrieves a page of projects whose names contain the specified pattern and are associated with a specific user.
     *
     * @param name     The pattern to search for in project names.
     * @param user     The User associated with the projects to retrieve.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing projects matching the specified name pattern and associated with the given User.
     */
    Page<Project> findProjectByNameContainingAndUsersContains(String name, User user, Pageable pageable);

    /**
     * Checks if a project with the specified ID exists.
     *
     * @param id The unique identifier (ID) of the project to check.
     * @return true if a project with the given ID exists; otherwise, false.
     */
    boolean existsProjectById(Long id);
}
