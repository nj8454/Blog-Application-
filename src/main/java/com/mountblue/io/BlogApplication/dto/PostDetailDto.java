package com.mountblue.io.BlogApplication.dto;

import com.mountblue.io.BlogApplication.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailDto(
        Long id,
        String title,
        String author,
        String excerpt,
        String content,
        LocalDateTime createdAt,
        List<String> tags,
        List<CommentItemDto> comments,
        User user
) {
}
