package com.deliguy.admin_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.deliguy.admin_service.model.Restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";

    public void sendRestaurantCreatedEvent(Restaurant restaurant) {
        RestaurantEvent event = new RestaurantEvent(
                "CREATED",
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getPhone(),
                restaurant.getEmail(),
                restaurant.getDescription(),
                restaurant.getIsActive()
        );
        
        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send restaurant-created event: {}", ex.getMessage());
                    } else {
                        log.info("Sent restaurant-created event for: {}", restaurant.getId());
                    }
                });
    }

    public void sendRestaurantUpdatedEvent(Restaurant restaurant) {
        RestaurantEvent event = new RestaurantEvent(
                "UPDATED",
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getPhone(),
                restaurant.getEmail(),
                restaurant.getDescription(),
                restaurant.getIsActive()
        );
        
        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurant.getId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send restaurant-updated event: {}", ex.getMessage());
                    } else {
                        log.info("Sent restaurant-updated event for: {}", restaurant.getId());
                    }
                });
    }

    public void sendRestaurantDeletedEvent(String restaurantId) {
        RestaurantEvent event = new RestaurantEvent(
                "DELETED",
                restaurantId,
                null, null, null, null, null, null, null, null
        );
        
        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, restaurantId, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send restaurant-deleted event: {}", ex.getMessage());
                    } else {
                        log.info("Sent restaurant-deleted event for: {}", restaurantId);
                    }
                });
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
