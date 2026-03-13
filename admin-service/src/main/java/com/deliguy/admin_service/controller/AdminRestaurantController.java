package com.deliguy.admin_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliguy.admin_service.dto.CreateRestaurantRequest;
import com.deliguy.admin_service.dto.RestaurantResponse;
import com.deliguy.admin_service.service.RestaurantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/restaurants")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        log.info("Creating new restaurant: {}", request.getName());
        RestaurantResponse response = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable String id) {
        RestaurantResponse restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RestaurantResponse>> getActiveRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getActiveRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody CreateRestaurantRequest request) {
        log.info("Updating restaurant: {}", id);
        RestaurantResponse response = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<RestaurantResponse> activateRestaurant(@PathVariable String id) {
        log.info("Activating restaurant: {}", id);
        RestaurantResponse response = restaurantService.activateRestaurant(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<RestaurantResponse> deactivateRestaurant(@PathVariable String id) {
        log.info("Deactivating restaurant: {}", id);
        RestaurantResponse response = restaurantService.deactivateRestaurant(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        log.info("Deleting restaurant: {}", id);
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
