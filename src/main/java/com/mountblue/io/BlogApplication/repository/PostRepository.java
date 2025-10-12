package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = {"tags", "comments"})
    Optional<Post> findById(Long id);


    List<Post> findDistinctByAuthorContainingOrTitleContainingOrContentContainingOrTags_NameContaining
                    (String postAuthor
                    , String postTitle
                    , String postContent
                    , String postTag);

}

