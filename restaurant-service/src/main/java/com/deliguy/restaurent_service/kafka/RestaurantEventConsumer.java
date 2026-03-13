package com.deliguy.restaurent_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.deliguy.restaurent_service.model.Restaurant;
import com.deliguy.restaurent_service.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantEventConsumer {

    private final RestaurantRepository restaurantRepository;

    @KafkaListener(topics = "restaurant-events", groupId = "restaurant-service-group")
    public void consumeRestaurantEvent(RestaurantEvent event) {
        log.info("Received restaurant event: type={}, id={}", event.eventType(), event.restaurantId());

        switch (event.eventType()) {
            case "CREATED", "UPDATED" -> upsertRestaurant(event);
            case "DELETED" -> deleteRestaurant(event.restaurantId());
            default -> log.warn("Unknown restaurant event type: {}", event.eventType());
        }
    }

    private void upsertRestaurant(RestaurantEvent event) {
        Restaurant restaurant = restaurantRepository.findById(event.restaurantId())
                .orElse(Restaurant.builder().id(event.restaurantId()).build());

        restaurant.setName(event.name());
        restaurant.setAddress(event.address());
        restaurant.setLatitude(event.latitude());
        restaurant.setLongitude(event.longitude());
        restaurant.setPhone(event.phone());
        restaurant.setEmail(event.email());
        restaurant.setDescription(event.description());
        restaurant.setIsActive(event.isActive());
        restaurant.setUpdatedAt(java.time.LocalDateTime.now());

        if (restaurant.getCreatedAt() == null) {
            restaurant.setCreatedAt(java.time.LocalDateTime.now());
        }

        restaurantRepository.save(restaurant);
        log.info("Restaurant synced: {}", event.restaurantId());
    }

    private void deleteRestaurant(String restaurantId) {
        if (restaurantRepository.existsById(restaurantId)) {
            restaurantRepository.deleteById(restaurantId);
            log.info("Restaurant deleted: {}", restaurantId);
        }
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
