package com.roadready.service;

import com.roadready.dto.PaginatedResponse;
import com.roadready.dto.VehicleDto;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import com.roadready.dto.VehicleSearchCriteria;

public interface VehicleService {
    PaginatedResponse<VehicleDto> searchVehicles(VehicleSearchCriteria criteria, Pageable pageable);

    VehicleDto addVehicle(com.roadready.dto.VehicleRequestDto dto);
    void deleteVehicle(Integer id);
    PaginatedResponse<VehicleDto> getVehiclesByAgentId(Integer agentId, Pageable pageable);
}
