package com.deliguy.analytics_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.deliguy.analytics_service.model.DailyStats;
import com.deliguy.analytics_service.model.RestaurantStats;
import com.deliguy.analytics_service.repository.DailyStatsRepository;
import com.deliguy.analytics_service.repository.RestaurantStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantEventConsumer {

    private final DailyStatsRepository dailyStatsRepository;
    private final RestaurantStatsRepository restaurantStatsRepository;

    @KafkaListener(topics = "restaurant-events", groupId = "analytics-service-group")
    public void consumeRestaurantEvent(RestaurantEvent event) {
        log.info("Analytics: Restaurant event - type={}, id={}", event.eventType(), event.restaurantId());
        
        switch (event.eventType()) {
            case "CREATED" -> handleRestaurantCreated(event);
            case "DELETED" -> handleRestaurantDeleted(event.restaurantId());
            default -> log.debug("Restaurant event type not tracked: {}", event.eventType());
        }
    }

    private void handleRestaurantCreated(RestaurantEvent event) {
        RestaurantStats stats = RestaurantStats.builder()
                .restaurantId(event.restaurantId())
                .restaurantName(event.name())
                .orderCount(0L)
                .totalRevenue(0.0)
                .acceptedCount(0L)
                .rejectedCount(0L)
                .build();
        
        restaurantStatsRepository.save(stats);
        
        updateActiveRestaurantsCount(1);
    }

    private void handleRestaurantDeleted(String restaurantId) {
        restaurantStatsRepository.deleteById(restaurantId);
        
        updateActiveRestaurantsCount(-1);
    }

    private void updateActiveRestaurantsCount(int delta) {
        LocalDate today = LocalDate.now();
        
        DailyStats stats = dailyStatsRepository.findByDate(today)
                .orElse(DailyStats.builder()
                        .date(today)
                        .orderCount(0L)
                        .totalRevenue(0.0)
                        .completedDeliveries(0L)
                        .activeRestaurants(0L)
                        .activeBikers(0L)
                        .build());
        
        long newCount = Math.max(0, stats.getActiveRestaurants() + delta);
        stats.setActiveRestaurants(newCount);
        stats.setUpdatedAt(LocalDateTime.now());
        
        dailyStatsRepository.save(stats);
    }

    public record RestaurantEvent(
            String eventType,
            String restaurantId,
            String name,
            String address,
            Double latitude,
            Double longitude,
            String phone,
            String email,
            String description,
            Boolean isActive
    ) {}
}
