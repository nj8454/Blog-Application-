package com.mountblue.io.BlogApplication.dto;

public record CommentCreateRequest(
        String name,
        String email,
        String text
) {
}