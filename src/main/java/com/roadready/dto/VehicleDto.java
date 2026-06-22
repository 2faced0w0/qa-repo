package com.roadready.dto;

import java.math.BigDecimal;

public record VehicleDto(
        Integer vehicleId,
        String brandName,
        Integer agentId,
        String agentName,
        String model,
        String specifications,
        BigDecimal pricingPerDay,
        com.roadready.enums.AvailabilityStatus availabilityStatus,
        String imageUrl,
        String location,
        String vehicleType,
        String subType
) {
}
