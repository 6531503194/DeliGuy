package com.deliguy.analytics_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliguy.analytics_service.model.DailyStats;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
    
    Optional<DailyStats> findByDate(LocalDate date);
}
