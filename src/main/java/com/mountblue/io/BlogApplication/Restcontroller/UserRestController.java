package com.mountblue.io.BlogApplication.Restcontroller;

import com.mountblue.io.BlogApplication.dto.UserCreateDto;
import com.mountblue.io.BlogApplication.dto.UserDetailDto;
import com.mountblue.io.BlogApplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequestMapping("/api/auth")
public class UserRestController {
    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDetailDto> registerUser(@RequestBody UserCreateDto userCreateDto) {
        UserDetailDto created = userService.saveUser(userCreateDto);

        return ResponseEntity.created(URI.create("/users/" + created.id())).body(created);
    }
}
