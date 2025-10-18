package com.mountblue.io.BlogApplication.dto;

public record UserDetailDto(
        Long id,
        String name,
        String email,
        String password
) {
}