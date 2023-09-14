package com.tasky.api.dao.comment;

import com.tasky.api.models.Comment;
import com.tasky.api.models.ToDo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The CommentDao interface defines methods to interact with comment data.
 */
public interface CommentDao {

    /**
     * Creates a new comment.
     *
     * @param comment The Comment object to be created.
     */
    void createComment(Comment comment);

    /**
     * Retrieves a comment by its unique identifier (ID).
     *
     * @param id The unique identifier (ID) of the comment to retrieve.
     * @return An Optional containing the retrieved Comment object, or an empty Optional if not found.
     */
    Optional<Comment> selectCommentById(Long id);

    /**
     * Retrieves a page of comments associated with a specific ToDo based on the ToDo's unique identifier,
     * where the comment's name contains the specified pattern.
     *
     * @param toDo     The ToDo to retrieve comments for.
     * @param name     The pattern to search for in comment names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing comments associated with the specified ToDo that match the name pattern.
     */
    Page<Comment> selectAllCommentForTodoWhereNameContains(ToDo toDo, String name, Pageable pageable);

    /**
     * Retrieves a page of comments associated with a specific ToDo based on the ToDo's unique identifier,
     * where the comment's content contains the specified pattern.
     *
     * @param toDo     The ToDo to retrieve comments for.
     * @param content  The pattern to search for in comment content.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing comments associated with the specified ToDo that match the content pattern.
     */
    Page<Comment> selectAllCommentForTodoWhereContentContains(ToDo toDo, String content, Pageable pageable);

    /**
     * Updates an existing comment.
     *
     * @param comment The Comment object containing updated information to be saved.
     */
    void updateComment(Comment comment);

    /**
     * Deletes a comment by its unique identifier (ID).
     *
     * @param id The unique identifier (ID) of the comment to be deleted.
     */
    void deleteCommentById(Long id);

}
