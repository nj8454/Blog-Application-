package com.mountblue.io.BlogApplication.Restcontroller;

import com.mountblue.io.BlogApplication.config.UserPrincipal;
import com.mountblue.io.BlogApplication.dto.PostCreateDto;
import com.mountblue.io.BlogApplication.dto.PostDetailDto;
import com.mountblue.io.BlogApplication.service.CommentService;
import com.mountblue.io.BlogApplication.service.PostService;
import com.mountblue.io.BlogApplication.service.TagService;
import com.mountblue.io.BlogApplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private PostService postService;
    private CommentService commentService;
    private TagService tagService;
    private UserService userService;

    public PostRestController(PostService postService,
                              CommentService commentService,
                              TagService tagService,
                              UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Page<PostDetailDto>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> authors,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 10, sort = "publishedAt") Pageable pageable
    ) {
        Page<PostDetailDto> page = postService.searchWithFilter(keyword, tags, authors, from, to, pageable);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostDetailDto> postDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.detail(id));
    }

    @PreAuthorize("hasAnyRole({'ADMIN', 'AUTHOR'})")
    @PostMapping("")
    public ResponseEntity<PostDetailDto> create(@RequestBody PostCreateDto postCreateDto,
                                                @AuthenticationPrincipal UserPrincipal currentUser,
                                                @RequestParam(value = "authorId", required = false) Long authorId) {
        System.out.println(currentUser.getEmail());
        PostDetailDto created = postService.savePost(postCreateDto, currentUser, authorId);
        return ResponseEntity.created(URI.create("/post/" + created.id())).body(created);
    }


    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<PostDetailDto> updatePost(@PathVariable("id") Long id,
                                                    @RequestBody PostDetailDto updatedPost,
                                                    @AuthenticationPrincipal UserPrincipal currentUser,
                                                    @RequestParam(value = "authorId", required = false) Long authorId) {
        PostDetailDto updated = postService.editPost(id, updatedPost, currentUser, authorId);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}

