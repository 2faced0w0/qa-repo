package com.roadready.repository;

import com.roadready.dto.ReservationResponseDto;
import com.roadready.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalAgentRepository rentalAgentRepository;

    @Test
    void testFindReservationsByCustomerId() {
        User user = new User();
        user.setUsername("customer@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setName("Cust Test");
        customer.setPhoneNumber("1234");
        customerRepository.save(customer);

        Page<ReservationResponseDto> page = reservationRepository.findReservationsByCustomerId(customer.getId(), PageRequest.of(0, 10));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isEmpty(); // No reservations yet
    }

    @Test
    void testFindReservationsByAgentId() {
        User user = new User();
        user.setUsername("agent@test.com");
        user.setPassword("password");
        userRepository.save(user);

        RentalAgent agent = new RentalAgent();
        agent.setUser(user);
        agent.setName("Test Agency");
        agent.setPhoneNumber("1234567890");
        rentalAgentRepository.save(agent);

        Page<ReservationResponseDto> page = reservationRepository.findReservationsByAgentId(agent.getId(), PageRequest.of(0, 10));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isEmpty();
    }
}
