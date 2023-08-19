package com.tasky.api.repositories;

import com.tasky.api.models.Project;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Page<Project> findProjectByNameContaining(String name, Pageable pageable);
    Page<Project> findProjectByNameContainingAndUsersContains(String name, User user, Pageable pageable);
    boolean existsProjectById(Long id);
}
