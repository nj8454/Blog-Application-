package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.CommentModel;
import com.mountblue.io.BlogApplication.dto.PostDetailView;
import com.mountblue.io.BlogApplication.dto.PostDto;
import com.mountblue.io.BlogApplication.dto.PostListItems;
import com.mountblue.io.BlogApplication.service.CommentService;
import com.mountblue.io.BlogApplication.service.PostService;
import com.mountblue.io.BlogApplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;

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

    @GetMapping({"", "/search"})
    public String getAllPost(@RequestParam(name = "key", required = false) String keyword, Model model) {

        if (keyword != null) {
            keyword = keyword.trim();
            model.addAttribute("posts", postService.searchPost(keyword));
            model.addAttribute("key", keyword);
            model.addAttribute("authors", postService.getAuthors());
            model.addAttribute("allTags", tagService.getTags());
        } else {
            model.addAttribute("posts", postService.getPostList());
        }
        return "all-posts";
    }

    @GetMapping({"/search/filter"})
    public String getAllPostWithFilter(@RequestParam(name = "key", required = false) String keyword
            , @RequestParam(name = "author", required = false) String author
            , @RequestParam(name = "from", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from
            , @RequestParam(name = "to", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
            , @RequestParam(name = "tags", required = false) List<String> selectedTags
            , Model model) {
        model.addAttribute("key", keyword);
        model.addAttribute("author", postService.getAuthors());
        model.addAttribute("tags", tagService.getTags());
        model.addAttribute("posts", postService.searchWithFilter(keyword, selectedTags, author, from, to));


        return "all-posts";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.detail(id));
        model.addAttribute("comments", commentService.getCommentsList(id));
        model.addAttribute("comment", new CommentModel.CommentCreateRequest("", "", ""));

        return "post-details";
    }


    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        PostDetailView previousPost = postService.detail(id);
        model.addAttribute("post", previousPost);
        return "post-edit";
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") PostDetailView updatedPost) {
        postService.editPost(id, updatedPost);
        return "redirect:/post/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/post";
    }


}

