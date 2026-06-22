package com.roadready.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
        Integer paymentId,
        Integer reservationId,
        BigDecimal amount,
        String paymentMethod,
        String paymentStatus,
        LocalDateTime paymentDate
) {
}
