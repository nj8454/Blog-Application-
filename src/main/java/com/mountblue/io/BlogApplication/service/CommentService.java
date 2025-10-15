package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.CommentCreateDto;
import com.mountblue.io.BlogApplication.dto.CommentItemDto;
import com.mountblue.io.BlogApplication.entities.Comment;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.repository.CommentRepository;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private CommentRepository commentRepo;
    private PostRepository postRepo;

    public CommentService(CommentRepository commentRepo, PostRepository postRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
    }

    @PreAuthorize("permitAll()")
    public void addComment(Long postId, CommentCreateDto newComment) {
        Post post = postRepo.findById(postId).orElseThrow();

        Comment comment = new Comment();

        comment.setName(newComment.name());
        comment.setEmail(newComment.email());
        comment.setComment(newComment.text());
        comment.setPost(post);

        commentRepo.save(comment);
    }

    public CommentItemDto getComment(Long commentId) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();

        return new CommentItemDto(
                comment.getId(),
                comment.getName(),
                comment.getEmail(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    public void editComment(Long commentId, CommentItemDto newComment) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        comment.setComment(newComment.text());
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());
        commentRepo.save(comment);
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    public void deleteComment(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}
