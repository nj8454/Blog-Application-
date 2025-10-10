package com.mountblue.io.BlogApplication.repository;

import com.mountblue.io.BlogApplication.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Override
    Optional<Tag> findById(Long aLong);
    Optional<Tag> findByName(String name);
}
