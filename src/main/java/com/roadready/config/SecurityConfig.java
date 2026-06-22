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

    private static final String ROLE_AGENT = "AGENT";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Allow login-signup auth endpoints for all
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/search").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/vehicles/brands").permitAll()// Allow
                        .requestMatchers(HttpMethod.GET, "/api/v1/promotions/active-banner").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/top").permitAll()
                        .requestMatchers("/api/v1/reservations/**").hasAnyAuthority(ROLE_CUSTOMER, ROLE_AGENT, ROLE_ADMIN)
                                                                                                                      // vehicle
                                                                                                                      // auth
                        .requestMatchers("/api/v1/admin/**").hasAuthority(ROLE_ADMIN) // Restricted to ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/add").hasAnyAuthority(ROLE_ADMIN, ROLE_AGENT) // Restricted to ADMIN or AGENT
                        .requestMatchers("/error").permitAll() // Expose actual errors instead of 403
                        .requestMatchers(("/{customerId}/past")).hasAnyAuthority(ROLE_ADMIN, ROLE_AGENT)
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

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}