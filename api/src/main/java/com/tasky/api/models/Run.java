package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

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
    @Column(nullable = false) private String description;
    @Column(nullable = false) private Timestamp startDate;
    @Column(nullable = false) private Timestamp endDate;
    private Integer totalTask;
    private Integer completedTask;

    @ManyToOne @JoinColumn(name ="project_id",nullable = false) private Project project;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;
    @OneToMany(orphanRemoval = true,mappedBy = "run")
    private Set<Feature> features;

}
