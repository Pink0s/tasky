package com.tasky.api.services.comment;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.comment.CommentDao;
import com.tasky.api.dao.toDo.ToDoDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.comment.CommentDto;
import com.tasky.api.dto.comment.CreateCommentRequest;
import com.tasky.api.dto.comment.SearchCommentsResponse;
import com.tasky.api.dto.comment.UpdateCommentRequest;
import com.tasky.api.mappers.CommentDtoMapper;
import com.tasky.api.models.Comment;
import com.tasky.api.models.Project;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the CommentService interface to handle operations related to comments.
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final ToDoDao toDoDao;
    private final CommentDao commentDao;
    private final CommentDtoMapper commentDtoMapper;

    public CommentServiceImpl(@Qualifier("TO_DO_JPA") ToDoDao toDoDao, @Qualifier("COMMENT_JPA") CommentDao commentDao, CommentDtoMapper commentDtoMapper) {
        this.toDoDao = toDoDao;
        this.commentDao = commentDao;
        this.commentDtoMapper = commentDtoMapper;
    }

    /**
     * Creates a new comment associated with a ToDo.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo to associate the comment with.
     * @param request        The request containing the comment details.
     */
    @Override
    public void createComment(Authentication authentication, Long toDoId, CreateCommentRequest request) {
        ToDo toDo = evaluateTodoId(toDoId);
        evaluateUserAccessToTodo(authentication,toDo);
        verifyCreateRequest(request);
        Comment comment = new Comment(request.name(), request.content(), toDo);
        commentDao.createComment(comment);
    }

    /**
     * Retrieves a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to retrieve.
     * @return A CommentDto containing the retrieved comment information.
     * @throws NotFoundException   If the comment with the given ID is not found.
     * @throws UnauthorizedException If the user doesn't have access to the comment resource.
     */
    @Override
    public CommentDto selectCommentById(Authentication authentication, Long commentId) {
        Comment comment = retrieveCommentById(commentId);
        evaluateAccessToComment(comment,authentication);
        return commentDtoMapper.apply(comment);
    }

    /**
     * Updates a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to update.
     * @param request        The request containing the updated comment details.
     * @throws NotFoundException   If the comment with the given ID is not found.
     * @throws UnauthorizedException If the user doesn't have access to the comment resource.
     * @throws BadRequestException   If no changes are made to the comment.
     */
    @Override
    public void updateComment(Authentication authentication, Long commentId, UpdateCommentRequest request) {
        Comment comment = retrieveCommentById(commentId);
        evaluateAccessToComment(comment,authentication);
        Comment commentModified = processCommentChanges(comment,request);
        commentDao.updateComment(commentModified);
    }

    /**
     * Deletes a comment by its unique identifier (ID) with access control.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param commentId      The unique identifier (ID) of the comment to delete.
     * @throws NotFoundException   If the comment with the given ID is not found.
     * @throws UnauthorizedException If the user doesn't have access to the comment resource.
     */
    @Override
    public void deleteComment(Authentication authentication, Long commentId) {
        Comment comment = retrieveCommentById(commentId);
        evaluateAccessToComment(comment,authentication);
        commentDao.deleteCommentById(commentId);
    }

    /**
     * Retrieves comments containing a specific name associated with a ToDo.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param toDoId         The unique identifier (ID) of the ToDo to filter the comments for.
     * @param name            The pattern to search for in comment names.
     * @param page            The page number of the result.
     * @throws UnauthorizedException If the user doesn't have access toDo resource.
     * @throws BadRequestException If the page is not coherent
     * @return A SearchCommentsResponse containing the filtered comments and pagination information.
     */
    @Override
    public SearchCommentsResponse getAllCommentWhereNameContainsAndToDoIs(Authentication authentication, Long toDoId, String name, Integer page) {
        ToDo toDo = evaluateTodoId(toDoId);
        evaluateUserAccessToTodo(authentication,toDo);
        page = proccessPage(page);
        Pageable pageable = buildPageable(page);
        String pattern = proccessPattern(name);
        Page<Comment> pageResult = commentDao.selectAllCommentForTodoWhereNameContains(toDo,pattern,pageable);
        return buildSearchResponse(pageResult,page);
    }

    private SearchCommentsResponse buildSearchResponse(Page<Comment> pageComment, Integer page) {

        if(page != 0 && (pageComment.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        PageableDto pageableDto = new PageableDto(
                page,
                pageComment.getTotalPages()-1 > -1 ? pageComment.getTotalPages()-1 : 0,
                pageComment.getTotalPages(),
                pageComment.getNumberOfElements()
        );



        List<CommentDto> comments =
                pageComment.getContent()
                        .stream()
                        .map(commentDtoMapper)
                        .toList();


        return new SearchCommentsResponse(
                comments,
                pageableDto
        );
    }

    private Integer proccessPage(Integer page) {
        return page == null ? 0 : page;
    }

    private Pageable buildPageable(Integer page) {
        return PageRequest.of(page,5);
    }

    private String proccessPattern(String pattern) {
        return pattern == null ? "" : pattern;
    }

    private User retrieveUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private void  evaluateAccessToComment(Comment comment, Authentication authentication) {
        User user = retrieveUserFromAuthentication(authentication);
        boolean isFullAccess = user.getRole().equals("PROJECT_MANAGER");

        if (!isFullAccess) {
            comment.getToDo()
                    .getFeature()
                    .getProject()
                    .getUsers()
                    .stream()
                    .filter(
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }
    }

    private ToDo evaluateTodoId(Long toDoId) {
        return toDoDao.selectToDoById(toDoId).orElseThrow(
                () -> new NotFoundException("ToDo with id %s does not exists".formatted(toDoId))
        );
    }

    private void evaluateUserAccessToTodo(Authentication authentication, ToDo toDo) {
        User user = retrieveUserFromAuthentication(authentication);

        boolean fullAccess = user.getRole().equals("PROJECT_MANAGER");

        if(!fullAccess) {
            Project project = toDo.getFeature().getProject();

            project.getUsers()
                    .stream()
                    .filter(
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }
    }

    private void verifyCreateRequest(CreateCommentRequest request) {
        boolean isBadRequest = false;
        List<String> stackTrace = new ArrayList<>();
        if(request.content() == null) {
            String message = "Missing field content";
            stackTrace.add(message);
            logger.error(message);
            isBadRequest = true;
        }

        if(request.name() == null) {
            String message = "Missing field name";
            stackTrace.add(message);
            logger.error(message);
            isBadRequest = true;
        }

        if(isBadRequest) {
            throw new BadRequestException(stackTrace.toString());
        }
    }

    private Comment retrieveCommentById(Long commentId) {
        return commentDao
                .selectCommentById(
                        commentId
                )
                .orElseThrow(
                        () -> new NotFoundException(
                                "Comment with id %s does not exists".formatted(commentId)
                        )
                );
    }

    private Comment processCommentChanges(Comment comment, UpdateCommentRequest request) {
        boolean changes = false;

        if(request.name() != null && !request.name().equals(comment.getName())) {
            changes = true;
            comment.setName(request.name());
        }

        if(request.content() != null&& !request.content().equals(comment.getContent())) {
            changes = true;
            comment.setContent(request.content());
        }

        if(!changes) {
            throw  new BadRequestException("No changes");
        }

        comment.setUpdatedAt(Timestamp.from(Instant.now()));

        return comment;
    }

}
