package com.deliguy.analytics_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.deliguy.analytics_service.model.BikerStats;
import com.deliguy.analytics_service.model.DailyStats;
import com.deliguy.analytics_service.repository.BikerStatsRepository;
import com.deliguy.analytics_service.repository.DailyStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final DailyStatsRepository dailyStatsRepository;
    private final BikerStatsRepository bikerStatsRepository;

    @KafkaListener(topics = "delivery-status-changed", groupId = "analytics-service-group")
    public void consumeDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
        log.info("Analytics: Delivery status changed - orderId={}, status={}", 
                event.orderId(), event.status());
        
        if ("COMPLETED".equals(event.status())) {
            updateCompletedDelivery(event.bikerId(), event.deliveryFee());
        }
    }

    private void updateCompletedDelivery(Long bikerId, Double deliveryFee) {
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
        
        stats.setCompletedDeliveries(stats.getCompletedDeliveries() + 1);
        stats.setTotalRevenue(stats.getTotalRevenue() + (deliveryFee != null ? deliveryFee : 0.0));
        stats.setUpdatedAt(LocalDateTime.now());
        
        dailyStatsRepository.save(stats);
        
        if (bikerId != null) {
            BikerStats bikerStats = bikerStatsRepository.findById(bikerId)
                    .orElse(BikerStats.builder()
                            .bikerId(bikerId)
                            .deliveryCount(0L)
                            .totalEarnings(0.0)
                            .build());
            
            bikerStats.setDeliveryCount(bikerStats.getDeliveryCount() + 1);
            bikerStats.setTotalEarnings(bikerStats.getTotalEarnings() + (deliveryFee != null ? deliveryFee : 0.0));
            
            bikerStatsRepository.save(bikerStats);
        }
    }

    public record DeliveryStatusChangedEvent(
            Long orderId,
            String status,
            Long bikerId,
            // Object[] details
            Double deliveryFee
    ) {}
}
