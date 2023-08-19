package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Entity @Data @NoArgsConstructor
public class Feature {
    @SequenceGenerator(
            name= "feature_id_seq",
            sequenceName = "feature_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "feature_id_seq"
    )
    @Id private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String description;
    @Column(nullable = false) private String type;

    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;
    @ManyToOne @JoinColumn(name ="run_id") private Run run;
    @ManyToOne @JoinColumn(name ="project_id",nullable = false) private Project project;
    @OneToMany(orphanRemoval = true,mappedBy = "feature")
    private Set<ToDo> toDos;
}
