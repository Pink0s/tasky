package com.tasky.api.services.feature;

import com.tasky.api.dto.feature.CreateFeatureRequest;
import com.tasky.api.dto.feature.FeatureDto;
import com.tasky.api.dto.feature.SearchFeatureResponse;
import com.tasky.api.dto.feature.UpdateFeatureRequest;
import org.springframework.security.core.Authentication;

/**
 * Service interface for managing features.
 */
public interface FeatureService {

    /**
     * Create a new feature associated with a run.
     *
     * @param authentication The authentication object for the user creating the feature.
     * @param runId          The unique identifier (ID) of the run associated with the feature.
     * @param request        The request object containing feature details.
     */
    void createFeature(Authentication authentication ,Long runId, CreateFeatureRequest request);

    /**
     * Find a feature by its unique identifier (ID).
     *
     * @param authentication The authentication object for the user making the request.
     * @param featureId      The unique identifier (ID) of the feature to retrieve.
     * @return The FeatureDto containing information about the retrieved feature.
     */
    FeatureDto findFeatureById(Authentication authentication, Long featureId);

    /**
     * Find features associated with a run and matching a name pattern.
     *
     * @param authentication The authentication object for the user making the request.
     * @param runId          The unique identifier (ID) of the run associated with the features.
     * @param name           The pattern to search for in feature names.
     * @param page           The page number for pagination.
     * @return A SearchFeatureResponse containing a list of matching features and pagination details.
     */
    SearchFeatureResponse findFeatureWhereRunIsAndNameContaining(Authentication authentication, Long runId, String name, Integer page);

    /**
     * Update an existing feature.
     *
     * @param authentication The authentication object for the user making the request.
     * @param featureId      The unique identifier (ID) of the feature to update.
     * @param request        The request object containing updated feature details.
     */
    void updateFeature(Authentication authentication, Long featureId, UpdateFeatureRequest request);
    /**
     * Delete a feature by its unique identifier (ID).
     *
     * @param authentication The authentication object for the user making the request.
     * @param featureId      The unique identifier (ID) of the feature to delete.
     */
    void deleteFeatureById(Authentication authentication, Long featureId);
}
