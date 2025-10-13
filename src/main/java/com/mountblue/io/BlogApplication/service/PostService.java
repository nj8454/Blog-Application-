package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.PostDetailView;
import com.mountblue.io.BlogApplication.dto.PostDto;
import com.mountblue.io.BlogApplication.dto.PostListItems;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Service
public class PostService {
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private TagService tagService;

    public void savePost(PostDto postDto) {
        Post post = new Post();
        post.setAuthor(postDto.getAuthor());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setExcerpt(postDto.getContent().substring(0, 200) + "........");
        Set<Tag> tags = tagService.saveTags(postDto.getTag());
        post.setTags(tags);
        Post saved = postRepo.save(post);
    }

    public void deletePost(Long postId) {
        postRepo.deleteById(postId);
    }

    //    @Transactional(readOnly = true)
    public Page<PostListItems> getPostList(Pageable pageable) {
        return postRepo.findAll(pageable)
                .map(p -> new PostListItems(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getCreatedAt(),
                        p.getTags().stream().map(t -> t.getName()).toList()
                ));
    }

    //    @Transactional(readOnly = true)
    public PostDetailView detail(Long id) {
        // This calls the @EntityGraph method so tags are fetched eagerly with the Post
        Post p = postRepo.findById(id).orElseThrow();
        return new PostDetailView(
                p.getId(),
                p.getTitle(),
                p.getAuthor(),
                p.getContent(),
                p.getCreatedAt(),
                p.getTags().stream().map(t -> t.getName()).toList()
        );
    }

    public void editPost(Long id, PostDetailView updatedPost) {
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

//    public Page<PostListItems> searchPost(String key, Pageable pageable) {
//        return
//                postRepo.findDistinctByAuthorContainingIgnoreCaseOrTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrTags_NameContainingIgnoreCase
//                        (key, key, key, key, pageable).map(p -> new PostListItems(
//                        p.getId(),
//                        p.getTitle(),
//                        p.getAuthor(),
//                        p.getExcerpt(),
//                        p.getCreatedAt(),
//                        p.getTags().stream().map(t -> t.getName()).toList()
//                ));
//    }

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

    public Page<PostListItems> searchWithFilter(
            String keyword,
            List<String> selectedTags,
            List<String> selectedAuthors,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        boolean hasTags = selectedTags != null && !selectedTags.isEmpty();
        boolean hasAuthor = selectedAuthors != null && !selectedAuthors.isEmpty();

        List<String> tagsLower = hasTags ? selectedTags.stream().filter(Objects::nonNull).map(String::toLowerCase).toList() : List.of();
        List<String> authorsLower = hasAuthor ? selectedAuthors.stream().filter(Objects::nonNull).map(String::toLowerCase).toList() : List.of();

        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return postRepo.searchAndFilter(
                k, hasAuthor, authorsLower, from, to, hasTags, tagsLower, pageable
        ).map(p -> new PostListItems(
                p.getId(), p.getTitle(), p.getAuthor(), p.getExcerpt(),
                p.getCreatedAt(), p.getTags().stream().map(t -> t.getName()).toList()
        ));
    }
}
