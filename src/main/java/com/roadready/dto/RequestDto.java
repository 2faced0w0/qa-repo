package com.roadready.dto;

import java.time.LocalDateTime;

public record RequestDto(
        Integer id,
        String requestType,
        String status,
        String requestedByEmail,
        String requestedByRole,
        Integer vehicleId,
        String vehicleBrand,
        String vehicleModel,
        String description,
        Integer daysSinceLastService,
        LocalDateTime createdAt
) {
}
