package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

/**
 * Represents a Run.
 */
@Entity @Data @NoArgsConstructor
public class Run {
    @SequenceGenerator(
            name= "run_id_seq",
            sequenceName = "run_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "run_id_seq"
    )
    @Id private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = true) private String description;
    @Column(nullable = false) private Timestamp startDate;
    @Column(nullable = false) private Timestamp endDate;
    @Column(nullable = false) private String status;
    @ManyToOne @JoinColumn(name ="project_id",nullable = false) private Project project;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;
    @OneToMany(orphanRemoval = true,mappedBy = "run")
    private Set<Feature> features;

    public Run(String name, String description, Timestamp startDate, Timestamp endDate, Project project) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
        this.status = "New";
    }
}
