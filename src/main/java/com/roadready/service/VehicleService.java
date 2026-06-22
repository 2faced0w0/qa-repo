package com.roadready.service;

import com.roadready.dto.PaginatedResponse;
import com.roadready.dto.VehicleDto;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface VehicleService {
    PaginatedResponse<VehicleDto> searchVehicles(
            String model,
            BigDecimal maxPrice,
            String brandName,
            String location,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            String vehicleType,
            String subType,
            Pageable pageable);

    VehicleDto addVehicle(com.roadready.dto.VehicleRequestDto dto);
    void deleteVehicle(Integer id);
    PaginatedResponse<VehicleDto> getVehiclesByAgentId(Integer agentId, Pageable pageable);
}
