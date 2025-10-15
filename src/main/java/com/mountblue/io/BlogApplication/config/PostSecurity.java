package com.mountblue.io.BlogApplication.config;

import com.mountblue.io.BlogApplication.repository.PostRepository;
import com.mountblue.io.BlogApplication.repository.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("postSecurity")
public class PostSecurity {
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    public PostSecurity(PostRepository postRepo, CommentRepository commentRepo) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
    }

    public boolean isOwner(Long postId, Authentication auth) {
        if (postId == null || auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) return false;
        return postRepo.findById(postId)
                .map(p -> p.getUser() != null && p.getUser().getId().equals(up.getId()))
                .orElse(false);
    }

    public boolean isOwnerByComment(Long commentId, Authentication auth) {
        if (commentId == null || auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) return false;
        return commentRepo.findById(commentId)
                .map(c -> c.getPost() != null
                        && c.getPost().getUser() != null
                        && c.getPost().getUser().getId().equals(up.getId()))
                .orElse(false);
    }
}
