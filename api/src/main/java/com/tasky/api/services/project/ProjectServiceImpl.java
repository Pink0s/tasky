package com.tasky.api.services.project;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.project.ProjectDao;
import com.tasky.api.dao.user.UserDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.project.*;
import com.tasky.api.mappers.ProjectDtoMapper;
import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectDao projectDao;
    private final UserDao userDao;

    private final ProjectDtoMapper projectDtoMapper;

    public ProjectServiceImpl(@Qualifier("PROJECT_JPA") ProjectDao projectDao, ProjectDtoMapper projectDtoMapper, @Qualifier("JPA") UserDao userDao) {
        this.projectDao = projectDao;
        this.projectDtoMapper = projectDtoMapper;
        this.userDao = userDao;
    }

    /**
     * Creates a new project.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param request        The request containing the project details.
     * @throws BadRequestException If the request is missing required fields.
     */
    @Override
    public void createProject(Authentication authentication, CreateProjectRequest request) {
        User user = retriveAuthenticatedUser(authentication);
        List<String> errorStackTrace = new ArrayList<>();

        boolean isBadRequest = false;
        boolean withDescription = true;

        if(request == null) {
            String message = "Error Missing body";
            logger.error(message);
            errorStackTrace.add(message);
            throw new BadRequestException(errorStackTrace.toString());
        }

        if(request.description() == null) {
            withDescription = false;
        }

        if(request.dueDate() == null) {
            isBadRequest = true;
            String message = "Missing field dueDate in request body";
            logger.error(message);
            errorStackTrace.add(message);
        }

        if(request.name() == null) {
            String message = "Missing field description in request body";
            isBadRequest = true;
            logger.error(message);
            errorStackTrace.add(message);
        }

        if(isBadRequest) {
            throw new BadRequestException(errorStackTrace.toString());
        }

        Project project;
        var date = Instant.ofEpochSecond(request.dueDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if(withDescription) {

            project = new Project(
                    request.name(),
                    Timestamp.valueOf(date),
                    request.description(),
                    user
            );
        } else {

            project = new Project(request.name(), Timestamp.valueOf(date), user);
        }

        projectDao.insertProject(project);
    }

    /**
     * Updates an existing project based on the provided update request.
     *
     * @param projectId The ID of the project to update.
     * @param request   The update request containing the modified project details.
     * @throws BadRequestException If no changes are found or if the status is not correct.
     * @throws NotFoundException   If the project with the given ID is not found.
     */
    @Override
    public void updateProject(Long projectId, UpdateProjectRequest request) {



        Project project = retrieveProject(projectId);

        boolean changes = false;

        if(request.name() != null && !request.name().equals(project.getName())) {
            project.setName(request.name());
            changes = true;
        }

        if(request.description() != null && !request.description().equals(project.getDescription())) {
            project.setDescription(request.description());
            changes = true;
        }

        if(request.status() != null) {
            switch (request.status()) {
                case "New", "Completed", "In progress" -> {
                    if (!request.status().equals(project.getStatus())) {
                        changes = true;
                        project.setStatus(request.status());
                    }
                }
                default -> throw new BadRequestException("Status is not correct");
            }
        }

        if(request.DueDate() != null) {
            var date = Instant.ofEpochSecond(request.DueDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            Timestamp timestamp = Timestamp.valueOf(date);
            if(!Objects.equals(project.getDueDate(), timestamp)) {
                changes = true;
                project.setDueDate(timestamp);
            }
        }

        if(!changes){
            throw new BadRequestException("No changes found");
        }

        project.setUpdatedAt(Timestamp.from(Instant.now()));
        projectDao.updateProject(project);

    }

    /**
     * Adds a user to a project.
     *
     * @param projectId The ID of the project to which the user is to be added.
     * @param request   The request containing the user ID to add to the project.
     * @throws BadRequestException If the request is missing required fields.
     * @throws NotFoundException   If the project with the given ID or the user with the given ID is not found.
     */
    @Override
    public void addUser(Long projectId, AddUserToProjectRequest request) {
        if(request == null || request.userId() == null) {
            throw new BadRequestException("Missing required userId");
        }

        Project project = retrieveProject(projectId);

        User user = retrieveUser(request.userId());
        List<Project> projects = user.getProjects();
        projects.add(project);
        user.setProjects(projects);
        userDao.updateUser(user);
    }

    /**
     * Retrieves a project by its ID and ensures that the authenticated user has the right to access it.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param projectId      The ID of the project to retrieve.
     * @return The ProjectDto object representing the retrieved project.
     * @throws NotFoundException   If the project with the given ID is not found.
     * @throws UnauthorizedException If the authenticated user is not authorized to access the project.
     */
    @Override
    public ProjectDto findProjectById(Authentication authentication, Long projectId) {
        User user = retriveAuthenticatedUser(authentication);
        Project project = retrieveProject(projectId);
        checkRightToAccessToProject(user,project);
        return projectDtoMapper.apply(project);
    }

    /**
     * Searches for projects based on the provided search pattern and pagination parameters.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param pattern        The search pattern for project names.
     * @param page           The page number for pagination.
     * @return A SearchProjectResponse containing a list of matching projects and pagination information.
     * @throws BadRequestException If the requested page does not exist.
     */
    @Override
    public SearchProjectResponse findProject(Authentication authentication, String pattern, Integer page) {
        int currentPage = 0;
        int perPage = 5;
        String searchPattern = "";
        boolean isFullAccess = false;

        User user = retriveAuthenticatedUser(authentication);

        if(user.getRole().equals("PROJECT_MANAGER")) {
            isFullAccess = true;
        }
        if(page != null) {
            currentPage = page;
        }

        if(pattern != null) {
           searchPattern  = pattern;
        }

        Pageable pageable = PageRequest.of(currentPage,perPage);

        if(isFullAccess) {
            Page<Project> requestResult = projectDao.selectAllProject(searchPattern,pageable);

            PageableDto pageableDto = new PageableDto(
                    currentPage,
                    requestResult.getTotalPages()-1 > -1 ? requestResult.getTotalPages()-1 : 0,
                    requestResult.getTotalPages(),
                    requestResult.getNumberOfElements()
            );

            if(currentPage != 0 && (requestResult.getTotalPages()-1) < page) {
                throw new BadRequestException("Page requested does not exists");
            }

            List<ProjectDto> projects = requestResult
                    .getContent()
                    .stream()
                    .map(projectDtoMapper)
                    .toList();

            return new SearchProjectResponse(projects,pageableDto);
        }

        Page<Project> requestResult = projectDao
                .selectAllProjectForUser(
                        searchPattern,
                        user,
                        pageable
                );

        List<ProjectDto> projects = requestResult
                .getContent()
                .stream()
                .map(projectDtoMapper)
                .toList();

        PageableDto pageableDto = new PageableDto(
                currentPage,
                requestResult.getTotalPages()-1 > -1 ? requestResult.getTotalPages()-1 : 0,
                requestResult.getTotalPages(),
                requestResult.getNumberOfElements()
        );

        if(currentPage != 0 && (requestResult.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        return new SearchProjectResponse(projects,pageableDto);
    }

    /**
     * Deletes a project based on its ID.
     *
     * @param projectId The ID of the project to delete.
     * @throws NotFoundException If the project with the given ID is not found.
     */
    @Override
    public void deleteProjectById(Long projectId) {

        if(!projectDao.isProjectExistsWithId(projectId)) {

            throw new NotFoundException("Project with id "+projectId+" does not exists");
        }

        projectDao.deleteProjectById(projectId);
    }

    private User retriveAuthenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private Project retrieveProject(Long projectId) {
        return projectDao
                .selectProjectById(
                        projectId
                ).orElseThrow(
                        () -> new NotFoundException("Project with id "+projectId+" does not exists")
                );
    }

    private User retrieveUser(Long userId) {
        return userDao
                .selectUserById(
                        userId
                ).orElseThrow(
                        () -> new NotFoundException("User with id "+userId+" does not exists")
                );
    }

    private void checkRightToAccessToProject(User user, Project project) {
        boolean isAuthorized = user.getRole().equals("PROJECT_MANAGER");
        if(!isAuthorized) {
            project.getUsers()
                    .stream()
                    .filter(
                            actualUser -> actualUser
                                    .getId()
                                    .equals(
                                            user.getId()
                                    )
                    ).findFirst()
                    .orElseThrow(() -> new UnauthorizedException("You can't access to this resource"));
        }

    }
}
