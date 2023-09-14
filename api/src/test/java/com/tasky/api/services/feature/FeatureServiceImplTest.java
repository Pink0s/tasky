package com.tasky.api.services.feature;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.feature.FeatureDao;
import com.tasky.api.dao.run.RunDao;
import com.tasky.api.dto.feature.CreateFeatureRequest;
import com.tasky.api.dto.feature.SearchFeatureResponse;
import com.tasky.api.dto.feature.UpdateFeatureRequest;
import com.tasky.api.mappers.FeatureDtoMapper;
import com.tasky.api.models.Feature;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureServiceImplTest {
    @Mock private  RunDao runDao;
    @Mock private FeatureDao featureDao;
    @Mock private FeatureDtoMapper featureDtoMapper;
    @InjectMocks private FeatureServiceImpl underTest;

    @Test
    void createFeatureShouldTrowNotFoundException() {
        Authentication authentication = mock(Authentication.class);
        CreateFeatureRequest request = new CreateFeatureRequest("name","description");
        assertThrows(NotFoundException.class, () -> underTest.createFeature(authentication,1L,request));
    }

    @Test
    void createFeatureShouldTrowUnauthorizedException() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Run run = createFakeRun(project);

        CreateFeatureRequest request = new CreateFeatureRequest("name","description");
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        assertThrows(UnauthorizedException.class,()->underTest.createFeature(authentication,run.getId(),request));
    }

    @Test
    void createFeatureShouldTrowBadRequestException() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);

        CreateFeatureRequest request = new CreateFeatureRequest(null,null);
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        assertThrows(BadRequestException.class,()->underTest.createFeature(authentication,run.getId(),request));
    }

    @Test
    void createFeatureShouldWork() {
        Authentication authentication = mock(Authentication.class);
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);

        CreateFeatureRequest request = new CreateFeatureRequest("null","null");
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        ArgumentCaptor<Feature> featureArgumentCaptor = ArgumentCaptor.forClass(Feature.class);
        underTest.createFeature(authentication,run.getId(),request);

        verify(featureDao).createFeature(featureArgumentCaptor.capture());
        Feature featureCaptured = featureArgumentCaptor.getValue();

        assertEquals(featureCaptured.getName(),request.name());
        assertEquals(featureCaptured.getDescription(),request.description());

    }

    @Test
    void findFeatureById() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        ArgumentCaptor<Feature> featureArgumentCaptor = ArgumentCaptor.forClass(Feature.class);

        underTest.findFeatureById(authentication,feature.getId());
        verify(featureDtoMapper).apply(featureArgumentCaptor.capture());
        Feature featureCaptured = featureArgumentCaptor.getValue();


        assertEquals(featureCaptured, feature);
    }

    @Test
    void findFeatureByIdShouldThrowNotFoundIfFeatureDoesNotExists() {
       Authentication authentication = mock(Authentication.class);
       assertThrows(NotFoundException.class, () -> underTest.findFeatureById(authentication,1L));
    }

    @Test
    void findFeatureByIdShouldThrowUnauthorizedException() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);

        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        assertThrows(UnauthorizedException.class, () -> underTest.findFeatureById(authentication,feature.getId()));
    }

    @Test
    void findFeatureWhereRunIsAndNameContaining() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        List<Feature> features = List.of(feature);
        Page<Feature> featurePage = new PageImpl<>(features);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));
        when(featureDao.findAllFeatureWhereRunIsAndNameContaining(any(),any(),any())).thenReturn(featurePage);

        SearchFeatureResponse result = underTest
                .findFeatureWhereRunIsAndNameContaining(
                        authentication,
                        run.getId(),
                        "aer",
                        0
                );

        assertNotNull(result);
    }

    @Test
    void findFeatureWhereRunIsAndNameContainingShouldThrowBadRequest() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        List<Feature> features = List.of(feature);
        Page<Feature> featurePage = new PageImpl<>(features);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));
        when(featureDao.findAllFeatureWhereRunIsAndNameContaining(any(),any(),any())).thenReturn(featurePage);

        assertThrows(
                BadRequestException.class,
                () -> underTest
                        .findFeatureWhereRunIsAndNameContaining(
                                authentication,
                                run.getId(),
                                run.getName(),
                                23
                        )
        );

    }

    @Test
    void updateFeatureShouldThrowBadRequest() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        UpdateFeatureRequest request = new UpdateFeatureRequest("terest","tesearearz","tset");

        assertThrows(BadRequestException.class, () -> underTest.updateFeature(authentication,feature.getId(),request));
    }

    @Test
    void updateFeature() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        UpdateFeatureRequest request = new UpdateFeatureRequest("terest","tesearearz","Completed");

        ArgumentCaptor<Feature> featureArgumentCaptor = ArgumentCaptor.forClass(Feature.class);
        underTest.updateFeature(authentication,feature.getId(),request);
        verify(featureDao).updateFeature(featureArgumentCaptor.capture());

        Feature featureCaptured = featureArgumentCaptor.getValue();

        assertEquals(featureCaptured.getStatus(),request.status());
        assertEquals(featureCaptured.getName(),request.name());
        assertEquals(featureCaptured.getDescription(),request.description());
    }

    @Test
    void updateFeatureShouldThrowBadRequestWhenNoChanges() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        UpdateFeatureRequest request = new UpdateFeatureRequest(null,null,null);

        assertThrows(BadRequestException.class,()->underTest.updateFeature(authentication,feature.getId(),request));

    }

    @Test
    void deleteFeatureById() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Feature feature = createFakeFeature(run);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(featureDao.findFeatureById(feature.getId())).thenReturn(Optional.of(feature));

        underTest.deleteFeatureById(authentication,feature.getId());

        verify(featureDao).deleteFeatureById(feature.getId());
    }

    private User createFakeProjectManager() {
        User user = new User("tete","test","test@test.com","password");
        user.setRole("PROJECT_MANAGER");
        user.setId(1L);
        return user;
    }

    private User createFakeUser() {
        User user = new User("tete","test","testr@test.com","password");
        user.setId(2L);
        return user;
    }

    private Run createFakeRun(Project project) {
        Run run = new Run(
                "name",
                "desc",
                Timestamp.from(
                        Instant.now()
                ),
                Timestamp.from(
                        Instant.now()
                ),
                project
        );

        run.setId(1L);

        return run;
    }

    private Project createFakeProject(User user) {
        Project project = new Project(
                "name",
                Timestamp.from(Instant.now()),
                "desc",
                user
        );

        project.setId(1L);
        return project;
    }

    private Feature createFakeFeature(Run run) {
        Feature feature = new Feature(
                "name",
                "description",
                run,
                run.getProject()
        );

        feature.setId(1L);
        return feature;
    }

}