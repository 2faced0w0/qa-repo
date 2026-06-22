package com.roadready.service;

import com.roadready.dto.ReservationRequestDto;
import com.roadready.dto.ReservationResponseDto;
import com.roadready.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface ReservationService {
    ReservationResponseDto createReservation(ReservationRequestDto requestDTO);
    ReservationResponseDto cancelReservation(Integer reservationId);
    PaginatedResponse<ReservationResponseDto> getReservations(Integer customerId, Pageable pageable);
    PaginatedResponse<ReservationResponseDto> getReservationsByAgent(Integer agentId, Pageable pageable);
    ReservationResponseDto modifyReservation(Integer reservationId, ReservationRequestDto requestDto);
    ReservationResponseDto checkOut(Integer reservationId, String initialCondition);
    ReservationResponseDto checkIn(Integer reservationId, String finalCondition);
}
