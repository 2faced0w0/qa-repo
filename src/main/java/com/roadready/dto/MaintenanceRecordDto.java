package com.roadready.dto;

public record MaintenanceRecordDto(
        Integer recordId,
        String particulars,
        Integer daysSinceLastService,
        Integer agentId,
        String agentName,
        Integer vehicleId,
        String vehicleModel
) {
}
