package com.roadready.repository;

import com.roadready.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    @Query("SELECT p FROM Payment p WHERE p.reservation.reservationId = :reservationId")
    List<Payment> findByReservation_ReservationId(@Param("reservationId") Integer reservationId);
    
}
