package com.tasky.api.dao.feature;

import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.repositories.FeatureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The FeatureDaoImpl implementation for managing interaction with feature data.
 */
@Repository("FEATURE_JPA")
public class FeatureDaoImpl implements FeatureDao {
    private final FeatureRepository repository;

    public FeatureDaoImpl(FeatureRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeature(Feature feature) {
        repository.save(feature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Feature> findFeatureById(Long featureId) {
        return repository.findById(featureId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Feature> findAllFeatureWhereProjectIsAndNameContaining(Project project, String name, Pageable pageable) {
        return repository.findAllByProjectIsAndNameContaining(project,name,pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Feature> findAllFeatureWhereRunIsAndNameContaining(Run run, String name, Pageable pageable) {
        return repository.findAllByRunIsAndNameContaining(run,name,pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeature(Feature feature) {
        repository.save(feature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFeatureById(Long featureId) {
        repository.deleteById(featureId);
    }
}
