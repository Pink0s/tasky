package com.tasky.api.dto.comment;

import com.tasky.api.dto.PageableDto;

import java.util.List;

public record CommentListDto(List<CommentDto> list, PageableDto pageableDto) {
}
