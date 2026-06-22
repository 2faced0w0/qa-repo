package com.roadready.controller;

import com.roadready.dto.MaintenanceRecordDto;
import com.roadready.dto.PaginatedResponse;
import com.roadready.service.MaintenanceRecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.roadready.repository.RequestRepository;
import com.roadready.repository.VehicleRepository;
import com.roadready.repository.UserRepository;
import com.roadready.mapper.RequestMapper;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;
    private final RequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    public MaintenanceRecordController(MaintenanceRecordService maintenanceRecordService,
                                       RequestRepository requestRepository,
                                       VehicleRepository vehicleRepository,
                                       UserRepository userRepository,
                                       RequestMapper requestMapper) {
        this.maintenanceRecordService = maintenanceRecordService;
        this.requestRepository = requestRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT')")
    public ResponseEntity<PaginatedResponse<MaintenanceRecordDto>> getMaintenanceRecords(
            @RequestParam(required = false) Integer vehicleId,
            @RequestParam(required = false) Integer agentId,
            Pageable pageable) {
        
        PaginatedResponse<MaintenanceRecordDto> records = maintenanceRecordService.getMaintenanceRecords(vehicleId, agentId, pageable);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<?> addMaintenanceRequest(@RequestBody com.roadready.dto.MaintenanceRequestDto dto, Principal principal) {
        com.roadready.model.User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        com.roadready.model.Vehicle vehicle = vehicleRepository.findById(dto.vehicleId()).orElseThrow();
        
        com.roadready.model.Request request = requestMapper.mapToMaintenanceRequest(dto, user, vehicle);
        
        requestRepository.save(request);
        return ResponseEntity.ok("Maintenance request submitted to Admin.");
    }
}
