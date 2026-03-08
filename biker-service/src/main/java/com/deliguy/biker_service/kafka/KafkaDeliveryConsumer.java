package com.deliguy.biker_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.deliguy.biker_service.service.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDeliveryConsumer {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "delivery-assigned", groupId = "biker-service-group", 
        containerFactory = "kafkaListenerContainerFactory")
    public void consumeDeliveryAssigned(DeliveryAssignedEvent event) {
        log.info("Received delivery assignment: orderId={}, restaurant={}", 
            event.orderId(), event.restaurantName());

        try {
            var assignment = deliveryService.assignDelivery(
                event.orderId(),
                event.restaurantName(),
                event.restaurantAddress(),
                event.restaurantLat(),
                event.restaurantLng(),
                event.customerAddress(),
                event.customerLat(),
                event.customerLng()
            );

            if (assignment == null) {
                log.error("Failed to assign delivery for order {}", event.orderId());
            } else {
                log.info("Successfully assigned delivery: orderId={}, bikerId={}", 
                    event.orderId(), assignment.getBikerId());
            }
        } catch (Exception e) {
            log.error("Error processing delivery assignment for order {}: {}", 
                event.orderId(), e.getMessage());
        }
    }
}
