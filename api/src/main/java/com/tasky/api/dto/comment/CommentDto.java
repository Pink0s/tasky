package com.tasky.api.dto.comment;

import java.sql.Timestamp;

public record CommentDto (Long commentId, String name, String content, Timestamp creationDate) {
}
