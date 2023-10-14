package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

/**
 * Represents a ToDo.
 */
@Entity @Data @NoArgsConstructor
public class ToDo {
    @SequenceGenerator(
            name= "to_do_id_seq",
            sequenceName = "to_do_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "to_do_id_seq"
    )
    @Id
    private Long id;
    @Column(nullable = false) String name;
    @Column(nullable = false) String type;
    @Column(nullable = false) String description;
    @Column(nullable = false) String status;
    @ManyToOne @JoinColumn(name ="feature_id",nullable = false) private Feature feature;
    @ManyToOne @JoinColumn(name ="user_id") private User user;
    @Column(nullable = false) Timestamp createdAt;
    @Column(nullable = false) Timestamp updatedAt;
    @OneToMany(mappedBy = "toDo", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Comment> comments;

    public ToDo(String name, String type, String description, Feature feature, User user) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.type = type;
        this.description = description;
        this.feature = feature;
        this.user = user;
        this.status = "New";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }
}
