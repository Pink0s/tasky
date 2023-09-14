package com.tasky.api.services.run;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.project.ProjectDao;
import com.tasky.api.dao.run.RunDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.run.CreateRunRequest;
import com.tasky.api.dto.run.RunDto;
import com.tasky.api.dto.run.SearchRunResponse;
import com.tasky.api.dto.run.UpdateRunRequest;
import com.tasky.api.mappers.RunDtoMapper;
import com.tasky.api.models.Project;
import com.tasky.api.models.Run;
import com.tasky.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing run-related operations.
 */
@Service
public class RunServiceImpl implements RunService {
    private final RunDao runDao;
    private final ProjectDao projectDao;
    private final RunDtoMapper runDtoMapper;

    public RunServiceImpl(RunDao runDao, ProjectDao projectDao, RunDtoMapper runDtoMapper) {
        this.runDao = runDao;
        this.projectDao = projectDao;
        this.runDtoMapper = runDtoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRun(Authentication authentication, Long projectId, CreateRunRequest request) {

        Project project = retrieveProject(projectId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToProject(user,project);

        List<String> stackTrace = new ArrayList<>();
        boolean isBadRequest = false;

        if(request.name() == null) {
            String message = "Missing name field";
            stackTrace.add(message);
            isBadRequest = true;
        }

        if(request.description() == null) {
            String message = "Missing description field";
            stackTrace.add(message);
            isBadRequest = true;
        }

        if(request.startDate() == null) {
            String message = "Missing startDate field";
            stackTrace.add(message);
            isBadRequest = true;
        }

        if(request.endDate() == null) {
            String message = "Missing endDate field";
            stackTrace.add(message);
            isBadRequest = true;
        }

        if(isBadRequest) {
            throw new BadRequestException(stackTrace.toString());
        }

        var startDate = Instant.ofEpochSecond(request.startDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        var endDate = Instant.ofEpochSecond(request.endDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        Timestamp startDateTimestamp = Timestamp.valueOf(startDate);
        Timestamp endDateTimestamp = Timestamp.valueOf(endDate);

        Run run = new Run(
                request.name(),
                request.description(),
                startDateTimestamp,
                endDateTimestamp,
                project
        );

        runDao.createRun(run);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RunDto findById(Authentication authentication, Long runId) {
        Run run = retrieveRun(runId);
        User userAuthenticated = retrieveUserAuthenticated(authentication);
        checkAccessToRun(userAuthenticated,run);
        return runDtoMapper.apply(run);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchRunResponse findRunByProjectIdAndByNameContaining(Authentication authentication, Long projectId, String name, Integer page) {
        Project project = retrieveProject(projectId);
        User userAuthenticated = retrieveUserAuthenticated(authentication);
        checkAccessToProject(userAuthenticated,project);
        int currentPage = 0;
        String pattern = "";

        if(page != null) {
            currentPage = page;
        }

        if(name != null) {
            pattern = name;
        }

        Pageable pageable = PageRequest.of(currentPage,5);
        Page<Run> runPage = runDao.findAllRunWhereProjectIsAndNameContaining(project,pattern,pageable);

        if(currentPage != 0 && (runPage.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        PageableDto pageableDto = new PageableDto(
                currentPage,
                runPage.getTotalPages()-1 > -1 ? runPage.getTotalPages()-1 : 0,
                runPage.getTotalPages(),
                runPage.getNumberOfElements()
        );

        List<RunDto> runDtos = runPage
                .getContent()
                .stream()
                .map(runDtoMapper)
                .toList();

        return new SearchRunResponse(
            runDtos,pageableDto
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRunById(Authentication authentication, UpdateRunRequest request, Long runId) {
        Run run = retrieveRun(runId);
        User authenticatedUser = retrieveUserAuthenticated(authentication);
        checkAccessToRun(authenticatedUser,run);
        boolean changes = false;

        if( request.name() != null && !request.name().equals(run.getName())) {
            changes = true;
            run.setName(request.name());
        }

        if( request.description() != null && !request.description().equals(run.getDescription())) {
            changes = true;
            run.setDescription(request.description());
        }

        if(request.status() != null) {
            switch (request.status()) {
                case "New", "In progress", "Completed" -> {
                    if (!request.status().equals(run.getStatus())) {

                        changes = true;
                        run.setStatus(request.status());
                    }
                }
                default -> throw new BadRequestException("Status not correct");
            }
        }

        if(request.startDate() != null) {
            var startDate = Instant.ofEpochSecond(request.startDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            Timestamp startDateTimestamp = Timestamp.valueOf(startDate);
            if(!startDateTimestamp.equals(run.getStartDate())) {
                changes = true;
                run.setStartDate(startDateTimestamp);
            }
        }

        if(request.endDate() != null) {
            var endDate = Instant.ofEpochSecond(request.endDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            Timestamp endDateTimestamp = Timestamp.valueOf(endDate);
            if(!endDateTimestamp.equals(run.getEndDate())) {
                changes = true;
                run.setEndDate(endDateTimestamp);
            }
        }

        if(!changes) {
            throw new BadRequestException("No changes found");
        }

        run.setUpdatedAt(Timestamp.from(Instant.now()));

        runDao.updateRun(run);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRunById(Authentication authentication, Long runId) {
        Run run = retrieveRun(runId);
        User userAuthenticated = retrieveUserAuthenticated(authentication);
        checkAccessToRun(userAuthenticated,run);
        runDao.deleteRunById(runId);
    }

    private Project retrieveProject(Long projectId) {
        return projectDao
                .selectProjectById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with id %s does not exists")
                );
    }

    private User retrieveUserAuthenticated(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private void checkAccessToProject(User user, Project project) {
        boolean fullAccess = user.getRole().equals("PROJECT_MANAGER");

        if(!fullAccess) {
            project.getUsers()
                    .stream()
                    .filter(
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(() -> new UnauthorizedException("You can't access to this resource"));
        }
    }

    private Run retrieveRun(Long runId) {
        return runDao.findRunById(runId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Run with id %s does not exists".formatted(runId)
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
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }

    }
}
