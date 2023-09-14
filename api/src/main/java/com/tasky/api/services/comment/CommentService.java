package com.tasky.api.services.comment;

import com.tasky.api.dto.comment.CommentDto;
import com.tasky.api.dto.comment.CreateCommentRequest;
import com.tasky.api.dto.comment.SearchCommentsResponse;
import com.tasky.api.dto.comment.UpdateCommentRequest;
import org.springframework.security.core.Authentication;

/**
 * The CommentService interface defines methods to handle operations related to comments.
 */
public interface CommentService {

    /**
     * Creates a new comment associated with a ToDo.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo to associate the comment with.
     * @param request        The request containing the comment details.
     */
    void createComment(Authentication authentication, Long toDoId, CreateCommentRequest request);

    /**
     * Retrieves a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to retrieve.
     * @return A CommentDto containing the retrieved comment information.
     */
    CommentDto selectCommentById(Authentication authentication, Long commentId);

    /**
     * Updates a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to update.
     * @param request        The request containing the updated comment details.
     */
    void updateComment(Authentication authentication, Long commentId, UpdateCommentRequest request);

    /**
     * Deletes a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to delete.
     */
    void deleteComment(Authentication authentication, Long commentId);

    /**
     * Retrieves comments containing a specific name associated with a ToDo.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo to filter the comments for.
     * @param name           The pattern to search for in comment names.
     * @param page           The page number of the result.
     * @return A SearchCommentsResponse containing the filtered comments and pagination information.
     */
    SearchCommentsResponse getAllCommentWhereNameContainsAndToDoIs(Authentication authentication, Long toDoId, String name, Integer page);

}