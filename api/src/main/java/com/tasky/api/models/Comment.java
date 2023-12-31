package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Represents a Comments.
 */
@Entity @Data @NoArgsConstructor
public class Comment {
    @SequenceGenerator(
            name= "comment_id_seq",
            sequenceName = "comment_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_id_seq"
    )
    @Id private Long id;
    @Column(nullable = false) String name;
    @Column(nullable = false) String content;
    @ManyToOne @JoinColumn(name ="to_do_id",nullable = false) private ToDo toDo;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;

    public Comment(String name, String content, ToDo toDo) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.name = name;
        this.content = content;
        this.toDo = toDo;
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

}
