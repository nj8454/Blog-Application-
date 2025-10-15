package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
