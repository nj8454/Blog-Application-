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

    public List<CommentModel.CommentItem> getCommentsList(Long postId) {
        List<Comment> comments = commentRepo.findAllByPostId(postId);

        List<CommentModel.CommentItem> commentItemList = new ArrayList<>();

        for(Comment tempComment: comments) {
            System.out.println(postId);
            System.out.println(tempComment.getComment());
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

    public CommentModel.CommentItem getComment(Long commnetId) {
        Comment comment = commentRepo.getReferenceById(commnetId);

        CommentModel.CommentItem tempComment = new CommentModel.CommentItem(
                comment.getId(),
                comment.getName(),
                comment.getEmail(),
                comment.getComment(),
                comment.getCreatedAt()
        );

        return tempComment;
    }

    public void editComment(Long postId, CommentModel.CommentItem newComment) {
        Post post = postRepo.findById(postId).orElseThrow();

        Comment comment = commentRepo.findById(postId).orElseThrow();

        comment.setComment(newComment.text());
        comment.setName(newComment.name());
        comment.setEmail(newComment.email());

        commentRepo.save(comment);

    }

    public void deleteComment(Long commentId){
        commentRepo.deleteById(commentId);
    }
}
