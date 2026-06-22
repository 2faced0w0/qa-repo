package com.roadready.repository;

import com.roadready.model.Vehicle;
import java.math.BigDecimal;
import com.roadready.dto.VehicleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    Page<Vehicle> findByAvailabilityStatus(com.roadready.enums.AvailabilityStatus availabilityStatus, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.agent.id = :agentId")
    Page<Vehicle> findByAgent_Id(@Param("agentId") Integer agentId, Pageable pageable);

    @Query("""
            SELECT new com.roadready.dto.VehicleDto(
                v.vehicleId, 
                b.brandName, 
                a.id, 
                a.name, 
                v.model, 
                v.specifications, 
                v.pricingPerDay,
                v.availabilityStatus, 
                v.imageUrl, 
                v.location,
                v.vehicleType,
                v.subType
            ) 
            FROM Vehicle v
            LEFT JOIN v.brand b
            LEFT JOIN v.agent a
            WHERE (:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) 
            AND (:maxPrice IS NULL OR v.pricingPerDay <= :maxPrice) 
            AND (:brandName IS NULL OR LOWER(b.brandName) LIKE LOWER(CONCAT('%', :brandName, '%'))) 
            AND (:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:vehicleType IS NULL OR LOWER(v.vehicleType) LIKE LOWER(CONCAT('%', :vehicleType, '%')))
            AND (:subType IS NULL OR LOWER(v.subType) LIKE LOWER(CONCAT('%', :subType, '%')))
            AND (CAST(:startDate AS java.time.LocalDateTime) IS NULL OR CAST(:endDate AS java.time.LocalDateTime) IS NULL OR NOT EXISTS (
                SELECT r FROM Reservation r 
                WHERE r.vehicle = v 
                AND r.bookingStatus IN ('CONFIRMED', 'CHECKED_OUT')
                AND r.dropoffTime > :startDate 
                AND r.pickupTime < :endDate
            ))""")
    Page<VehicleDto> searchVehicles(@Param("model") String model,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("brandName") String brandName,
                                 @Param("location") String location,
                                 @Param("startDate") java.time.LocalDateTime startDate,
                                 @Param("endDate") java.time.LocalDateTime endDate,
                                 @Param("vehicleType") String vehicleType,
                                 @Param("subType") String subType,
                                 Pageable pageable
    );
}
