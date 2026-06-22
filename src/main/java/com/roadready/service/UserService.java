package com.roadready.service;

import com.roadready.dto.SignupRequestDto;
import com.roadready.enums.Role;
import com.roadready.model.Admin;
import com.roadready.model.Customer;
import com.roadready.model.RentalAgent;
import com.roadready.model.User;
import com.roadready.repository.AdminRepository;
import com.roadready.repository.CustomerRepository;
import com.roadready.repository.RentalAgentRepository;
import com.roadready.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final RentalAgentRepository rentalAgentRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Invalid Credentials"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Customer createCustomer(SignupRequestDto dto, String encodedPassword) {
        if (userRepository.findByUsername(dto.email()).isPresent()) {
            throw new com.roadready.exception.UserAlreadyExistsException(
                    "Customer already exists with email: " + dto.email());
        }
        User user=new User();
        user.setRole(Role.CUSTOMER);
        user.setUsername(dto.email());
        user.setPassword(encodedPassword);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setName(dto.name());

        return customerRepository.save(customer);
    }

    public Admin createAdmin(SignupRequestDto dto, String encodedPassword) {
        if (userRepository.findByUsername(dto.email()).isPresent()) {
            throw new com.roadready.exception.UserAlreadyExistsException(
                    "Admin already exists with email: " + dto.email());
        }
        User user=new User();
        user.setRole(Role.ADMIN);
        user.setUsername(dto.email());
        user.setPassword(encodedPassword);
        save(user);

        Admin admin = new Admin();
        admin.setName(dto.name());
        admin.setPhoneNumber(dto.phoneNumber());
        admin.setUser(user);

        return adminRepository.save(admin);
    }

    public RentalAgent createRentalAgent(SignupRequestDto dto, String encodedPassword, Admin admin) {
        if (userRepository.findByUsername(dto.email()).isPresent()) {
            throw new com.roadready.exception.UserAlreadyExistsException(
                    "Agent already exists with email: " + dto.email());
        }
        User user=new User();
        user.setRole(Role.AGENT);
        user.setUsername(dto.email());
        user.setPassword(encodedPassword);
        save(user);

        RentalAgent agent = new RentalAgent();
        agent.setName(dto.name());
        agent.setPhoneNumber(dto.phoneNumber());
        agent.setAdmin(admin);

        return rentalAgentRepository.save(agent);
    }
}