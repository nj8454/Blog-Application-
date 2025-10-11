package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.TagRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepo;
    private Tag tagDb;

    public Set<Tag> saveTags(String tagString) {
        String[] parts = tagString.split(",");
        Set<Tag> tagSet = new LinkedHashSet<>();
        for (String raw : parts) {
            String name = raw.trim();
            if (name.isEmpty()) continue;
            Tag tag = tagRepo.findByName(name)
                    .orElseGet(() -> tagRepo.save(new Tag(name)));
            tagSet.add(tag);
        }
        return tagSet;
    }
}
