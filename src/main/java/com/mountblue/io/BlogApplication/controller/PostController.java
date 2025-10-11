package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.CommentModel;
import com.mountblue.io.BlogApplication.dto.PostDto;
import com.mountblue.io.BlogApplication.service.CommentService;
import com.mountblue.io.BlogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;
    private CommentService commentService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new PostDto());
        return "create-post";
    }

    @PostMapping("/save")
    public String create(@ModelAttribute("post") PostDto postDto) {
        postService.savePost(postDto);
        return "redirect:/post/create?success";
    }

    @GetMapping
    public String getAllPost(Model model){
        model.addAttribute("posts", postService.getPosts());
        return "all-posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.detail(id));
        model.addAttribute("comments", commentService.getComments(id));
        return "post-details";
    }

    @PostMapping("/comment")
    public String createComment(@PathVariable Long id,
                                @ModelAttribute("comment")CommentModel.CommentCreateRequest newComment){
        commentService.addComment(id, newComment);
        return "redirect:/posts/" + id;
    }

}

