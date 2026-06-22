package com.roadready.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Integer reviewId,
        Integer reservationId,
        Integer rating,
        String comments,
        LocalDateTime createdAt
) {
}
