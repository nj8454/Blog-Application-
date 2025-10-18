package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.UserCreateDto;
import com.mountblue.io.BlogApplication.dto.UserDetailDto;
import com.mountblue.io.BlogApplication.entities.User;
import com.mountblue.io.BlogApplication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetailDto saveUser(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        User saved = userRepo.save(user);

        return new UserDetailDto(saved.getId(), saved.getName(), saved.getEmail(), null);
    }

    public List<User> findAuthors() {
        return userRepo.findAll();
    }
}
