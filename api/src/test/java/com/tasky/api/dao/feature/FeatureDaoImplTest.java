package com.tasky.api.dao.feature;

import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import com.tasky.api.repositories.FeatureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeatureDaoImplTest {

    @Mock FeatureRepository featureRepository;
    @InjectMocks FeatureDaoImpl underTest;

    @Test
    void createFeature() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Feature feature = new Feature("feature","desc",project);

        underTest.createFeature(feature);

        verify(featureRepository).save(feature);

    }

    @Test
    void findFeatureById() {
        Long featureId = 1L;

        underTest.findFeatureById(featureId);

        verify(featureRepository).findById(featureId);
    }

    @Test
    void findAllFeatureWhereProjectIsAndNameContaining() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Pageable pageable = PageRequest.of(0,10);
        String name = "test";

        underTest.findAllFeatureWhereProjectIsAndNameContaining(project,name,pageable);

        verify(featureRepository).findAllByProjectIsAndNameContaining(project,name,pageable);
    }

    @Test
    void findAllFeatureWhereRunIsAndNameContaining() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Run run = new Run(
                "runName",
                "Description",
                Timestamp.from(Instant.now().plus(4,ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(8,ChronoUnit.DAYS)),
                project
        );


        Pageable pageable = PageRequest.of(0,10);
        String name = "test";

        underTest.findAllFeatureWhereRunIsAndNameContaining(run,name,pageable);

        verify(featureRepository).findAllByRunIsAndNameContaining(run,name,pageable);
    }

    @Test
    void updateFeature() {
        User user = new User("firstname","lastname","email","password");
        user.setId(1L);

        Project project = new Project(
                "name",
                Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                user
        );

        project.setId(1L);

        Feature feature = new Feature("feature","desc",project);

        underTest.updateFeature(feature);

        verify(featureRepository).save(feature);
    }

    @Test
    void deleteFeatureById() {
        Long featureId = 1L;

        underTest.deleteFeatureById(featureId);

        verify(featureRepository).deleteById(featureId);
    }
}