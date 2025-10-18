package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.config.UserPrincipal;
import com.mountblue.io.BlogApplication.dto.CommentItemDto;
import com.mountblue.io.BlogApplication.dto.PostCreateDto;
import com.mountblue.io.BlogApplication.dto.PostDetailDto;
import com.mountblue.io.BlogApplication.dto.UserDetailDto;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.entities.User;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import com.mountblue.io.BlogApplication.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private PostRepository postRepo;
    private TagService tagService;
    private UserRepository userRepo;

    public PostService(PostRepository postRepo, TagService tagService, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.tagService = tagService;
        this.userRepo = userRepo;
    }

    public PostDetailDto savePost(PostCreateDto dto, UserPrincipal currentUser, Long authorId) {
        Post post = new Post();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setExcerpt(makeExcerpt(dto.content()));
        post.setTags(tagService.saveTags(dto.tag()));
        boolean isAdmin = currentUser
                .getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
        User owner;
        if (isAdmin && authorId != null) {
            owner = userRepo.findById(authorId).orElseThrow();
        } else {
            owner = userRepo.getReferenceById(currentUser.getId());
        }
        post.setUser(owner);
        post.setAuthor(owner.getName());
        return toDetailDto(postRepo.save(post));
    }

    public void deletePost(Long postId) {
        postRepo.deleteById(postId);
    }


    public PostDetailDto detail(Long id) {
        Post post = postRepo.findById(id).orElseThrow(NoSuchElementException::new);
        return toDetailDto(post);
    }

    public PostDetailDto editPost(Long id, PostDetailDto dto, UserPrincipal currentUser, Long authorId) {
        Post post = postRepo.findById(id).orElseThrow(NoSuchElementException::new);
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setExcerpt(makeExcerpt(dto.content()));

        String tagsAsString = String.join(",", dto.tags());
        post.setTags(tagService.saveTags(tagsAsString));
        post.setUpdatedAt(LocalDateTime.now());
        return toDetailDto(postRepo.save(post));
    }


    public Set<String> getAuthors() {
        List<Post> posts = postRepo.findAll();
        Set<String> authors = new LinkedHashSet<>();
        for (Post post : posts) {
            String author = post.getAuthor();
            if (author != null && !author.isBlank()) {
                authors.add(author.trim());
            }
        }
        return authors;
    }

    public Page<PostDetailDto> searchWithFilter(
            String keyword,
            List<String> tags,
            List<String> authors,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        boolean hasAuthors = authors != null && !authors.isEmpty();
        boolean hasTags = tags != null && !tags.isEmpty();
        List<String> authorsLower = hasAuthors ? authors.stream().map(String::toLowerCase).toList() : Collections.emptyList();
        List<String> tagsLower = hasTags ? tags.stream().map(String::toLowerCase).toList() : Collections.emptyList();
        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return postRepo.searchAndFilter(k, hasAuthors, authorsLower, from, to, hasTags, tagsLower, pageable)
                .map(this::toDetailDto);
    }

    private String makeExcerpt(String content) {
        if (content == null) return "";
        int end = Math.min(200, content.length());
        return content.substring(0, end);
    }

    private PostDetailDto toDetailDto(Post post) {
        List<String> tagNames = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        List<CommentItemDto> comments = post.getComments().stream()
                .sorted(Comparator.comparing(c -> c.getCreatedAt() == null ? LocalDateTime.MIN : c.getCreatedAt()))
                .map(c -> new CommentItemDto(c.getId(), c.getName(), c.getEmail(), c.getComment(), c.getCreatedAt()))
                .toList();
        User user = post.getUser();
        return new PostDetailDto(post.getId(), post.getTitle(), post.getAuthor(), post.getExcerpt(),
                post.getContent(), post.getPublishedAt() != null ? post.getPublishedAt() : post.getCreatedAt(),
                tagNames, comments,
                new UserDetailDto(user.getId(), user.getName(), user.getEmail(), null));
    }
}
