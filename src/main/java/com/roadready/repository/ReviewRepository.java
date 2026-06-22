package com.roadready.repository;

import com.roadready.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.reservation.vehicle.vehicleId = :vehicleId")
    List<Review> findByVehicleId(@Param("vehicleId") Integer vehicleId);

    List<Review> findByRatingGreaterThan(Integer rating);
}
