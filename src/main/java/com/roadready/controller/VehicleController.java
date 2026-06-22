package com.roadready.controller;

import com.roadready.dto.BrandDto;
import com.roadready.dto.VehicleDto;
import java.math.BigDecimal;
import java.util.List;

import com.roadready.dto.PaginatedResponse;
import com.roadready.model.RentalAgent;
import com.roadready.model.User;
import com.roadready.repository.BrandRepository;
import com.roadready.repository.RentalAgentRepository;
import com.roadready.repository.VehicleRepository;
import com.roadready.service.VehicleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private static final String NOT_FOUND_MSG = "Not found";

    private final VehicleService vehicleService;
    private final BrandRepository brandRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAgentRepository rentalAgentRepository;

    public VehicleController(VehicleService vehicleService, BrandRepository brandRepository, VehicleRepository vehicleRepository, RentalAgentRepository rentalAgentRepository) {
        this.vehicleService = vehicleService;
        this.brandRepository = brandRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalAgentRepository = rentalAgentRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<VehicleDto>> searchVehicles(
            @ModelAttribute com.roadready.dto.VehicleSearchCriteria criteria,
            Pageable pageable) {
        log.info("Searching vehicles with model: {}, brand: {}, location: {}", criteria.model(), criteria.brandName(), criteria.location());
        PaginatedResponse<VehicleDto> vehicles = vehicleService.searchVehicles(criteria, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/agent")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<PaginatedResponse<VehicleDto>> getAgentVehicles(@AuthenticationPrincipal User user, Pageable pageable) {
        RentalAgent agent = rentalAgentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Agent not found for the logged-in user"));
        PaginatedResponse<VehicleDto> response = vehicleService.getVehiclesByAgentId(agent.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<VehicleDto> addVehicle(@RequestBody com.roadready.dto.VehicleRequestDto dto) {
        log.info("Adding new vehicle: {} {}", dto.brandId(), dto.model());
        VehicleDto createdVehicle = vehicleService.addVehicle(dto);
        return ResponseEntity.ok(createdVehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable Integer id, @RequestBody com.roadready.dto.VehicleRequestDto dto) {
        com.roadready.model.Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG));
        vehicle.setModel(dto.model());
        vehicle.setPricingPerDay(dto.pricingPerDay());
        vehicle.setLocation(dto.location());
        vehicle.setVehicleType(dto.vehicleType());
        vehicle.setSubType(dto.subType());
        if (dto.imageUrl() != null && !dto.imageUrl().isEmpty()) {
            vehicle.setImageUrl(dto.imageUrl());
        }
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok("Vehicle updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Integer id) {
        log.info("Deleting vehicle with ID: {}", id);
        vehicleRepository.deleteById(id);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Integer id, @RequestParam com.roadready.enums.AvailabilityStatus status) {
        com.roadready.model.Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG));
        vehicle.setAvailabilityStatus(status);
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok("Vehicle status updated to " + status);
    }

    @PutMapping("/{id}/finish-maintenance")
    public ResponseEntity<String> finishMaintenance(@PathVariable Integer id) {
        com.roadready.model.Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG));
        vehicle.setAvailabilityStatus(com.roadready.enums.AvailabilityStatus.AVAILABLE);
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok("Vehicle marked as available and maintenance finished.");
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<BrandDto> brands = brandRepository.findAll()
                .stream()
                .map(brand -> new BrandDto(brand.getBrandId(), brand.getBrandName()))
                .toList();
        return ResponseEntity.ok(brands);
    }
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }
        try {
            // Determine the absolute path to the UI assets directory
            String projectDir = System.getProperty("user.dir"); // This is typically the backend directory
            File uiAssetsDir = new File(projectDir, "../roadready-ui/src/assets/vehicle-images");
            if (!uiAssetsDir.exists()) {
                uiAssetsDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uiAssetsDir.getAbsolutePath(), fileName);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the relative URL for the frontend
            String relativeUrl = "/src/assets/vehicle-images/" + fileName;
            return ResponseEntity.ok(relativeUrl);
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Could not upload the file: " + e.getMessage());
        }
    }
}
