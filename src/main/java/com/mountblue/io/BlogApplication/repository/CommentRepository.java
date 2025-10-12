package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllById(Long postId);

    List<Comment> findAllByPostId(Long postId);
}
