package com.roadready.mapper;

import com.roadready.dto.VehicleDto;
import com.roadready.model.Vehicle;
import com.roadready.model.Brand;
import com.roadready.model.RentalAgent;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleDto mapEntityToDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new VehicleDto(
                vehicle.getVehicleId(),
                vehicle.getBrand() != null ? vehicle.getBrand().getBrandName() : null,
                vehicle.getAgent() != null ? vehicle.getAgent().getId() : null,
                vehicle.getAgent() != null ? vehicle.getAgent().getName() : null,
                vehicle.getModel(),
                vehicle.getSpecifications(),
                vehicle.getPricingPerDay(),
                vehicle.getAvailabilityStatus(),
                vehicle.getImageUrl(),
                vehicle.getLocation(),
                vehicle.getVehicleType(),
                vehicle.getSubType()
        );
    }

    public Vehicle mapDtoToEntity(VehicleDto dto, Brand brand, RentalAgent agent) {
        if (dto == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(dto.vehicleId());
        vehicle.setBrand(brand);
        vehicle.setAgent(agent);
        vehicle.setModel(dto.model());
        vehicle.setSpecifications(dto.specifications());
        vehicle.setPricingPerDay(dto.pricingPerDay());
        vehicle.setAvailabilityStatus(dto.availabilityStatus());
        vehicle.setImageUrl(dto.imageUrl());
        vehicle.setLocation(dto.location());
        vehicle.setVehicleType(dto.vehicleType());
        vehicle.setSubType(dto.subType());
        return vehicle;
    }
}
