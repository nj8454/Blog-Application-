package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.entities.PostTag;
import com.mountblue.io.BlogApplication.entities.User;
import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import com.mountblue.io.BlogApplication.repository.PostTagRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
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
    @Autowired
    private PostTagRepository postTagRepo;

    public PostDto savePost(PostDto postDto) {
        User user = new User((long)1, "Nikhil jain", "nj8454@gmail.com", "0000");
        Post post = new Post();
        post.setAuthor(postDto.getAuthor());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setExcerpt(postDto.getContent());

        Set<Tag> postTags = tagService.saveTags(postDto.getTag());
//        post.setUser(user);
        Post savedPost = postRepo.save(post);

        Set<PostTag> links = new HashSet<>();
        for (Tag tag : postTags) {
            PostTag link = new PostTag();
            link.setPosts(savedPost.getId());
            link.setTag(tag);
            links.add(link);
        }

        post.setPostTags(links);


        // Convert back to DTO
        PostDto responseDto = new PostDto();
        responseDto.setId(savedPost.getId());
        responseDto.setAuthor(savedPost.getAuthor());
        responseDto.setTitle(savedPost.getTitle());
        responseDto.setContent(savedPost.getContent());
        responseDto.setPostTags(links);

        return responseDto;
    }
}
