package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.TagRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepo;

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

    public List<String> getTags() {
        List<Tag> tags = tagRepo.findAll();
        List<String> tagsName = new ArrayList<>();
        for (Tag tag : tags) {
            String name = tag.getName();
            tagsName.add(name);
        }

        return tagsName;
    }
}
