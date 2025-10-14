package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.CommentCreateRequest;
import com.mountblue.io.BlogApplication.dto.CommentItem;
import com.mountblue.io.BlogApplication.entities.Comment;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.repository.CommentRepository;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepo;
    @Autowired
    private PostRepository postRepo;

    public void addComment(Long postId, CommentCreateRequest newComment) {
        Post post = postRepo.findById(postId).orElseThrow();

        Comment comment = new Comment();

        comment.setComment(newComment.text());
        comment.setPost(post);
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());

        commentRepo.save(comment);
    }

    public CommentItem getComment(Long commentId) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();

        return new CommentItem(
                comment.getId(),
                comment.getName(),
                comment.getEmail(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }

    public void editComment(Long commentId, CommentItem newComment) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        comment.setComment(newComment.text());
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());
        commentRepo.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}
