package com.mountblue.io.BlogApplication.dto;

public record CommentItemDto(
        Long id,
        String name,
        String email,
        String text,
        java.time.LocalDateTime createdAt
) {
}

