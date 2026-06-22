package com.roadready.dto;

public record UserDto(
        int id,
        String username,
        String role,
        boolean active
) {
}
