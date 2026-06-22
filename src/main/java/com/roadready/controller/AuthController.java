package com.roadready.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.roadready.dto.SignupRequestDto;
import com.roadready.dto.TokenDto;
import com.roadready.exception.CustomerNotFoundException;
import com.roadready.model.Customer;
import com.roadready.model.User;
import com.roadready.service.UserService;
import com.roadready.utility.JwtUtility;
import com.roadready.repository.CustomerRepository;
import com.roadready.repository.AdminRepository;
import com.roadready.repository.RentalAgentRepository;
import com.roadready.repository.RequestRepository;
import com.roadready.repository.UserRepository;
import com.roadready.enums.RequestStatus;
import com.roadready.enums.RequestType;
import com.roadready.model.Request;
import com.roadready.dto.ForgotPasswordDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private static final String INVALID_CREDENTIALS_MSG = "Invalid Credentials!!!";

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final RentalAgentRepository rentalAgentRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public TokenDto signup(@RequestBody SignupRequestDto dto) {
        Customer customer = userService.createCustomer(dto, passwordEncoder.encode(dto.password()));
        String token = jwtUtility.generateToken(customer.getUser().getUsername());
        return new TokenDto(customer.getName(), customer.getUser().getUsername(), "CUSTOMER", token, customer.getId());
    }

    @GetMapping("/login")
    public TokenDto login(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        String token = jwtUtility.generateToken(principal.getName());
        String role = user.getRole().toString();

        Integer id = null;
        String name = null;
        if (role.equals("CUSTOMER")) {
            Customer customer = customerRepository.findByUser(user).orElseThrow(
                    () -> new CustomerNotFoundException(INVALID_CREDENTIALS_MSG));
            id = customer.getId();
            name = customer.getName();
        } else if (role.equals("ADMIN")) {
            com.roadready.model.Admin admin = adminRepository.findByUser(user).orElseThrow(
                    () -> new CustomerNotFoundException(INVALID_CREDENTIALS_MSG));
            id = admin.getId();
            name = admin.getName();
        } else if (role.equals("AGENT")) {
            com.roadready.model.RentalAgent agent = rentalAgentRepository.findByUser(user).orElseThrow(
                    () -> new CustomerNotFoundException(INVALID_CREDENTIALS_MSG));
            id = agent.getId();
            name = agent.getName();
        }

        return new TokenDto(
                name,
                principal.getName(),
                role,
                token,
                id);
    }

    @PostMapping("/forgot-password")
    public org.springframework.http.ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        User user = userRepository.findByUsername(dto.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Request request = new Request();
        request.setRequestType(RequestType.PASSWORD_RESET);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestedBy(user);
        requestRepository.save(request);

        return org.springframework.http.ResponseEntity.ok("Password reset request submitted successfully.");
    }
}