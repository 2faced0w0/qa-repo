package com.roadready.repository;

import com.roadready.model.Reservation;
import com.roadready.dto.ReservationResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("""
        SELECT new com.roadready.dto.ReservationResponseDto(
            r.reservationId,
            c.id,
            v.vehicleId,
            r.pickupTime,
            r.dropoffTime,
            r.optionalExtras,
            r.bookingStatus,
            r.createdAt
        )
        FROM Reservation r 
        LEFT JOIN r.customer c
        LEFT JOIN r.vehicle v
        WHERE c.id = :customerId
        """)
    Page<ReservationResponseDto> findReservationsByCustomerId(@Param("customerId") Integer customerId, Pageable pageable);

    @Query("""
        SELECT new com.roadready.dto.ReservationResponseDto(
            r.reservationId,
            c.id,
            v.vehicleId,
            r.pickupTime,
            r.dropoffTime,
            r.optionalExtras,
            r.bookingStatus,
            r.createdAt
        )
        FROM Reservation r 
        LEFT JOIN r.customer c
        LEFT JOIN r.vehicle v
        WHERE v.agent.id = :agentId
        """)
    Page<ReservationResponseDto> findReservationsByAgentId(@Param("agentId") Integer agentId, Pageable pageable);
}
