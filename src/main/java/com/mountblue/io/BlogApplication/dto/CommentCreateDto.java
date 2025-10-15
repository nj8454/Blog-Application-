package com.mountblue.io.BlogApplication.dto;

public record CommentCreateDto(
        String name,
        String email,
        String text
) {
}