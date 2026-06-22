package com.roadready.repository;

import com.roadready.enums.RequestStatus;
import com.roadready.enums.RequestType;
import com.roadready.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByStatus(RequestStatus status);
    List<Request> findByRequestTypeAndStatus(RequestType requestType, RequestStatus status);
}
