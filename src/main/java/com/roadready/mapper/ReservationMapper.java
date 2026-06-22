package com.roadready.mapper;

import com.roadready.dto.ReservationRequestDto;
import com.roadready.dto.ReservationResponseDto;
import com.roadready.model.Customer;
import com.roadready.model.Reservation;
import com.roadready.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDto mapEntityToDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return new ReservationResponseDto(
                reservation.getReservationId(),
                reservation.getCustomer() != null ? reservation.getCustomer().getId() : null,
                reservation.getVehicle() != null ? reservation.getVehicle().getVehicleId() : null,
                reservation.getPickupTime(),
                reservation.getDropoffTime(),
                reservation.getOptionalExtras(),
                reservation.getBookingStatus(),
                reservation.getCreatedAt()
        );
    }

    public Reservation mapDtoToEntity(ReservationRequestDto dto, Customer customer, Vehicle vehicle) {
        if (dto == null) {
            return null;
        }
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setVehicle(vehicle);
        reservation.setPickupTime(dto.pickupTime());
        reservation.setDropoffTime(dto.dropoffTime());
        reservation.setOptionalExtras(dto.optionalExtras());
        return reservation;
    }
}
