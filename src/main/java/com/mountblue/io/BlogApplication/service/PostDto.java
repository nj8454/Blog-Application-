package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.entities.PostTag;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Component
public class PostDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private String tag;
    private Set<PostTag> postTags;
}
