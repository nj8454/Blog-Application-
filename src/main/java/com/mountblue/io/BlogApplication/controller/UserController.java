package com.mountblue.io.BlogApplication.controller;

import com.mountblue.io.BlogApplication.dto.UserCreateDto;
import com.mountblue.io.BlogApplication.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String longinForm() {

        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("register", new UserCreateDto("", "", ""));

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("register") UserCreateDto userCreateDto) {
        userService.saveUser(userCreateDto);

        return "login";
    }
}
