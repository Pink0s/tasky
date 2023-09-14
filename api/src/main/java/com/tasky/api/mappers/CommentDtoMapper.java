package com.tasky.api.mappers;

import com.tasky.api.dto.comment.CommentDto;
import com.tasky.api.models.Comment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * The CommentDtoMapper class converts Comment objects to CommentDto objects.
 */
@Service
public class CommentDtoMapper implements Function<Comment, CommentDto> {
    /**
     * Converts a Comment object to a CommentDto object.
     *
     * @param comment The Comment object to be converted.
     * @return A CommentDto object representing the converted Comment.
     */
    @Override
    public CommentDto apply(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
