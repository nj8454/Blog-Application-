package com.mountblue.io.BlogApplication.dto;

public record PostCreateDto(
        String title,
        String author,
        String content,
        String tag) {
}
