package com.roadready.controller;

import com.roadready.dto.MaintenanceRecordDto;
import com.roadready.dto.PaginatedResponse;
import com.roadready.service.MaintenanceRecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    public MaintenanceRecordController(MaintenanceRecordService maintenanceRecordService) {
        this.maintenanceRecordService = maintenanceRecordService;
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
    public ResponseEntity<MaintenanceRecordDto> addMaintenanceRequest(@RequestBody com.roadready.dto.MaintenanceRequestDto dto) {
        MaintenanceRecordDto record = maintenanceRecordService.addMaintenanceRecord(dto);
        return ResponseEntity.ok(record);
    }
}
