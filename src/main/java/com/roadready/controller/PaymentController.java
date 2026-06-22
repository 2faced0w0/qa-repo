package com.roadready.controller;

import com.roadready.dto.PaymentDto;
import com.roadready.model.Payment;
import com.roadready.model.User;
import com.roadready.model.Customer;
import com.roadready.repository.PaymentRepository;
import com.roadready.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/my-history")
    public ResponseEntity<List<PaymentDto>> getMyPaymentHistory(@AuthenticationPrincipal User user) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getReservation().getCustomer().getId().equals(customer.getId()))
                .collect(Collectors.toList());

        List<PaymentDto> dtos = payments.stream()
                .map(p -> new PaymentDto(
                        p.getPaymentId(),
                        p.getReservation().getReservationId(),
                        p.getAmount(),
                        p.getPaymentMethod(),
                        p.getPaymentStatus().name(),
                        p.getPaymentDate()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
