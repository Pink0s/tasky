package com.tasky.api.dao.run;

import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.repositories.RunRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of the RunDao interface using Spring Data JPA.
 */
@Repository("RUN_JPA")
public class RunDaoImpl implements RunDao {
    private final RunRepository repository;

    public RunDaoImpl(RunRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRun(Run run) {
        repository.save(run);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Run> findRunById(Long runId) {
        return repository.findById(runId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Run> findAllRunWhereProjectIsAndNameContaining(Project project, String name, Pageable pageable) {
        return repository.findAllByProjectIsAndNameContaining(project,name,pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRun(Run run) {
        repository.save(run);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRunById(Long runId) {
        repository.deleteById(runId);
    }
}
