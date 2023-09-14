package com.tasky.api.dao.feature;

import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The FeatureDao interface defines methods to interact with feature data.
 */
public interface FeatureDao {

    /**
     * Creates a new feature.
     *
     * @param feature The Feature object to be created.
     */
    void createFeature(Feature feature);

    /**
     * Retrieves a feature by its unique identifier (ID).
     *
     * @param featureId The unique identifier (ID) of the feature to retrieve.
     * @return An Optional containing the retrieved Feature object, or an empty Optional if not found.
     */
    Optional<Feature> findFeatureById(Long featureId);

    /**
     * Retrieves a page of features associated with a specific project, where the feature's name contains the specified pattern.
     *
     * @param project  The Project associated with the features to retrieve.
     * @param name     The pattern to search for in feature names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing features associated with the specified project that match the name pattern.
     */
    Page<Feature> findAllFeatureWhereProjectIsAndNameContaining(Project project, String name, Pageable pageable);

    /**
     * Retrieves a page of features associated with a specific run, where the feature's name contains the specified pattern.
     *
     * @param run      The Run associated with the features to retrieve.
     * @param name     The pattern to search for in feature names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing features associated with the specified run that match the name pattern.
     */
    Page<Feature> findAllFeatureWhereRunIsAndNameContaining(Run run, String name, Pageable pageable);

    /**
     * Updates an existing feature.
     *
     * @param feature The Feature object containing updated information to be saved.
     */
    void updateFeature(Feature feature);

    /**
     * Deletes a feature by its unique identifier (ID).
     *
     * @param featureId The unique identifier (ID) of the feature to be deleted.
     */
    void deleteFeatureById(Long featureId);
}
