package com.tasky.api.controllers;

import com.tasky.api.dto.feature.CreateFeatureRequest;
import com.tasky.api.dto.feature.FeatureDto;
import com.tasky.api.dto.feature.SearchFeatureResponse;
import com.tasky.api.dto.feature.UpdateFeatureRequest;
import com.tasky.api.services.feature.FeatureService;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles HTTP requests related to features in the Tasky API.
 */
@RestController
@RequestMapping("/api/v1/feature")
public class FeatureController {
    private final Logger logger = LoggerFactory.getLogger(FeatureController.class);
    private final FeatureService featureService;

    /**
     * Constructor for FeatureController.
     *
     * @param featureService The service responsible for handling feature-related operations.
     */
    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    /**
     * Handles POST requests to create a new feature within a run.
     *
     * @param authentication The authentication details of the user making the request.
     * @param request        The request body containing the information of the new feature.
     * @param runId          The ID of the run to which the feature belongs.
     */
    @PostMapping("run/{runId}")
    @ResponseStatus(HttpStatus.CREATED)
    void createFeature(Authentication authentication, @RequestBody CreateFeatureRequest request, @PathVariable Long runId) {
        logger.info("POST /api/v1/feature/run/%s".formatted(runId));
        featureService.createFeature(authentication,runId,request);
    }

    /**
     * Handles GET requests to retrieve a feature by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param featureId      The ID of the feature to retrieve.
     * @return The feature DTO corresponding to the given ID.
     */
    @GetMapping("{featureId}")
    FeatureDto getFeatureById(Authentication authentication, @PathVariable Long featureId) {
        logger.info("GET /api/v1/feature/%s".formatted(featureId));
        return featureService.findFeatureById(authentication,featureId);
    }

    /**
     * Handles GET requests to search for features within a run based on name and page.
     *
     * @param authentication The authentication details of the user making the request.
     * @param runId          The ID of the run to which the features belong.
     * @param page           The page number for pagination.
     * @param name           The search pattern for feature names.
     * @return The search response containing a list of matching features and pagination information.
     */
    @GetMapping("run/{runId}")
    SearchFeatureResponse getFeatureWhereRunIsAndNameContaining(Authentication authentication, @PathVariable Long runId, @PathParam("page") Integer page, @PathParam("name") String name) {
        logger.info("GET /api/v1/feature/run/%s".formatted(runId));
        return featureService
                .findFeatureWhereRunIsAndNameContaining(
                        authentication,
                        runId,
                        name,
                        page
                );
    }

    /**
     * Handles PATCH requests to update a feature by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param request        The request body containing the updated feature information.
     * @param featureId      The ID of the feature to update.
     */
    @PatchMapping("{featureId}")
    void updateFeatureById(Authentication authentication, @RequestBody UpdateFeatureRequest request, @PathVariable Long featureId) {
        logger.info("PATCH /api/v1/feature/%s".formatted(featureId));
        featureService.updateFeature(authentication,featureId,request);
    }

    /**
     * Handles DELETE requests to delete a feature by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param featureId      The ID of the feature to delete.
     */
    @DeleteMapping("{featureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteFeatureById(Authentication authentication, @PathVariable Long featureId) {
        logger.info("DELETE /api/v1/feature/%s".formatted(featureId));
        featureService.deleteFeatureById(authentication, featureId);
    }

}
