package com.deliguy.restaurent_service.kafka;

import com.deliguy.restaurent_service.event.OrderStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaRestaurantEventProducer {

    private final KafkaTemplate<String, OrderStatusUpdatedEvent> kafkaTemplate;

    public void sendOrderStatusUpdated(Long orderId, String status, String restaurantId, String rejectionReason) {
        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(orderId, status, restaurantId, rejectionReason);
        kafkaTemplate.send("order-status-updated", orderId.toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send order-status-updated event for orderId={}: {}", 
                        orderId, ex.getMessage());
                } else {
                    log.info("Successfully sent order-status-updated event for orderId={}, status={}", 
                        orderId, status);
                }
            });
    }
}
