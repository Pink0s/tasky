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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectDao projectDao;
    private final UserDao userDao;

    private final ProjectDtoMapper projectDtoMapper;

    public ProjectServiceImpl(@Qualifier("PROJECT_JPA") ProjectDao projectDao, ProjectDtoMapper projectDtoMapper, UserDao userDao) {
        this.projectDao = projectDao;
        this.projectDtoMapper = projectDtoMapper;
        this.userDao = userDao;
    }

    /**
     * @param authentication
     * @param request
     */
    @Override
    public void createProject(Authentication authentication, CreateProjectRequest request) {
        User user = (User) authentication.getPrincipal();
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

        if(withDescription) {
            project = new Project(
                    request.name(),
                    request.dueDate(),
                    request.description(),
                    user
            );
        } else {
            project = new Project(request.name(), request.dueDate(),user);
        }

        projectDao.insertProject(project);
    }

    /**
     * @param request
     */
    @Override
    public void updateProject(Long projectId, UpdateProjectRequest request) {

        if(request == null || (request.description() == null && request.name() == null) ) {
            throw new BadRequestException("Missing required fields");
        }

        if(projectId == null) {
            throw new BadRequestException("Missing project id");
        }

        Project project = projectDao
                .selectProjectById(
                        projectId
                ).orElseThrow(
                        () -> new NotFoundException("Project with id "+projectId+" does not exists")
                );

        if(request.name() != null) {
            project.setName(request.name());
        }

        if(request.description() != null) {
            project.setDescription(request.description());
        }

        projectDao.updateProject(project);
    }

    /**
     * @param request
     */
    @Override
    public void addUser(Long projectId, AddUserToProjectRequest request) {

        if(request == null || request.userId() == null) {
            throw new BadRequestException("Missing required userId");
        }

        if(projectId == null) {
            throw new BadRequestException("Missing project id");
        }

        Project project = projectDao
                .selectProjectById(
                        projectId
                ).orElseThrow(
                        () -> new NotFoundException("Project with id "+projectId+" does not exists")
                );

        User user = userDao
                .selectUserById(
                        request.userId()
                ).orElseThrow(
                        () -> new NotFoundException("User with id "+request.userId()+" does not exists")
                );

        Set<User> users = project.getUsers();
        users.add(user);
        project.setUsers(users);

        projectDao.updateProject(project);

    }

    /**
     * @param projectId
     * @return
     */
    @Override
    public ProjectDto findProjectById(Authentication authentication, Long projectId) {

        boolean isAuthorized = false;

        //Retrieve authenticated user information
        User user = (User) authentication.getPrincipal();

        //check if project !exists throw 404
        Project project = projectDao
                .selectProjectById(projectId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Project with id "+projectId+"does not exists."
                        )
                );

        //check if users have PROJECT_MANAGER role or is in project else throw UNAUTHORIZED

        if(user.getRole().equals("PROJECT_MANAGER")) {
            isAuthorized = true;
        } else {
            project
                    .getUsers()
                    .stream()
                    .filter(user1 ->
                            user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(() -> new UnauthorizedException("You can't access to this resource"));
        }

        if(!isAuthorized) {
            throw new UnauthorizedException("You can't access to this resource");
        }

        //return PROJECT DTO
        projectDtoMapper.apply(project);

        return projectDtoMapper.apply(project);
    }

    /**
     * @param pattern
     * @param page
     * @return
     */
    @Override
    public SearchProjectResponse findProject(Authentication authentication, String pattern, Integer page) {
        int currentPage = 0;
        int perPage = 5;
        String searchPattern = "";
        boolean isFullAccess = false;

        User user = (User) authentication.getPrincipal();

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
                    requestResult.getTotalPages()-1,
                    requestResult.getTotalPages(),requestResult.getNumberOfElements()
            );

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

        return new SearchProjectResponse(projects,pageableDto);
    }

    /**
     * @param projectId
     */
    @Override
    public void deleteProjectById(Long projectId) {

        if(!projectDao.isProjectExistsWithId(projectId)) {
            throw new NotFoundException("Project with id "+projectId+" does not exists");
        }

        projectDao.deleteProjectById(projectId);
    }
}
