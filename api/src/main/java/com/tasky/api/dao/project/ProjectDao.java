package com.tasky.api.dao.project;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProjectDao {
    void insertProject(Project project);
    Optional<Project> selectProjectById(Long projectId);
    Page<Project> selectAllProject(String name, Pageable pageable);
    Page<Project> selectAllProjectForUser(String name, User user, Pageable pageable);
    Boolean isProjectExistsWithId(Long id);
    void deleteProjectById(Long id);
    void updateProject(Project project);
}
