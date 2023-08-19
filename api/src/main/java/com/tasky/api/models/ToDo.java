package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

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
    @OneToMany(orphanRemoval = true,mappedBy = "toDo")
    private Set<Comment> comments;

}
