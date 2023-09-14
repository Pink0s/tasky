package com.tasky.api.controllers;

import com.tasky.api.dto.comment.CommentDto;
import com.tasky.api.dto.comment.CreateCommentRequest;
import com.tasky.api.dto.comment.SearchCommentsResponse;
import com.tasky.api.dto.comment.UpdateCommentRequest;
import com.tasky.api.services.comment.CommentService;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class handling endpoints related to comments.
 */
@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Creates a new comment associated with a ToDo.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo to associate the comment with.
     * @param request        The request containing the comment details.
     */
    @PostMapping("toDo/{toDoId}")
    @ResponseStatus(HttpStatus.CREATED)
    void createComment(Authentication authentication, @PathVariable Long toDoId, @RequestBody CreateCommentRequest request) {

        logger.info("POST /api/v1/comment/toDo/%s".formatted(toDoId));

        commentService
                .createComment(
                        authentication,
                        toDoId,
                        request
                );
    }

    /**
     * Retrieves a comment by its unique identifier (ID).
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to retrieve.
     * @return The CommentDto containing the retrieved comment information.
     */
    @GetMapping("{commentId}")
    CommentDto getCommentById(Authentication authentication, @PathVariable Long commentId) {

        logger.info("GET /api/v1/comment/%s".formatted(commentId));

        return commentService
                .selectCommentById(
                        authentication,
                        commentId
                );
    }

    /**
     * Retrieves comments containing specific name associated with a ToDo task.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo task to filter the comments for.
     * @param content        The pattern to search for in comment content.
     * @param page           The page number of the result.
     * @return A SearchCommentsResponse containing the filtered comments and pagination information.
     */
    @GetMapping("/toDo/{toDoId}")
    SearchCommentsResponse getAllCommentWhereNameContains(
            Authentication authentication,
            @PathVariable Long toDoId,
            @PathParam("pattern") String content,
            @PathParam("page") Integer page
    ) {
        logger.info("GET /api/v1/comment/toDo/%s".formatted(toDoId));
        return commentService.getAllCommentWhereNameContainsAndToDoIs(authentication,toDoId,content,page);
    }

    /**
     * Updates a comment by its unique identifier (ID).
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to update.
     * @param request        The request containing the updated comment details.
     */
    @PatchMapping("{commentId}")
    void updateComment(Authentication authentication, @PathVariable Long commentId, @RequestBody UpdateCommentRequest request) {
        logger.info("PATCH /api/v1/comment/%s".formatted(commentId));
        commentService.updateComment(authentication,commentId,request);
    }

    /**
     * Deletes a comment by its unique identifier (ID).
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to delete.
     */
    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(Authentication authentication, @PathVariable Long commentId) {
        logger.info("DELETE /api/v1/comment/%s".formatted(commentId));
        commentService.deleteComment(authentication,commentId);
    }

}


