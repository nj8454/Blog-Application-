// src/main/java/com/mountblue/io/BlogApplication/service/PostListItem.java
package com.mountblue.io.BlogApplication.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostListItems(
        Long id,
        String title,
        String author,
        String excerpt,
        LocalDateTime createdAt,
        List<String> tags
) {}


