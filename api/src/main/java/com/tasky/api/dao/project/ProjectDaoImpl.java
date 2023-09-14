package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data Access Object Implementation (DAO) interface for managing Project entities.
 */
@Repository("PROJECT_JPA")
public class ProjectDaoImpl implements ProjectDao {

    private final ProjectRepository repository;

    public ProjectDaoImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertProject(Project project) {
        repository.save(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Project> selectProjectById(Long projectId) {
        return repository.findById(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Project> selectAllProject(String name, Pageable pageable) {
        return repository.findProjectByNameContaining(name, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Project> selectAllProjectForUser(String name, User user, Pageable pageable) {
        return repository.findProjectByNameContainingAndUsersContains(name, user, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isProjectExistsWithId(Long id) {
        return repository.existsProjectById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectById(Long id) {
        repository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProject(Project project) {
        repository.save(project);
    }
}
