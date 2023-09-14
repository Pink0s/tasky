package com.tasky.api.mappers;

import com.tasky.api.dto.feature.FeatureDto;
import com.tasky.api.models.Feature;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * The FeatureDtoMapper class converts Feature objects to FeatureDto objects.
 */
@Service
public class FeatureDtoMapper implements Function<Feature, FeatureDto> {
    /**
     * @param feature the function argument
     * @return FeatureDto
     */
    @Override
    public FeatureDto apply(Feature feature) {
        return new FeatureDto(
                feature.getId(),
                feature.getName(),
                feature.getDescription(),
                feature.getStatus(),
                feature.getCreatedAt(),
                feature.getUpdatedAt()
        );
    }
}
