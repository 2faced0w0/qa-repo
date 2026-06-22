package com.roadready.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequestDto(
        Integer customerId,
        Integer vehicleId,
        LocalDateTime pickupTime,
        @NotNull(message = "Do you really want to keep the car forever ? Go buy it then.")
        LocalDateTime dropoffTime,
        String optionalExtras,
        String promoCode
) {
}
