package com.mountblue.io.BlogApplication.Restcontroller;

import com.mountblue.io.BlogApplication.dto.CommentCreateDto;
import com.mountblue.io.BlogApplication.dto.CommentItemDto;
import com.mountblue.io.BlogApplication.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class CommentRestController {
    private CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentItemDto> createComment(@PathVariable("postId") Long postId,
                                                        @RequestBody CommentCreateDto newComment) {
        CommentItemDto created = commentService.addComment(postId, newComment);

        return ResponseEntity.created(URI.create("/comments/" + created.id())).body(created);
    }


    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentItemDto> updateComment(@PathVariable("commentId") Long commentId,
                                                        @ModelAttribute("comment") CommentCreateDto newComment) {
        CommentItemDto updated = commentService.editComment(commentId, newComment);

        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> removeComment(@PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
