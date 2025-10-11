package com.mountblue.io.BlogApplication.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailView(
        Long id,
        String title,
        String author,
        String content,
        LocalDateTime createdAt,
        List<String> tags
) {}
