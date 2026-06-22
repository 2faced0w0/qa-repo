package com.roadready.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehicleSearchCriteria(
        String model,
        BigDecimal maxPrice,
        String brandName,
        String location,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        String vehicleType,
        String subType
) {}
