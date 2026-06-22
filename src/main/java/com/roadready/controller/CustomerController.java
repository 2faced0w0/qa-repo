package com.roadready.controller;

import com.roadready.dto.CustomerProfileDto;
import com.roadready.dto.ProfileUpdateDto;
import com.roadready.model.Customer;
import com.roadready.model.User;
import com.roadready.repository.CustomerRepository;
import com.roadready.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/customers")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDto> getProfile(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return ResponseEntity.ok(new CustomerProfileDto(customer.getName(), customer.getPhoneNumber(), user.getUsername()));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(Principal principal, @RequestBody ProfileUpdateDto dto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (dto.name() != null && !dto.name().isEmpty()) {
            customer.setName(dto.name());
        }
        if (dto.phoneNumber() != null && !dto.phoneNumber().isEmpty()) {
            customer.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
            userRepository.save(user);
        }
        customerRepository.save(customer);

        return ResponseEntity.ok("Profile updated successfully");
    }
}
