package com.deliguy.analytics_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliguy.analytics_service.model.BikerStats;

@Repository
public interface BikerStatsRepository extends JpaRepository<BikerStats, Long> {
    
    List<BikerStats> findTop10ByDeliveryCountGreaterThanOrderByDeliveryCountDesc(Long count);
}
