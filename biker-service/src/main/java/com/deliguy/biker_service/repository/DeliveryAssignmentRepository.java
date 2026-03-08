package com.deliguy.biker_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliguy.biker_service.model.DeliveryAssignment;
import com.deliguy.biker_service.model.DeliveryStatus;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    List<DeliveryAssignment> findByBikerIdAndStatus(Long bikerId, DeliveryStatus status);

    List<DeliveryAssignment> findByStatus(DeliveryStatus status);

    List<DeliveryAssignment> findByBikerId(Long bikerId);
}
