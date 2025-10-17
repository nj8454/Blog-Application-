package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.UserCreateDto;
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

    public void saveUser(UserCreateDto userCreateDto) {
        User newUser = new User();

        newUser.setName(userCreateDto.name());
        newUser.setEmail(userCreateDto.email());
        newUser.setPassword(passwordEncoder.encode(userCreateDto.password()));

        userRepo.save(newUser);

    }

    public List<User> findAuthors() {
        return userRepo.findAll();
    }
}
