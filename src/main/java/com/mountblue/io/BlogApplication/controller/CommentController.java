package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.CommentModel;
import com.mountblue.io.BlogApplication.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post/{postId}/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("")
    public String createComment(@PathVariable("postId") Long postId,
                                @ModelAttribute("comment") CommentModel.CommentCreateRequest newComment) {
        commentService.addComment(postId, newComment);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/{commentId}/edit")
    public String showUpdateComment(@PathVariable("postId") Long postId
            , @PathVariable("commentId") Long commentId
            , Model model) {
        CommentModel.CommentItem comment = commentService.getComment(commentId);
        model.addAttribute("postId", postId);
        model.addAttribute("postId", postId);
        model.addAttribute("comment", comment);

        return "comment-edit";
    }

    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @ModelAttribute("comment") CommentModel.CommentItem newComment) {
        commentService.editComment(postId, commentId, newComment);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String removeComment(@PathVariable("postId") Long postId
            , @PathVariable("commentId") Long commentId
            , @ModelAttribute("comment") CommentModel.CommentItem newComment) {

        commentService.deleteComment(commentId);

        return "redirect:/post/" + postId;
    }
}
