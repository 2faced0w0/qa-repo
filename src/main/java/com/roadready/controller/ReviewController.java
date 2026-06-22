package com.roadready.controller;

import com.roadready.dto.ReviewRequestDto;
import com.roadready.dto.ReviewResponseDto;
import com.roadready.model.Review;
import com.roadready.model.Reservation;
import com.roadready.repository.ReviewRepository;
import com.roadready.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto requestDto) {
        Reservation reservation = reservationRepository.findById(requestDto.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        Review review = new Review();
        review.setReservation(reservation);
        review.setRating(requestDto.rating());
        review.setComments(requestDto.comments());

        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.ok(new ReviewResponseDto(
                savedReview.getReviewId(),
                savedReview.getReservation().getReservationId(),
                savedReview.getRating(),
                savedReview.getComments(),
                savedReview.getCreatedAt()
        ));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByVehicle(@PathVariable Integer vehicleId) {
        List<Review> reviews = reviewRepository.findByVehicleId(vehicleId);
        List<ReviewResponseDto> dtos = reviews.stream()
                .map(r -> new ReviewResponseDto(r.getReviewId(), r.getReservation().getReservationId(), r.getRating(), r.getComments(), r.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
