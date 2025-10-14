package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.CommentItem;
import com.mountblue.io.BlogApplication.dto.PostCreateDto;
import com.mountblue.io.BlogApplication.dto.PostDetailDto;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private TagService tagService;

    public void savePost(PostCreateDto postCreateDto) {
        Post post = new Post();
        post.setAuthor(postCreateDto.author());
        post.setTitle(postCreateDto.title());
        post.setContent(postCreateDto.content());
        post.setExcerpt(postCreateDto.content().substring(0, 200) + "........");
        Set<Tag> tags = tagService.saveTags(postCreateDto.tag());
        post.setTags(tags);
        postRepo.save(post);
    }

    public void deletePost(Long postId) {
        postRepo.deleteById(postId);
    }

    public Page<PostDetailDto> getPostList(Pageable pageable) {
        return postRepo.findAll(pageable)
                .map(p -> new PostDetailDto(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getContent(),
                        p.getCreatedAt(),
                        p.getTags().stream().map(t -> t.getName()).toList(),
                        p.getComments().stream()
                                .map(c -> new CommentItem(
                                        c.getId(),
                                        c.getName(),
                                        c.getEmail(),
                                        c.getComment(),
                                        c.getCreatedAt()
                                ))
                                .toList()
                ));
    }

    public PostDetailDto detail(Long id) {
        Post p = postRepo.findById(id).orElseThrow();
        return new PostDetailDto(
                p.getId(),
                p.getTitle(),
                p.getAuthor(),
                p.getExcerpt(),
                p.getContent(),
                p.getCreatedAt(),
                p.getTags().stream().map(t -> t.getName()).toList(),
                p.getComments().stream()
                        .map(c -> new CommentItem(
                                c.getId(),
                                c.getName(),
                                c.getEmail(),
                                c.getComment(),
                                c.getCreatedAt()
                        ))
                        .toList()
        );
    }

    public void editPost(Long id, PostDetailDto updatedPost) {
        Post oldPost = postRepo.findById(id).orElseThrow();

        oldPost.setAuthor(updatedPost.author());
        oldPost.setTitle(updatedPost.title());
        oldPost.setContent(updatedPost.content());
        oldPost.setExcerpt(updatedPost.content().substring(0, 200));

        String tagsAsString = String.join(",", updatedPost.tags());
        Set<Tag> tags = tagService.saveTags(tagsAsString);
        oldPost.setTags(tags);

        postRepo.save(oldPost);
    }

    public Set<String> getAuthors() {
        List<Post> posts = postRepo.findAll();
        Set<String> authors = new LinkedHashSet<>();
        for (Post post : posts) {
            String a = post.getAuthor();
            if (a != null && !a.isBlank()) {
                authors.add(a.trim());
            }
        }
        return authors;
    }

    public Page<PostDetailDto> searchWithFilter(
            String keyword,
            List<String> selectedTags,
            List<String> selectedAuthors,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        boolean hasTags = selectedTags != null && !selectedTags.isEmpty();
        boolean hasAuthor = selectedAuthors != null && !selectedAuthors.isEmpty();

        List<String> tagsLower =
                (selectedTags == null || selectedTags.isEmpty())
                        ? List.of()
                        : selectedTags.stream()
                        .map(s -> s.toLowerCase())
                        .toList();
        List<String> authorsLower =
                (selectedAuthors == null || selectedAuthors.isEmpty())
                        ? List.of()
                        : selectedAuthors.stream()
                        .map(s -> s.toLowerCase())
                        .toList();

        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return postRepo.searchAndFilter(k, hasAuthor, authorsLower, from, to, hasTags, tagsLower, pageable)
                .map(p -> new PostDetailDto(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getContent(),
                        p.getCreatedAt(),
                        p.getTags().stream()
                                .map(t -> t.getName()).toList(),
                        p.getComments().stream()
                                .map(c -> new CommentItem(
                                        c.getId(),
                                        c.getName(),
                                        c.getEmail(),
                                        c.getComment(),
                                        c.getCreatedAt()
                                ))
                                .toList()
                ));
    }
}
