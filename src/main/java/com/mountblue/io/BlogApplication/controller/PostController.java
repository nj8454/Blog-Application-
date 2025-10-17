package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.config.UserPrincipal;
import com.mountblue.io.BlogApplication.dto.CommentCreateDto;
import com.mountblue.io.BlogApplication.dto.PostCreateDto;
import com.mountblue.io.BlogApplication.dto.PostDetailDto;
import com.mountblue.io.BlogApplication.service.CommentService;
import com.mountblue.io.BlogApplication.service.PostService;
import com.mountblue.io.BlogApplication.service.TagService;
import com.mountblue.io.BlogApplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping({"/", "/post"})
public class PostController {
    private PostService postService;
    private CommentService commentService;
    private TagService tagService;
    private UserService userService;

    public PostController(PostService postService,
                          CommentService commentService,
                          TagService tagService,
                          UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        model.addAttribute("post", new PostCreateDto("", "", "", ""));
        model.addAttribute("authorName", currentUser.getDisplayName());

        boolean isAdmin = currentUser
                .getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            model.addAttribute("authors", userService.findAuthors());
        }
        return "create-post";
    }

    @PreAuthorize("hasAnyRole('ADMIN','AUTHOR')")
    @PostMapping("/save")
    public String createPost(@ModelAttribute("post") PostCreateDto postCreateDto,
                             @RequestParam(value = "authorId", required = false) Long authorId,
                             @AuthenticationPrincipal UserPrincipal currentUser) {
        postService.savePost(postCreateDto, currentUser, authorId);
        return "redirect:/post/create";
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
    public String postDetail(@PathVariable("id") Long id,
                             Model model,
                             @AuthenticationPrincipal UserPrincipal currentUser) {
        model.addAttribute("post", postService.detail(id));
        model.addAttribute("comment", new CommentCreateDto("", "", ""));
        Long currentUserId = (currentUser != null) ? currentUser.getId() : null;
        model.addAttribute("currentUserId", currentUserId);

        return "post-details";
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserPrincipal currentUser,
                                 Model model) {
        PostDetailDto oldPost = postService.detail(id);
        model.addAttribute("post", oldPost);
        model.addAttribute("authorName", currentUser.getDisplayName());

        boolean isAdmin = currentUser
                .getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            model.addAttribute("authors", userService.findAuthors());
        }

        return "post-edit";
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable("id") Long id,
                             @ModelAttribute("post") PostDetailDto updatedPost,
                             @RequestParam(value = "authorId", required = false) Long authorId,
                             @AuthenticationPrincipal UserPrincipal currentUser) {
        postService.editPost(id, updatedPost, currentUser, authorId);
        return "redirect:/post/" + id;
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return "redirect:/post";
    }
}

