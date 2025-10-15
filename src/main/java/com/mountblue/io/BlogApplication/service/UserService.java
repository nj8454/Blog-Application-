package com.mountblue.io.BlogApplication.service;

import com.mountblue.io.BlogApplication.dto.UserCreateDto;
import com.mountblue.io.BlogApplication.entities.User;
import com.mountblue.io.BlogApplication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void saveUser(UserCreateDto userCreateDto) {
        User newUser = new User();

        newUser.setName(userCreateDto.name());
        newUser.setEmail(userCreateDto.email());
        newUser.setPassword(userCreateDto.password());

        userRepo.save(newUser);

    }

    public List<User> findAuthors(String role) {
        return userRepo.findAllByRole(role);
    }
}
