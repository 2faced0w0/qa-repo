package com.roadready.controller;

import com.roadready.dto.ReservationRequestDto;
import com.roadready.dto.ReservationResponseDto;
import com.roadready.dto.PaginatedResponse;
import com.roadready.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.roadready.model.User;
import com.roadready.model.Customer;
import com.roadready.model.RentalAgent;
import com.roadready.repository.CustomerRepository;
import com.roadready.repository.RentalAgentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final CustomerRepository customerRepository;
    private final RentalAgentRepository rentalAgentRepository;

    @PostMapping("/add")
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto requestDto) {
        log.info("Creating reservation for customer ID: {} and vehicle ID: {}", requestDto.customerId(), requestDto.vehicleId());
        ReservationResponseDto response = reservationService.createReservation(requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(@PathVariable Integer reservationId) {
        log.info("Cancelling reservation with ID: {}", reservationId);
        ReservationResponseDto response = reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PaginatedResponse<ReservationResponseDto>> getMyPastReservations(@AuthenticationPrincipal User user, Pageable pageable) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found for the logged-in user"));
        PaginatedResponse<ReservationResponseDto> responses = reservationService.getReservations(customer.getId(), pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/agent")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<PaginatedResponse<ReservationResponseDto>> getAgentReservations(@AuthenticationPrincipal User user, Pageable pageable) {
        RentalAgent agent = rentalAgentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Agent not found for the logged-in user"));
        PaginatedResponse<ReservationResponseDto> responses = reservationService.getReservationsByAgent(agent.getId(), pageable);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> modifyReservation(@PathVariable Integer reservationId, @RequestBody ReservationRequestDto requestDto) {
        ReservationResponseDto response = reservationService.modifyReservation(reservationId, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationId}/check-out")
    public ResponseEntity<ReservationResponseDto> checkOut(@PathVariable Integer reservationId, @RequestParam(required = false) String initialCondition) {
        log.info("Checking out reservation ID: {}", reservationId);
        return ResponseEntity.ok(reservationService.checkOut(reservationId, initialCondition));
    }

    @PostMapping("/{reservationId}/check-in")
    public ResponseEntity<ReservationResponseDto> checkIn(@PathVariable Integer reservationId, @RequestParam(required = false) String finalCondition) {
        log.info("Checking in reservation ID: {}", reservationId);
        return ResponseEntity.ok(reservationService.checkIn(reservationId, finalCondition));
    }
}
