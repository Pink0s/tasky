package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Project.
 */
@Entity @Data @NoArgsConstructor @ToString
public class Project {
    @SequenceGenerator(
            name= "project_id_seq",
            sequenceName = "project_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_id_seq"
    )
    @Id
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private Timestamp dueDate;
    @Column(nullable = true) private String description;
    @Column(nullable = false) private String status;
    @ManyToOne @JoinColumn(name ="created_by",nullable = false) private User user;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;

    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "projects")
    private List<User> users =  new ArrayList<>();

    @OneToMany(orphanRemoval = true,mappedBy = "project")
    private Set<Run> runs;

    @OneToMany(orphanRemoval = true,mappedBy = "project")
    private Set<Feature> features;

    public Project(String name, Timestamp dueDate, String description, User user) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.dueDate = dueDate;
        this.description = description;
        this.user = user;
        this.status = "New";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Project(String name, Timestamp dueDate, User user) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.dueDate = dueDate;
        this.user = user;
        this.status = "New";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }
}
