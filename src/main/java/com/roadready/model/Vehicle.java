package com.roadready.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "pricing_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricingPerDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private com.roadready.enums.AvailabilityStatus availabilityStatus = com.roadready.enums.AvailabilityStatus.AVAILABLE;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private String location;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "sub_type")
    private String subType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private RentalAgent agent;
}
