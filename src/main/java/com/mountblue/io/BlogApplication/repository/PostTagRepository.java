package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mountblue.io.BlogApplication.entities.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
