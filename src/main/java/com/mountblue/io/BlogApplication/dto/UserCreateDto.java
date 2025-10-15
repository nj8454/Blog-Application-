package com.mountblue.io.BlogApplication.dto;

public record UserCreateDto(
        String name,
        String email,
        String password
) {
}
