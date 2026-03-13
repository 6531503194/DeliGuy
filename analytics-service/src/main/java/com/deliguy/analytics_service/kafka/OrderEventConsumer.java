package com.deliguy.analytics_service.kafka;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.deliguy.analytics_service.model.DailyStats;
import com.deliguy.analytics_service.repository.DailyStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final DailyStatsRepository dailyStatsRepository;

    @KafkaListener(topics = "order-created", groupId = "analytics-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Analytics: Order created - orderId={}, amount={}", event.orderId(), event.totalAmount());
        
        updateDailyStats(event.totalAmount() != null ? event.totalAmount() : 0.0);
    }

    private void updateDailyStats(double revenue) {
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
        
        stats.setOrderCount(stats.getOrderCount() + 1);
        stats.setTotalRevenue(stats.getTotalRevenue() + revenue);
        stats.setUpdatedAt(LocalDateTime.now());
        
        dailyStatsRepository.save(stats);
    }

    public record OrderCreatedEvent(
            Long orderId,
            String customerUsername,
            String customerPhone,
            String deliveryAddress,
            Double customerLat,
            Double customerLng,
            String restaurantId,
            String restaurantName,
            Double totalAmount,
            Object[] items
    ) {}
}
