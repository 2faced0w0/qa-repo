package com.roadready.dto;

public record ReviewRequestDto(
        Integer reservationId,
        Integer rating,
        String comments
) {
}
