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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional(readOnly = true)
    public List<PostListItems> getPosts() {
        return postRepo.findAll()
                .stream()
                .map(p -> new PostListItems(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthor(),
                        p.getExcerpt(),
                        p.getCreatedAt(),
                        p.getTags().stream().map(t -> t.getName()).toList()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
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
}
