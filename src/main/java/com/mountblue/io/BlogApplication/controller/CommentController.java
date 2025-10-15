package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.CommentCreateDto;
import com.mountblue.io.BlogApplication.dto.CommentItemDto;
import com.mountblue.io.BlogApplication.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post/{postId}/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    public String createComment(@PathVariable("postId") Long postId,
                                @ModelAttribute("comment") CommentCreateDto newComment) {
        commentService.addComment(postId, newComment);

        return "redirect:/post/" + postId;
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    @GetMapping("/{commentId}/edit")
    public String showUpdateComment(@PathVariable("postId") Long postId
            , @PathVariable("commentId") Long commentId
            , Model model) {
        CommentItemDto comment = commentService.getComment(commentId);
        model.addAttribute("postId", postId);
        model.addAttribute("comment", comment);

        return "comment-edit";
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @ModelAttribute("comment") CommentItemDto newComment) {
        commentService.editComment(commentId, newComment);

        return "redirect:/post/" + postId;
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    @PostMapping("/{commentId}/delete")
    public String removeComment(@PathVariable("postId") Long postId
            , @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);

        return "redirect:/post/" + postId;
    }
}
