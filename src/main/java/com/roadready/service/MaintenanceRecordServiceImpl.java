package com.roadready.service;

import com.roadready.dto.MaintenanceRecordDto;
import com.roadready.dto.PaginatedResponse;
import com.roadready.repository.MaintenanceRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final com.roadready.repository.VehicleRepository vehicleRepository;
    private final com.roadready.repository.RentalAgentRepository rentalAgentRepository;
    private final com.roadready.repository.RequestRepository requestRepository;

    public MaintenanceRecordServiceImpl(MaintenanceRecordRepository maintenanceRecordRepository, 
            com.roadready.repository.VehicleRepository vehicleRepository, 
            com.roadready.repository.RentalAgentRepository rentalAgentRepository,
            com.roadready.repository.RequestRepository requestRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalAgentRepository = rentalAgentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public PaginatedResponse<MaintenanceRecordDto> getMaintenanceRecords(Integer vehicleId, Integer agentId, Pageable pageable) {
        Page<MaintenanceRecordDto> page = maintenanceRecordRepository.findAllWithFilters(vehicleId, agentId, pageable);
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public MaintenanceRecordDto addMaintenanceRecord(com.roadready.dto.MaintenanceRequestDto dto) {
        com.roadready.model.Vehicle vehicle = vehicleRepository.findById(dto.vehicleId()).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        com.roadready.model.RentalAgent agent = rentalAgentRepository.findById(dto.agentId()).orElseThrow(() -> new RuntimeException("Agent not found"));

        com.roadready.model.Request request = new com.roadready.model.Request();
        request.setRequestType(com.roadready.enums.RequestType.MAINTENANCE);
        request.setStatus(com.roadready.enums.RequestStatus.PENDING);
        request.setRequestedBy(agent.getUser());
        request.setVehicle(vehicle);
        request.setDescription(dto.particulars());
        request.setDaysSinceLastService(dto.daysSinceLastService());

        requestRepository.save(request);

        // Return a dummy DTO since we changed this to a Request instead of a direct record.
        // The frontend doesn't use the returned data anyway.
        return new MaintenanceRecordDto(
                request.getId(),
                request.getDescription(),
                request.getDaysSinceLastService(),
                agent.getId(),
                agent.getName(),
                vehicle.getVehicleId(),
                vehicle.getModel()
        );
    }
}
