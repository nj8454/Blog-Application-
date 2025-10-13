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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public Page<PostListItems> searchPost(String key, Pageable pageable) {
        return
                postRepo.findDistinctByAuthorContainingIgnoreCaseOrTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrTags_NameContainingIgnoreCase
                        (key, key, key, key, pageable).map(p -> new PostListItems(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getCreatedAt(),
                        p.getTags().stream().map(t -> t.getName()).toList()
                ));
    }

    public List<String> getAuthors() {
        List<Post> posts = postRepo.findAll();
        List<String> authors = new ArrayList<>();
        for (Post post : posts) {
            String authorName = post.getAuthor();
            authors.add(authorName);
        }

        return authors;
    }

    public Page<PostListItems> searchWithFilter(String keyword, List<String> selectedTags, String author, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        boolean hasTags = true;
        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
            hasTags = false;
        }
        return postRepo.searchAndFilter(keyword,
                        author,
                        from,
                        to,
                        hasTags,
                        selectedTags, pageable)
                .map(p -> new PostListItems(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getCreatedAt(),
                        p.getTags().stream().map(t -> t.getName()).toList()
                ));
    }
}
