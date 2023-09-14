package com.tasky.api.mappers;

import com.tasky.api.dto.feature.FeatureDto;
import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureDtoMapperTest {

    private final FeatureDtoMapper underTest = new FeatureDtoMapper();
    @Test
    void apply() {
        User user = new User("Fistname","LastName","email@test.com","password");
        user.setRole("PROJECT_MANAGER");
        Project project = new Project("name", Timestamp.from(Instant.now()),"description",user);
        Feature feature = new Feature("name","descr",project);
        feature.setId(1L);
        FeatureDto featureDto = underTest.apply(feature);
        assertEquals(featureDto.id(),feature.getId());
        assertEquals(featureDto.description(),feature.getDescription());
        assertEquals(featureDto.name(),feature.getName());
        assertEquals(featureDto.status(),feature.getStatus());
        assertEquals(featureDto.createdAt(),feature.getCreatedAt());
        assertEquals(featureDto.updatedAt(), feature.getUpdatedAt());

    }
}