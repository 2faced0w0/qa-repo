package com.roadready.repository;

import com.roadready.dto.MaintenanceRecordDto;
import com.roadready.model.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {

    @Query("""
        SELECT new com.roadready.dto.MaintenanceRecordDto(
            m.id,
            m.particulars,
            m.daysSinceLastService,
            a.id,
            a.name,
            v.vehicleId,
            v.model
        )
        FROM MaintenanceRecord m
        LEFT JOIN m.updatedByAgent a
        LEFT JOIN m.vehicle v
        WHERE (:vehicleId IS NULL OR v.vehicleId = :vehicleId)
        AND (:agentId IS NULL OR a.id = :agentId)
    """)
    Page<MaintenanceRecordDto> findAllWithFilters(@Param("vehicleId") Integer vehicleId, @Param("agentId") Integer agentId, Pageable pageable);
}
