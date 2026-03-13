package com.deliguy.analytics_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Long orderCount;

    private Double totalRevenue;

    private Long completedDeliveries;

    private Long activeRestaurants;

    private Long activeBikers;

    private LocalDateTime updatedAt;
}
