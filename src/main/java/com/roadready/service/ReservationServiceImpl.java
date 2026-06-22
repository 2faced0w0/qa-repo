package com.roadready.service;

import com.roadready.dto.ReservationRequestDto;
import com.roadready.dto.ReservationResponseDto;
import com.roadready.enums.BookingStatus;
import com.roadready.mapper.ReservationMapper;
import com.roadready.model.Customer;
import com.roadready.model.Reservation;
import com.roadready.model.Vehicle;
import com.roadready.repository.CustomerRepository;
import com.roadready.repository.ReservationRepository;
import com.roadready.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final String RESERVATION_NOT_FOUND_MSG = "Reservation not found";

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ReservationMapper reservationMapper;
    private final com.roadready.repository.PaymentRepository paymentRepository;
    private final com.roadready.repository.PromotionRepository promotionRepository;

    @Override
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto) {
        Customer customer = customerRepository
                .findById(requestDto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Vehicle vehicle = vehicleRepository
                .findById(requestDto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (vehicle.getAvailabilityStatus() != com.roadready.enums.AvailabilityStatus.AVAILABLE) {
            throw new IllegalStateException("Vehicle is not available");
        }

        Reservation reservation = reservationMapper.mapDtoToEntity(requestDto, customer, vehicle);
        reservation.setBookingStatus(BookingStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);

        java.time.ZoneId zone = java.time.ZoneId.systemDefault();
        long days = java.time.temporal.ChronoUnit.DAYS.between(
            requestDto.pickupTime().atZone(zone), 
            requestDto.dropoffTime().atZone(zone)
        );
        if (days <= 0) days = 1;
        java.math.BigDecimal totalAmount = vehicle.getPricingPerDay().multiply(java.math.BigDecimal.valueOf(days));

        if (requestDto.promoCode() != null && !requestDto.promoCode().isBlank()) {
            com.roadready.model.Promotion promo = promotionRepository.findByPromoCode(requestDto.promoCode()).orElse(null);
            if (promo != null && promo.getValidTill().isAfter(java.time.Instant.now())) {
                java.math.BigDecimal discount = totalAmount.multiply(java.math.BigDecimal.valueOf(promo.getDiscountPercentage())).divide(java.math.BigDecimal.valueOf(100));
                totalAmount = totalAmount.subtract(discount);
            }
        }

        com.roadready.model.Payment payment = new com.roadready.model.Payment();
        payment.setReservation(savedReservation);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setPaymentStatus(com.roadready.enums.PaymentStatus.PENDING);
        payment.setPromoCode(requestDto.promoCode());
        paymentRepository.save(payment);
        
        return reservationMapper.mapEntityToDto(savedReservation);
    }

    @Override
    public ReservationResponseDto cancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_MSG));

        reservation.setBookingStatus(BookingStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);

        return reservationMapper.mapEntityToDto(updatedReservation);
    }

    @Override
    public com.roadready.dto.PaginatedResponse<ReservationResponseDto> getReservations(Integer customerId, org.springframework.data.domain.Pageable pageable) {
        Page<ReservationResponseDto> page = reservationRepository.findReservationsByCustomerId(customerId, pageable);
        return new com.roadready.dto.PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public com.roadready.dto.PaginatedResponse<ReservationResponseDto> getReservationsByAgent(Integer agentId, org.springframework.data.domain.Pageable pageable) {
        Page<ReservationResponseDto> page = reservationRepository.findReservationsByAgentId(agentId, pageable);
        return new com.roadready.dto.PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public ReservationResponseDto modifyReservation(Integer reservationId, ReservationRequestDto requestDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_MSG));
        
        Vehicle vehicle = vehicleRepository
                .findById(requestDto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Update details
        reservation.setVehicle(vehicle);
        reservation.setPickupTime(requestDto.pickupTime());
        reservation.setDropoffTime(requestDto.dropoffTime());
        reservation.setOptionalExtras(requestDto.optionalExtras());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservationMapper.mapEntityToDto(updatedReservation);
    }

    @Override
    public ReservationResponseDto checkOut(Integer reservationId, String initialCondition) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_MSG));
        reservation.setBookingStatus(BookingStatus.CHECKED_OUT);
        
        Vehicle vehicle = reservation.getVehicle();
        if (vehicle != null) {
            vehicle.setAvailabilityStatus(com.roadready.enums.AvailabilityStatus.RENTED);
            vehicleRepository.save(vehicle);
        }

        // Note: we can log the initialCondition or save it to a separate entity
        return reservationMapper.mapEntityToDto(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponseDto checkIn(Integer reservationId, String finalCondition) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_MSG));
        reservation.setBookingStatus(BookingStatus.COMPLETED); // Or CHECKED_IN, user said CHECKED_IN/CHECKED_OUT
        
        Vehicle vehicle = reservation.getVehicle();
        if (vehicle != null) {
            vehicle.setAvailabilityStatus(com.roadready.enums.AvailabilityStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        // Note: we can log finalCondition
        return reservationMapper.mapEntityToDto(reservationRepository.save(reservation));
    }
}
