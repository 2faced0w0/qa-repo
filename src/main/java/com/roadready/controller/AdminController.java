package com.roadready.controller;

import com.roadready.dto.SignupRequestDto;
import com.roadready.model.Admin;
import com.roadready.service.AdminService;
import com.roadready.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import com.roadready.dto.RequestDto;
import com.roadready.dto.PasswordResetResolveDto;
import com.roadready.enums.RequestStatus;
import com.roadready.model.Request;
import com.roadready.model.User;
import com.roadready.model.Vehicle;
import com.roadready.repository.RequestRepository;
import com.roadready.repository.UserRepository;
import com.roadready.repository.VehicleRepository;
import com.roadready.repository.MaintenanceRecordRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final com.roadready.repository.RentalAgentRepository rentalAgentRepository;

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody SignupRequestDto dto) {
        userService.createAdmin(dto, passwordEncoder.encode(dto.password()));
        return ResponseEntity.ok("Admin created successfully.");
    }

    @PostMapping("/create-agent")
    public ResponseEntity<String> createAgent(@RequestBody SignupRequestDto dto, Principal principal) {
        Admin admin = (Admin) adminService.loadUserByUsername(principal.getName());
        userService.createRentalAgent(dto, passwordEncoder.encode(dto.password()), admin);
        return ResponseEntity.ok("Rental Agent created successfully.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<com.roadready.dto.UserResponseDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<com.roadready.dto.UserResponseDto> dtos = users.stream().map(u -> new com.roadready.dto.UserResponseDto(
                u.getId(),
                u.getUsername(),
                u.getRole().toString()
        )).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RequestDto>> getPendingRequests() {
        List<Request> pendingRequests = requestRepository.findByStatus(RequestStatus.PENDING);
        List<RequestDto> dtos = pendingRequests.stream().map(req -> new RequestDto(
                req.getId(),
                req.getRequestType().toString(),
                req.getStatus().toString(),
                req.getRequestedBy().getUsername(),
                req.getRequestedBy().getRole().toString(),
                req.getVehicle() != null ? req.getVehicle().getVehicleId() : null,
                req.getVehicle() != null ? req.getVehicle().getBrand().getBrandName() : null,
                req.getVehicle() != null ? req.getVehicle().getModel() : null,
                req.getDescription(),
                req.getDaysSinceLastService(),
                req.getCreatedAt()
        )).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/requests/{requestId}/resolve-password")
    public ResponseEntity<String> resolvePasswordRequest(@PathVariable Integer requestId, @RequestBody PasswordResetResolveDto dto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        User user = request.getRequestedBy();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        request.setStatus(RequestStatus.RESOLVED);
        request.setResolvedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        requestRepository.save(request);

        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/requests/{requestId}/resolve-maintenance")
    public ResponseEntity<String> resolveMaintenanceRequest(@PathVariable Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        Vehicle vehicle = request.getVehicle();
        if (vehicle != null) {
            vehicle.setAvailabilityStatus(com.roadready.enums.AvailabilityStatus.MAINTENANCE);
            vehicleRepository.save(vehicle);
        }

        com.roadready.model.RentalAgent agent = rentalAgentRepository.findByUser(request.getRequestedBy())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        com.roadready.model.MaintenanceRecord maintenanceRecord = new com.roadready.model.MaintenanceRecord();
        maintenanceRecord.setVehicle(vehicle);
        maintenanceRecord.setParticulars(request.getDescription());
        maintenanceRecord.setDaysSinceLastService(request.getDaysSinceLastService() != null ? request.getDaysSinceLastService() : 0);
        maintenanceRecord.setUpdatedByAgent(agent);
        maintenanceRecordRepository.save(maintenanceRecord);

        requestRepository.delete(request);

        return ResponseEntity.ok("Vehicle added for maintenance.");
    }
}
