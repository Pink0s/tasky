package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@Entity @Data @NoArgsConstructor
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
    @ManyToOne @JoinColumn(name ="created_by",nullable = false) private User user;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;

    @ManyToMany(mappedBy = "projects")
    private Set<User> users;

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
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Project(String name, Timestamp dueDate, User user) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.dueDate = dueDate;
        this.user = user;
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }
}
