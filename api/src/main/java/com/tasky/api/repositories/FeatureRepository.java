package com.tasky.api.repositories;

import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Feature entities.
 */
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    /**
     * Retrieves a page of features associated with a specific Project, where the feature names contain the specified pattern.
     *
     * @param project  The Project associated with the features to retrieve.
     * @param name     The pattern to search for in feature names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing features associated with the specified Project.
     */
    Page<Feature> findAllByProjectIsAndNameContaining(Project project, String name, Pageable pageable);
    /**
     * Retrieves a page of features associated with a specific Run, where the feature names contain the specified pattern.
     *
     * @param run      The Run associated with the features to retrieve.
     * @param name     The pattern to search for in feature names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing features associated with the specified Run.
     */
    Page<Feature> findAllByRunIsAndNameContaining(Run run, String name, Pageable pageable);
}
