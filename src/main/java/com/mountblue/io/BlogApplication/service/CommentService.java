package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.CommentModel;
import com.mountblue.io.BlogApplication.entities.Comment;
import com.mountblue.io.BlogApplication.entities.Post;
import com.mountblue.io.BlogApplication.repository.CommentRepository;
import com.mountblue.io.BlogApplication.repository.PostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@Service
public class CommentService {
    @Autowired
    private final CommentRepository commentRepo;
    private Comment comment = new Comment();
    @Autowired
    private PostRepository postRepo;

    public void addComment(Long postId, CommentModel.CommentCreateRequest newComment){
        Post post = postRepo.findById(postId).orElseThrow();

        Comment comment = new Comment();

        comment.setComment(newComment.text());
        comment.setPost(post);
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());

        commentRepo.save(comment);
    }

    public List<CommentModel.CommentItem> getComments(Long postId) {
        List<Comment> comments = commentRepo.findAllById(postId);
        List<CommentModel.CommentItem> commentItemList = new ArrayList<>();

        for(Comment tempComment: comments) {
            CommentModel.CommentItem commentDetail = new CommentModel.CommentItem(
                    tempComment.getId(),
                    tempComment.getName(),
                    tempComment.getEmail(),
                    tempComment.getComment(),
                    tempComment.getCreatedAt()
            );
            commentItemList.add(commentDetail);
        }

        return commentItemList;
    }
}
