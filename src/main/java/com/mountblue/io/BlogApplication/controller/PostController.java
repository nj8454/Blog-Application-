package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.PostCreateDto;
import com.mountblue.io.BlogApplication.dto.PostDetailDto;
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
        model.addAttribute("post", new PostCreateDto("", "", "", ""));
        return "create-post";
    }

    @PostMapping("/save")
    public String createPost(@ModelAttribute("post") PostCreateDto postCreateDto) {
        postService.savePost(postCreateDto);
        return "redirect:/post/create?success";
    }

    @GetMapping({""})
    public String getAllPost(
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "start", defaultValue = "1") int start,
            Model model) {
        final int PAGE_SIZE = 10;
        int safeStart = Math.max(start, 1);
        int pageIndex = (safeStart - 1) / PAGE_SIZE;

        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(new Sort.Order(dir, "publishedAt"));
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, sort);

        Page<PostDetailDto> page = postService.getPostList(pageable);

        model.addAttribute("posts", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("start", safeStart);
        model.addAttribute("limit", PAGE_SIZE);
        model.addAttribute("nextStart", safeStart + PAGE_SIZE);
        model.addAttribute("prevStart", Math.max(1, safeStart - PAGE_SIZE));
        model.addAttribute("order", dir);
        model.addAttribute("authors", postService.getAuthors());
        model.addAttribute("allTags", tagService.getTags());

        return "all-posts";
    }

    @GetMapping({"/search"})
    public String getAllPostWithSearchAndFilter(@RequestParam(name = "key", required = false) String keyword
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

        String trimKeyword = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword.trim();

        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(new Sort.Order(dir, "publishedAt"));
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, sort);

        Page<PostDetailDto> page;
        page = postService.searchWithFilter(trimKeyword, selectedTags, selectedAuthors, from, to, pageable);

        model.addAttribute("key", trimKeyword);
        model.addAttribute("posts", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("start", safeStart);
        model.addAttribute("limit", PAGE_SIZE);
        model.addAttribute("nextStart", safeStart + PAGE_SIZE);
        model.addAttribute("prevStart", Math.max(1, safeStart - PAGE_SIZE));
        model.addAttribute("order", dir);
        model.addAttribute("authors", postService.getAuthors());
        model.addAttribute("selectedAuthors", selectedAuthors);
        model.addAttribute("allTags", tagService.getTags());
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "all-posts";
    }


    @GetMapping("/{id}")
    public String postDetail(@PathVariable Long postId, Model model) {
        model.addAttribute("post", postService.detail(postId));

        return "post-details";
    }

    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long postId, Model model) {
        PostDetailDto oldPost = postService.detail(postId);
        model.addAttribute("post", oldPost);
        return "post-edit";
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long postId,
                             @ModelAttribute("post") PostDetailDto updatedPost) {
        postService.editPost(postId, updatedPost);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return "redirect:/post";
    }
}

