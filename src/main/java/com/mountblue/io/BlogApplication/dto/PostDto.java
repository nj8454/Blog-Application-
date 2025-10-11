package com.mountblue.io.BlogApplication.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class PostDto {
    private String title;
    private String author;
    private String content;
    private String tag;
}
