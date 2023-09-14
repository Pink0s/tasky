package com.tasky.api.services.feature;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.feature.FeatureDao;
import com.tasky.api.dao.run.RunDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.feature.CreateFeatureRequest;
import com.tasky.api.dto.feature.FeatureDto;
import com.tasky.api.dto.feature.SearchFeatureResponse;
import com.tasky.api.dto.feature.UpdateFeatureRequest;
import com.tasky.api.mappers.FeatureDtoMapper;
import com.tasky.api.models.Feature;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing features.
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    private final RunDao runDao;
    private final FeatureDao featureDao;
    private final FeatureDtoMapper featureDtoMapper;

    public FeatureServiceImpl(RunDao runDao, FeatureDao featureDao, FeatureDtoMapper featureDtoMapper) {
        this.runDao = runDao;
        this.featureDao = featureDao;
        this.featureDtoMapper = featureDtoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeature(Authentication authentication, Long runId ,CreateFeatureRequest request) {
        Run run = retriveRun(runId);
        User userAuthenticated = retriveAuthenticatedUser(authentication);
        checkAccessToRun(userAuthenticated,run);

        boolean isBadRequest = false;
        List<String> stackTrace = new ArrayList<>();

        if(request.name() == null) {
            stackTrace.add("Field name is required");
            isBadRequest = true;
        }

        if(request.description() == null) {
            isBadRequest = true;
            stackTrace.add("Field description is required");
        }

        if(isBadRequest) {
            throw new BadRequestException(stackTrace.toString());
        }

        Feature feature = new Feature(
                request.name(),
                request.description(),
                run,
                run.getProject()
        );

        featureDao.createFeature(feature);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureDto findFeatureById(Authentication authentication, Long featureId) {
        Feature feature = retriveFeature(featureId);
        User userAuthenticated = retriveAuthenticatedUser(authentication);
        checkAccessToFeature(userAuthenticated,feature);
        return featureDtoMapper.apply(feature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchFeatureResponse findFeatureWhereRunIsAndNameContaining(Authentication authentication, Long runId, String name, Integer page) {
        Run run = retriveRun(runId);
        User userAuthenticated = retriveAuthenticatedUser(authentication);
        checkAccessToRun(userAuthenticated,run);

        int currentPage = 0;

        String pattern = "";

        if(page != null) {
            currentPage = page;
        }

        if(name != null) {
            pattern = name;
        }

        Pageable pageable = PageRequest.of(currentPage,5);

        Page<Feature> pageResult = featureDao.findAllFeatureWhereRunIsAndNameContaining(run,pattern,pageable);

        if(currentPage != 0 && (pageResult.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        PageableDto pageableDto = new PageableDto(
                page,
                pageResult.getTotalPages()-1 > -1 ? pageResult.getTotalPages()-1 : 0,
                pageResult.getTotalPages(),
                pageResult.getNumberOfElements()
        );

        List<FeatureDto> features = pageResult
                .getContent()
                .stream()
                .map(featureDtoMapper)
                .toList();

        return new SearchFeatureResponse(
                features,
                pageableDto
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeature(Authentication authentication, Long featureId, UpdateFeatureRequest request) {
        Feature feature = retriveFeature(featureId);
        User userAuthenticated = retriveAuthenticatedUser(authentication);
        checkAccessToFeature(userAuthenticated,feature);
        boolean changes = false;

        if(request.name() != null && !request.name().equals(feature.getName())) {
            feature.setName(request.name());
            changes = true;
        }

        if(request.description() != null && !request.description().equals(feature.getDescription())) {
            feature.setDescription(request.description());
            changes = true;
        }

        if(request.status() != null) {
            switch (request.status()) {
                case "New", "In progress", "Completed" -> {
                    if (!request.status().equals(feature.getStatus())) {
                        feature.setStatus(request.status());
                        changes = true;
                    }
                }
                default -> throw new BadRequestException("Status not supported");
            }
        }

        if(!changes) {
            throw new BadRequestException("No changes found");
        }

        feature.setUpdatedAt(Timestamp.from(Instant.now()));
        featureDao.updateFeature(feature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFeatureById(Authentication authentication, Long featureId) {
       Feature feature = retriveFeature(featureId);
       User userAuthenticated = retriveAuthenticatedUser(authentication);
       checkAccessToFeature(userAuthenticated,feature);
       featureDao.deleteFeatureById(featureId);
    }

    private User retriveAuthenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private Run retriveRun(Long runId) {
        return runDao
                .findRunById(runId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Run with id %s does not exists"
                                        .formatted(runId)
                        )
                );
    }

    private void checkAccessToRun(User user, Run run) {
        boolean fullAccess = user.getRole().equals("PROJECT_MANAGER");
        if(!fullAccess) {
            run.getProject()
                    .getUsers()
                    .stream()
                    .filter(
                            user1 -> user1.getEmail().equals(user.getEmail())
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }
    }

    private Feature retriveFeature(Long featureId) {
        return featureDao
                .findFeatureById(
                        featureId
                )
                .orElseThrow(
                        () -> new NotFoundException(
                                "Feature with id %s does not exists"
                                        .formatted(
                                                featureId
                                        )
                        )
                );
    }

    private void checkAccessToFeature(User user, Feature feature) {
        if(!user.getRole().equals("PROJECT_MANAGER")) {
            feature
                    .getProject()
                    .getUsers()
                    .stream()
                    .filter(
                            user1 -> user1.getEmail().equals(user.getEmail())
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }
    }

}
