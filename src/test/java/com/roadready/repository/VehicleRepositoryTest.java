package com.roadready.repository;

import com.roadready.dto.VehicleDto;
import com.roadready.enums.AvailabilityStatus;
import com.roadready.model.RentalAgent;
import com.roadready.model.User;
import com.roadready.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalAgentRepository rentalAgentRepository;

    @Autowired
    private com.roadready.repository.BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByAvailabilityStatus() {
        Page<Vehicle> page = vehicleRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE, PageRequest.of(0, 10));
        assertThat(page).isNotNull();
    }

    @Test
    void testFindByAgentId() {
        User user = new User();
        user.setUsername("agent@test.com");
        user.setPassword("password");
        userRepository.save(user);

        RentalAgent agent = new RentalAgent();
        agent.setUser(user);
        agent.setName("Test Agency");
        agent.setPhoneNumber("1234567890");
        rentalAgentRepository.save(agent);

        com.roadready.model.Brand brand = new com.roadready.model.Brand();
        brand.setBrandName("Test Brand");

        brandRepository.save(brand);

        Vehicle vehicle = new Vehicle();
        vehicle.setAgent(agent);
        vehicle.setBrand(brand);
        vehicle.setModel("Test Model");
        vehicle.setLocation("Test Location");
        vehicle.setPricingPerDay(BigDecimal.valueOf(100));
        vehicle.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        Page<Vehicle> found = vehicleRepository.findByAgent_Id(agent.getId(), PageRequest.of(0, 10));
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getModel()).isEqualTo("Test Model");
    }

    @Test
    void testSearchVehicles() {
        Page<VehicleDto> dtos = vehicleRepository.searchVehicles(
                "Model", null, null, null, null, null, null, null, PageRequest.of(0, 10));
        assertThat(dtos).isNotNull();
    }
}
