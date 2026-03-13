package com.deliguy.admin_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliguy.admin_service.dto.CreateRestaurantRequest;
import com.deliguy.admin_service.dto.RestaurantResponse;
import com.deliguy.admin_service.kafka.RestaurantEventProducer;
import com.deliguy.admin_service.model.Restaurant;
import com.deliguy.admin_service.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventProducer kafkaProducer;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .email(request.getEmail())
                .description(request.getDescription())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        
        kafkaProducer.sendRestaurantCreatedEvent(saved);
        
        log.info("Restaurant created with ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getRestaurantById(String id) {
        return restaurantRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));
    }

    public List<RestaurantResponse> getActiveRestaurants() {
        return restaurantRepository.findByIsActive(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantResponse updateRestaurant(String id, CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setDescription(request.getDescription());
        restaurant.setUpdatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);
        
        kafkaProducer.sendRestaurantUpdatedEvent(saved);
        
        log.info("Restaurant updated: {}", id);
        
        return toResponse(saved);
    }

    @Transactional
    public RestaurantResponse activateRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));

        restaurant.setIsActive(true);
        restaurant.setUpdatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);
        
        kafkaProducer.sendRestaurantUpdatedEvent(saved);
        
        log.info("Restaurant activated: {}", id);
        
        return toResponse(saved);
    }

    @Transactional
    public RestaurantResponse deactivateRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));

        restaurant.setIsActive(false);
        restaurant.setUpdatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);
        
        kafkaProducer.sendRestaurantUpdatedEvent(saved);
        
        log.info("Restaurant deactivated: {}", id);
        
        return toResponse(saved);
    }

    @Transactional
    public void deleteRestaurant(String id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found: " + id);
        }
        
        restaurantRepository.deleteById(id);
        
        kafkaProducer.sendRestaurantDeletedEvent(id);
        
        log.info("Restaurant deleted: {}", id);
    }

    public long countActiveRestaurants() {
        return restaurantRepository.countByIsActive(true);
    }

    public long countInactiveRestaurants() {
        return restaurantRepository.countByIsActive(false);
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .description(restaurant.getDescription())
                .isActive(restaurant.getIsActive())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }
}
