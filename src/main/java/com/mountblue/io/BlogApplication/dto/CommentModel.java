package com.mountblue.io.BlogApplication.dto;

public class CommentModel {
    public record CommentCreateRequest(
            String name,
            String email,
            String text
    ) {}

    public record CommentItem(
            Long id,
            String name,
            String email,
            String text,
            java.time.LocalDateTime createdAt
    ) {}
}
