package com.tasky.api.services.run;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.project.ProjectDao;
import com.tasky.api.dao.run.RunDao;
import com.tasky.api.dto.run.CreateRunRequest;
import com.tasky.api.dto.run.SearchRunResponse;
import com.tasky.api.dto.run.UpdateRunRequest;
import com.tasky.api.mappers.RunDtoMapper;
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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RunServiceImplTest {

    @Mock private ProjectDao projectDao;
    @Mock private RunDao runDao;
    @Mock private RunDtoMapper runDtoMapper;
    @InjectMocks private RunServiceImpl underTest;

    @Test
    void createRun() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Authentication authentication = mock(Authentication.class);

        CreateRunRequest request = new CreateRunRequest(
                "name",
                "description",
                1692881836L,
                1692881838L
        );

        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        underTest.createRun(authentication,project.getId(),request);
        ArgumentCaptor<Run> runArgumentCaptor = ArgumentCaptor.forClass(Run.class);
        verify(runDao).createRun(runArgumentCaptor.capture());
        Run runCaptured = runArgumentCaptor.getValue();

        assertEquals(runCaptured.getName(),request.name());
        assertEquals(runCaptured.getDescription(),request.description());

    }

    @Test
    void createRunShouldThrowNotFoundIfProjectDoesNotExists() {
        Authentication authentication = mock(Authentication.class);
        Long projectId = 1L;
        CreateRunRequest request = new CreateRunRequest(
                "name",
                "description",
                1692881836L,
                1692881838L
        );

        assertThrows(NotFoundException.class,() -> underTest.createRun(authentication,projectId,request));
    }

    @Test
    void createRunShouldThrowUnauthorizedIfYouNotHaveAccessToProject() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Authentication authentication = mock(Authentication.class);

        CreateRunRequest request = new CreateRunRequest(
                "name",
                "description",
                1692881836L,
                1692881838L
        );

        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));

        assertThrows(UnauthorizedException.class,() -> underTest.createRun(authentication,project.getId(),request));
    }

    @Test
    void createRunShouldThrowBadRequestWhenRequestIsNotCorrect() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Authentication authentication = mock(Authentication.class);

        CreateRunRequest request = new CreateRunRequest(
                null,
                null,
                null,
                null
        );

        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));

        assertThrows(BadRequestException.class,() -> underTest.createRun(authentication,project.getId(),request));
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenRunDoesNotExist() {
        Long runId = 1L;
        Authentication authentication = mock(Authentication.class);
        assertThrows(NotFoundException.class,() -> underTest.findById(authentication,runId));
    }

    @Test
    void findByIdShouldThrowUnauthorizedWhenUserDoesNotHaveAccess() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        Run run = createFakeRun(project);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        assertThrows(UnauthorizedException.class,() -> underTest.findById(authentication,run.getId()));
    }

    @Test
    void findByIdShouldWork() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));

        Run run = createFakeRun(project);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        ArgumentCaptor<Run> runArgumentCaptor = ArgumentCaptor.forClass(Run.class);
        underTest.findById(authentication,run.getId());
        verify(runDtoMapper).apply(runArgumentCaptor.capture());

        Run runCaptured = runArgumentCaptor.getValue();

        assertEquals(runCaptured,run);

    }

    private Project createFakeProject(User creator) {
        Project project = new Project(
                "name",
                Timestamp.from(
                        Instant.now()
                                .plus(
                                        80,
                                        ChronoUnit.DAYS
                                )
                ),
                "descrt",
                creator
        );

        project.setId(1L);
        return project;
    }

    private User createFakeUser() {
        User user = new User("firstname","lastname","email@email.com","pass");
        user.setId(1L);
        return user;
    }

    private User createFakeProjectManager() {
        User user = new User("firstname","lastname","email2@email.com","pass");
        user.setId(2L);
        user.setRole("PROJECT_MANAGER");
        return user;
    }

    private Run createFakeRun(Project project) {
        Run run = new Run(
                "name",
                "description",
                Timestamp.from(
                        Instant.now()
                                .plus(
                                        5,
                                        ChronoUnit.DAYS
                                )
                ),
                Timestamp.from(
                        Instant.now()
                                .plus(
                                        10,
                                        ChronoUnit.DAYS
                                )
                )
                ,project
        );

        run.setId(1L);

        return run;
    }


    @Test
    void findRunByProjectIdAndByNameContainingShouldThrowBadRequest() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        List<Run> runs = List.of(run);
        String pattern = "";
        Page<Run> runPage = new PageImpl<>(runs);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        when(runDao.findAllRunWhereProjectIsAndNameContaining(any(),any(),any())).thenReturn(runPage);

        assertThrows(BadRequestException.class,() -> underTest.findRunByProjectIdAndByNameContaining(authentication,project.getId(),pattern,32));

    }

    @Test
    void findRunByProjectIdAndByNameContaining() {
        User projectManager = createFakeProjectManager();
        User user = createFakeUser();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        List<Run> runs = List.of(run);
        String pattern = "";
        Page<Run> runPage = new PageImpl<>(runs);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(projectDao.selectProjectById(project.getId())).thenReturn(Optional.of(project));
        when(runDao.findAllRunWhereProjectIsAndNameContaining(any(),any(),any())).thenReturn(runPage);

        SearchRunResponse response = underTest.findRunByProjectIdAndByNameContaining(authentication,project.getId(),pattern,null);

        assertNotNull(response);
    }

    @Test
    void deleteRunById() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));
        underTest.deleteRunById(authentication, run.getId());
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(runDao).deleteRunById(idCaptor.capture());

        Long idCaptured = idCaptor.getValue();

        assertEquals(run.getId(),idCaptured);

    }

    @Test
    void updateRunByIdShouldThrowBadRequestWhenNoChanges() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        UpdateRunRequest request = new UpdateRunRequest(run.getName(),run.getDescription(),null,null,run.getStatus());
        assertThrows(BadRequestException.class, () -> underTest.updateRunById(authentication,request, run.getId()));
    }

    @Test
    void updateRunByIdShouldThrowBadRequestWhenStatusIsWrong() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        UpdateRunRequest request = new UpdateRunRequest(run.getName(),run.getDescription(),null,null,"run.getStatus()");
        assertThrows(BadRequestException.class, () -> underTest.updateRunById(authentication,request, run.getId()));
    }

    @Test
    void updateRunById() {
        User user = createFakeUser();
        User projectManager = createFakeProjectManager();
        Project project = createFakeProject(projectManager);
        project.setUsers(List.of(user));
        Run run = createFakeRun(project);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(runDao.findRunById(run.getId())).thenReturn(Optional.of(run));

        UpdateRunRequest request = new UpdateRunRequest("run.getName()","run.getDescription()",1692881839L,1692881839L,"In progress");

        underTest.updateRunById(authentication,request,run.getId());

        ArgumentCaptor<Run> runArgCaptor = ArgumentCaptor.forClass(Run.class);

        verify(runDao).updateRun(runArgCaptor.capture());
        Run runCaptured = runArgCaptor.getValue();

        assertEquals(runCaptured.getStatus(),request.status());
        assertEquals(runCaptured.getDescription(),request.description());
        assertEquals(runCaptured.getName(),request.name());

        var date = Instant.ofEpochSecond(1692881839L).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Timestamp timestampToCompare = Timestamp.valueOf(date);

        assertEquals(runCaptured.getStartDate(),timestampToCompare);
        assertEquals(runCaptured.getEndDate(),timestampToCompare);

    }
}