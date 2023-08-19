package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PROJECT_JPA")
public class ProjectDaoImpl implements ProjectDao {

    private final ProjectRepository repository;

    public ProjectDaoImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * @param project
     */
    @Override
    public void insertProject(Project project) {
        repository.save(project);
    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public Optional<Project> selectProjectById(Long projectId) {
        return repository.findById(projectId);
    }

    /**
     * @param name
     * @param pageable
     * @return
     */
    @Override
    public Page<Project> selectAllProject(String name, Pageable pageable) {
        return repository.findProjectByNameContaining(name, pageable);
    }

    /**
     * @param name
     * @param user
     * @param pageable
     * @return
     */
    @Override
    public Page<Project> selectAllProjectForUser(String name, User user, Pageable pageable) {
        return repository.findProjectByNameContainingAndUsersContains(name, user, pageable);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Boolean isProjectExistsWithId(Long id) {
        return repository.existsProjectById(id);
    }

    /**
     * @param id
     */
    @Override
    public void deleteProjectById(Long id) {
        repository.deleteById(id);
    }

    /**
     * @param project
     */
    @Override
    public void updateProject(Project project) {
        repository.save(project);
    }
}
