package com.roadready.mapper;

import com.roadready.dto.MaintenanceRequestDto;
import com.roadready.enums.RequestStatus;
import com.roadready.enums.RequestType;
import com.roadready.model.Request;
import com.roadready.model.User;
import com.roadready.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public Request mapToMaintenanceRequest(MaintenanceRequestDto dto, User user, Vehicle vehicle) {
        if (dto == null) {
            return null;
        }
        Request request = new Request();
        request.setRequestType(RequestType.MAINTENANCE);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestedBy(user);
        request.setVehicle(vehicle);
        request.setDescription(dto.particulars());
        request.setDaysSinceLastService(dto.daysSinceLastService());
        return request;
    }
}
