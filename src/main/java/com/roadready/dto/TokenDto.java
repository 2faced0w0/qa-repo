package com.roadready.dto;

public record TokenDto(
        String name,
        String email,
        String role,
        String token,
        Integer id
) {
}
