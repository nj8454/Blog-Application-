package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.CommentCreateDto;
import com.mountblue.io.BlogApplication.dto.CommentItemDto;
import com.mountblue.io.BlogApplication.entities.Comment;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.repository.CommentRepository;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CommentService {
    private CommentRepository commentRepo;
    private PostRepository postRepo;

    public CommentService(CommentRepository commentRepo, PostRepository postRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
    }

    @PreAuthorize("permitAll()")
    public CommentItemDto addComment(Long postId, CommentCreateDto dto) {
        Post post = postRepo.findById(postId).orElseThrow(NoSuchElementException::new);
        Comment comment = new Comment();
        comment.setName(dto.name());
        comment.setEmail(dto.email());
        comment.setComment(dto.text());
        comment.setPost(post);
        Comment saved = commentRepo.save(comment);
        return new CommentItemDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getComment(), saved.getCreatedAt());

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
    public CommentItemDto editComment(Long commentId, CommentCreateDto newComment) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        comment.setComment(newComment.text());
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());
        Comment saved = commentRepo.save(comment);
        return new CommentItemDto(saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getComment(),
                saved.getCreatedAt());
    }

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwnerByComment(#commentId, authentication)")
    public void deleteComment(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}
