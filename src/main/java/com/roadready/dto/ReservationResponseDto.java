package com.roadready.dto;

import com.roadready.enums.BookingStatus;
import java.time.LocalDateTime;

public record ReservationResponseDto(
        Integer reservationId,
        Integer customerId,
        Integer vehicleId,
        LocalDateTime pickupTime,
        LocalDateTime dropoffTime,
        String optionalExtras,
        BookingStatus bookingStatus,
        LocalDateTime createdAt
) {
}
