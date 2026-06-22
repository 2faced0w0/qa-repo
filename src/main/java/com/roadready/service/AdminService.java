package com.roadready.service;

import com.roadready.model.Admin;
import com.roadready.model.User;
import com.roadready.repository.AdminRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin =(Admin)  adminRepository.findByUserUsername((username))
                .orElseThrow(()-> new UsernameNotFoundException("Invalid Credentials"));
        return adminRepository.save(admin);
    }
}
