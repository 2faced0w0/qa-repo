package com.roadready.dto;

public record MaintenanceRequestDto(
    Integer vehicleId,
    Integer agentId,
    String particulars,
    Integer daysSinceLastService
) {}
