package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing Project entities.
 */
public interface ProjectDao {

    /**
     * Inserts a new project into the database.
     *
     * @param project The project entity to be inserted.
     */
    void insertProject(Project project);

    /**
     * Retrieves a project from the database by its ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return An optional containing the retrieved project, or empty if not found.
     */
    Optional<Project> selectProjectById(Long projectId);

    /**
     * Retrieves a paginated list of all projects from the database.
     *
     * @param name     The name pattern to filter projects by.
     * @param pageable The pagination information.
     * @return A page of projects that match the criteria.
     */
    Page<Project> selectAllProject(String name, Pageable pageable);

    /**
     * Retrieves a paginated list of projects associated with a specific user.
     *
     * @param name     The name pattern to filter projects by.
     * @param user     The user associated with the projects.
     * @param pageable The pagination information.
     * @return A page of projects that match the criteria.
     */
    Page<Project> selectAllProjectForUser(String name, User user, Pageable pageable);

    /**
     * Checks if a project with the specified ID exists in the database.
     *
     * @param id The ID of the project to check for existence.
     * @return True if the project exists, false otherwise.
     */
    Boolean isProjectExistsWithId(Long id);

    /**
     * Deletes a project from the database by its ID.
     *
     * @param id The ID of the project to delete.
     */
    void deleteProjectById(Long id);

    /**
     * Updates an existing project in the database.
     *
     * @param project The updated project entity.
     */
    void updateProject(Project project);
}
