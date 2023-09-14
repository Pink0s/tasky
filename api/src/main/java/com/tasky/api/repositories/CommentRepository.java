package com.tasky.api.repositories;

import com.tasky.api.models.Comment;
import com.tasky.api.models.ToDo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository interface for managing Comment entities.
 */
public interface CommentRepository extends JpaRepository<Comment,Long> {
    /**
     * Retrieves a page of comments associated with a specific ToDo, where the comment names contain the specified pattern.
     *
     * @param toDo     The ToDo associated with the comments to retrieve.
     * @param name     The pattern to search for in comment names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing comments associated with the specified ToDo.
     */
    Page<Comment> getAllByToDoIsAndNameContaining(ToDo toDo, String name, Pageable pageable);
    /**
     * Retrieves a page of comments associated with a specific ToDo, where the comment content contains the specified pattern.
     *
     * @param toDo     The ToDo associated with the comments to retrieve.
     * @param content  The pattern to search for in comment content.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing comments associated with the specified ToDo.
     */
    Page<Comment> getAllByToDoIsAndContentContaining(ToDo toDo, String content, Pageable pageable);

    /**
     * Retrieves a page of comments associated with a specific ToDo.
     *
     * @param toDo     The ToDo associated with the comments to retrieve.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing comments associated with the specified ToDo.
     */
    Page<Comment> getAllByToDoIs(ToDo toDo, Pageable pageable);
}
