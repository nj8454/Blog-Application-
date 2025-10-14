package com.mountblue.io.BlogApplication.dto;

public record CommentItem(
        Long id,
        String name,
        String email,
        String text,
        java.time.LocalDateTime createdAt
) {
}

