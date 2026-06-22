package com.roadready.controller;

import com.roadready.dto.ReportDto;
import com.roadready.repository.PaymentRepository;
import com.roadready.repository.UserRepository;
import com.roadready.repository.ReservationRepository;
import com.roadready.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.roadready.model.Payment;
import java.math.BigDecimal;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
public class ReportController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;

    @GetMapping
    public ResponseEntity<ReportDto> getDashboardReport() {
        BigDecimal totalRevenue = paymentRepository.findAll().stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalUsers = userRepository.count();
        long totalReservations = reservationRepository.count();
        long totalVehicles = vehicleRepository.count();
        return ResponseEntity.ok(new ReportDto(totalRevenue, totalUsers, totalReservations, totalVehicles));
    }
}
