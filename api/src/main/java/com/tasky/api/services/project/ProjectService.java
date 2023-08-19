package com.tasky.api.services.project;

import com.tasky.api.dto.project.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProjectService {
    void createProject(Authentication authentication, CreateProjectRequest request);
    void updateProject(Long projectId, UpdateProjectRequest request);
    void addUser(Long projectId, AddUserToProjectRequest request);
    ProjectDto findProjectById(Authentication authentication, Long projectId);
    SearchProjectResponse findProject(Authentication authentication,  @Nullable String pattern, @Nullable Integer page);
    void deleteProjectById(Long projectId);
}
