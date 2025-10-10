package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.service.PostDto;
import com.mountblue.io.BlogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;
    @PostMapping("/save")
    public PostDto post(@RequestBody PostDto postDto){

        System.out.println(postDto);
        return postService.savePost(postDto);
    }
}
