package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    Page<Post> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"tags", "comments"})
    Optional<Post> findById(Long id);

    @Query("""
              SELECT DISTINCT p
              FROM Post p
              LEFT JOIN p.tags t
              WHERE
                (
                  :keyword IS NULL OR :keyword = '' OR
                  LOWER(p.author)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                  LOWER(p.title)   LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                  LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                  LOWER(t.name)    LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
                AND ( :hasAuthor = false OR LOWER(p.author) IN :authors )
                AND p.publishedAt >= COALESCE(:from, p.publishedAt)
                AND p.publishedAt <= COALESCE(:to,   p.publishedAt)
                AND ( :hasTags = false OR LOWER(t.name) IN :tagNames )
            """)
    Page<Post> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("hasAuthor") boolean hasAuthor,
            @Param("authors") List<String> authors,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("hasTags") boolean hasTags,
            @Param("tagNames") List<String> tagNames,
            Pageable pageable
    );

}

