package com.tasky.api.dao.comment;
import com.tasky.api.models.Comment;
import com.tasky.api.models.ToDo;
import com.tasky.api.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of the CommentDao interface providing methods to interact with comment data.
 */
@Repository("COMMENT_JPA")
public class CommentDaoImpl implements CommentDao {

    private final CommentRepository repository;

    /**
     * Constructs a CommentDaoImpl with the necessary dependencies.
     *
     * @param repository The CommentRepository implementation for accessing comment data.
     */
    public CommentDaoImpl(CommentRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createComment(Comment comment) {
        repository.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Comment> selectCommentById(Long id) {
        return repository.findById(id);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Comment> selectAllCommentForTodoWhereNameContains(ToDo toDo, String name, Pageable pageable) {
        return repository.getAllByToDoIsAndNameContaining(toDo, name, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Comment> selectAllCommentForTodoWhereContentContains(ToDo toDo, String content, Pageable pageable) {
        return repository.getAllByToDoIsAndContentContaining(toDo, content, pageable);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateComment(Comment comment) {
        repository.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCommentById(Long id) {
        repository.deleteById(id);
    }
}
