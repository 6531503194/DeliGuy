package com.deliguy.order_service.repository;

import com.deliguy.order_service.model.Biker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BikerRepository extends JpaRepository<Biker, Long> {
}