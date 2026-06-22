package com.roadready.service;

import com.roadready.dto.MaintenanceRecordDto;
import com.roadready.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface MaintenanceRecordService {
    PaginatedResponse<MaintenanceRecordDto> getMaintenanceRecords(Integer vehicleId, Integer agentId, Pageable pageable);
    MaintenanceRecordDto addMaintenanceRecord(com.roadready.dto.MaintenanceRequestDto dto);
}
