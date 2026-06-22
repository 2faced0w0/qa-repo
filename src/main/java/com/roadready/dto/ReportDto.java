package com.roadready.dto;
import java.math.BigDecimal;
public record ReportDto(BigDecimal totalRevenue, long totalUsers, long totalReservations, long totalVehicles) {}
