package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.entities.Tag;
import com.mountblue.io.BlogApplication.repository.TagRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepo;
    private Tag tagDb;

    public Set<Tag> saveTags(String tagString){
        String[] tags = tagString.split(",");
        Set<Tag> tagSet = new HashSet<>();

        for(String name: tags){
            System.out.println("hiiiiiii" + name);
            Tag tag = tagRepo.findByName(name)
                    .orElseGet(() -> tagRepo.save(new Tag(name)));

            tagSet.add(tag);
        }
        return tagSet;
    }
}
