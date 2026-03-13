package com.deliguy.analytics_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliguy.analytics_service.model.RestaurantStats;

@Repository
public interface RestaurantStatsRepository extends JpaRepository<RestaurantStats, String> {
    
    List<RestaurantStats> findTop10ByOrderCountGreaterThanOrderByOrderCountDesc(Long count);
}
