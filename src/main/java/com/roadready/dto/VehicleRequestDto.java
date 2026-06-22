package com.roadready.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VehicleRequestDto(
        @NotNull(message = "Brand-less car? Damn!")
        Integer brandId,
        @NotNull(message = "What car doesn't have a model ?")
        String model,
        String specifications,
        @NotNull(message = "Hey bruh, we don't do freebies, go ask your manager how much to charge per day")
        BigDecimal pricingPerDay,
        String imageUrl,
        String location,
        @NotNull(message = "Go get a agent id first you un-authenticated baboon")
        Integer agentId,
        String vehicleType,
        String subType
) {
}
