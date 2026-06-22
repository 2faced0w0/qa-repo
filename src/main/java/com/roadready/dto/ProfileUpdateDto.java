package com.roadready.dto;

public record ProfileUpdateDto(
        String name,
        String phoneNumber,
        String password
) {
}
