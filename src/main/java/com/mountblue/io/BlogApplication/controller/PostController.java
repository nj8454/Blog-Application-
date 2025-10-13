package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.CommentModel;
import com.mountblue.io.BlogApplication.dto.PostDetailView;
import com.mountblue.io.BlogApplication.dto.PostDto;
import com.mountblue.io.BlogApplication.dto.PostListItems;
import com.mountblue.io.BlogApplication.service.CommentService;
import com.mountblue.io.BlogApplication.service.PostService;
import com.mountblue.io.BlogApplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping({""})
    public String getAllPost(
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "start", defaultValue = "1") int start,
            Model model
    ) {
        final int PAGE_SIZE = 10;
        int safeStart = Math.max(start, 1);
        int pageIndex = (safeStart - 1) / PAGE_SIZE;

        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE,
                Sort.by(new Sort.Order(dir, "publishedAt")));

        Page<PostListItems> page = postService.getPostList(pageable);

        model.addAttribute("posts", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("start", safeStart);
        model.addAttribute("limit", PAGE_SIZE);
        model.addAttribute("nextStart", safeStart + PAGE_SIZE);
        model.addAttribute("prevStart", Math.max(1, safeStart - PAGE_SIZE));
        model.addAttribute("order", dir.isAscending() ? "asc" : "desc");
        model.addAttribute("authors", postService.getAuthors());
        model.addAttribute("allTags", tagService.getTags());

        return "all-posts";
    }

    @GetMapping({"/search"})
    public String getAllPostWithFilter(@RequestParam(name = "key", required = false) String keyword
            , @RequestParam(name = "authors", required = false) List<String> selectedAuthors
            , @RequestParam(name = "order", defaultValue = "desc") String order
            , @RequestParam(name = "start", defaultValue = "1") int start
            , @RequestParam(name = "from", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from
            , @RequestParam(name = "to", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
            , @RequestParam(name = "tags", required = false) List<String> selectedTags
            , Model model) {
        final int PAGE_SIZE = 10;

        int safeStart = Math.max(start, 1);
        int pageIndex = (safeStart - 1) / PAGE_SIZE;

        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(new Sort.Order(dir, "publishedAt"));
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, sort);

        Page<PostListItems> page;
        page = postService.searchWithFilter(keyword.trim(), selectedTags, selectedAuthors, from, to, pageable);
        model.addAttribute("key", keyword.trim());


        model.addAttribute("posts", page.getContent());
        model.addAttribute("page", page);

        // navigation with fixed size=10 and preserved order
        model.addAttribute("start", safeStart);
        model.addAttribute("limit", PAGE_SIZE);
        model.addAttribute("nextStart", safeStart + PAGE_SIZE);
        model.addAttribute("prevStart", Math.max(1, safeStart - PAGE_SIZE));
        model.addAttribute("order", dir.isAscending() ? "asc" : "desc");
        model.addAttribute("authors", postService.getAuthors());
        model.addAttribute("selectedAuthors", selectedAuthors);
        model.addAttribute("allTags", tagService.getTags());

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

