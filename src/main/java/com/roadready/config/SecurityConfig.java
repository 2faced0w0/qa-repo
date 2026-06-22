package com.roadready.config;

import org.springframework.security.crypto.password.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import com.roadready.service.UserService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // To allow @PreAuthorize
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Allow login-signup auth endpoints for all
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/search").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/vehicles/brands").permitAll()// Allow
                        .requestMatchers("/api/reservations/**").hasAnyAuthority("CUSTOMER", "AGENT", "ADMIN")
                                                                                                                      // vehicle
                                                                                                                      // auth
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN") // Restricted to ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/vehicles/add").hasAnyAuthority("ADMIN", "AGENT") // Restricted to ADMIN or AGENT
                        .requestMatchers("/error").permitAll() // Expose actual errors instead of 403
                        .requestMatchers(("/{customerId}/past")).hasAnyAuthority("ADMIN", "AGENT")
                        .anyRequest().authenticated())
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        java.util.Map<String, PasswordEncoder> encoders = new java.util.HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(
                "bcrypt",
                encoders);

        // Keeping both Bcrypt and noop for now
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());

        return delegatingPasswordEncoder;
    }
}