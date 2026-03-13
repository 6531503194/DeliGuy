package com.deliguy.biker_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliguy.biker_service.client.RouterClient;
import com.deliguy.biker_service.dto.RouteResponse;
import com.deliguy.biker_service.kafka.DeliveryStatusChangedEvent;
import com.deliguy.biker_service.model.BikerLocation;
import com.deliguy.biker_service.model.DeliveryAssignment;
import com.deliguy.biker_service.model.DeliveryStatus;
import com.deliguy.biker_service.repository.DeliveryAssignmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryAssignmentRepository repository;
    private final BikerLocationService bikerLocationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RouterClient routerClient;

    private static final int MAX_REJECTIONS = 3;
    private static final double BASE_DELIVERY_FEE = 2.0;
    private static final double FEE_PER_KM = 1.0;

    @Transactional
    public DeliveryAssignment assignDelivery(
            Long orderId,
            String restaurantName,
            String restaurantAddress,
            Double restaurantLat,
            Double restaurantLng,
            String customerAddress,
            Double customerLat,
            Double customerLng
    ) {
        if (repository.existsById(orderId)) {
            log.warn("Delivery assignment for order {} already exists", orderId);
            return repository.findById(orderId).orElse(null);
        }

        Long nearestBikerId = bikerLocationService.findNearestAvailableBikerId(restaurantLat, restaurantLng);
        
        if (nearestBikerId == null) {
            log.warn("No available biker found for order {}", orderId);
            return null;
        }

        BikerLocation bikerLocation = bikerLocationService.getLocation(nearestBikerId);
        double distanceToRestaurant = calculateDistanceWithRouter(
            bikerLocation.getLatitude(),
            bikerLocation.getLongitude(),
            restaurantLat,
            restaurantLng
        );

        double deliveryFee = calculateDeliveryFee(distanceToRestaurant);

        DeliveryAssignment assignment = DeliveryAssignment.builder()
                .orderId(orderId)
                .bikerId(nearestBikerId)
                .restaurantName(restaurantName)
                .restaurantAddress(restaurantAddress)
                .restaurantLat(restaurantLat)
                .restaurantLng(restaurantLng)
                .customerAddress(customerAddress)
                .customerLat(customerLat)
                .customerLng(customerLng)
                .distanceKm(distanceToRestaurant)
                .deliveryFee(deliveryFee)
                .status(DeliveryStatus.PENDING)
                .assignedAt(LocalDateTime.now())
                .rejectionCount(0)
                .build();

        DeliveryAssignment saved = repository.save(assignment);
        
        bikerLocationService.setBikerStatus(nearestBikerId, "BUSY");
        
        log.info("Assigned delivery order {} to biker {} with fee ${}", orderId, nearestBikerId, deliveryFee);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment acceptDelivery(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery is not in pending status");
        }

        assignment.setStatus(DeliveryStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());
        
        DeliveryAssignment saved = repository.save(assignment);
        
        bikerLocationService.setBikerStatus(bikerId, "BUSY");
        
        kafkaTemplate.send("delivery-status-changed", orderId.toString(),
            new DeliveryStatusChangedEvent(orderId, "ASSIGNED", bikerId, null, null, 
                assignment.getDeliveryFee(), assignment.getDistanceKm(), null, null, null));
        
        log.info("Biker {} accepted delivery for order {}", bikerId, orderId);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment rejectDelivery(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery is not in pending status");
        }

        assignment.setRejectionCount(assignment.getRejectionCount() + 1);
        
        if (assignment.getRejectionCount() >= MAX_REJECTIONS) {
            log.warn("Delivery order {} exceeded max rejections, cancelling", orderId);
            assignment.setStatus(DeliveryStatus.REJECTED);
            repository.save(assignment);
            
            kafkaTemplate.send("delivery-status-changed", orderId.toString(),
                new DeliveryStatusChangedEvent(orderId, "CANCELLED", null, null, null, 
                    null, null, null, null, "No biker available"));
            
            return assignment;
        }

        Long newBikerId = bikerLocationService.findNearestAvailableBikerId(
            assignment.getRestaurantLat(), assignment.getRestaurantLng());
        
        if (newBikerId == null) {
            log.warn("No other biker available for order {}", orderId);
            assignment.setStatus(DeliveryStatus.REJECTED);
            repository.save(assignment);
            
            kafkaTemplate.send("delivery-status-changed", orderId.toString(),
                new DeliveryStatusChangedEvent(orderId, "CANCELLED", null, null, null, 
                    null, null, null, null, "No biker available"));
            
            return assignment;
        }

        assignment.setBikerId(newBikerId);
        assignment.setRejectionCount(assignment.getRejectionCount());
        assignment.setAssignedAt(LocalDateTime.now());
        
        DeliveryAssignment saved = repository.save(assignment);
        
        bikerLocationService.setBikerStatus(newBikerId, "BUSY");
        
        log.info("Reassigned delivery order {} to new biker {}", orderId, newBikerId);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment markPickedUp(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.ACCEPTED) {
            throw new RuntimeException("Delivery must be accepted first");
        }

        assignment.setStatus(DeliveryStatus.PICKED_UP);
        assignment.setPickedUpAt(LocalDateTime.now());
        
        DeliveryAssignment saved = repository.save(assignment);
        
        BikerLocation bikerLoc = bikerLocationService.getLocation(bikerId);
        
        kafkaTemplate.send("delivery-status-changed", orderId.toString(),
            new DeliveryStatusChangedEvent(orderId, "PICKED_UP", bikerId, null, null, 
                assignment.getDeliveryFee(), assignment.getDistanceKm(), 
                bikerLoc != null ? bikerLoc.getLatitude() : null,
                bikerLoc != null ? bikerLoc.getLongitude() : null,
                null));
        
        log.info("Biker {} picked up order {}", bikerId, orderId);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment markOnTheWay(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new RuntimeException("Delivery must be picked up first");
        }

        assignment.setStatus(DeliveryStatus.ON_THE_WAY);
        
        DeliveryAssignment saved = repository.save(assignment);
        
        BikerLocation bikerLoc = bikerLocationService.getLocation(bikerId);
        double distanceToCustomer = calculateDistanceWithRouter(
            bikerLoc != null ? bikerLoc.getLatitude() : 0,
            bikerLoc != null ? bikerLoc.getLongitude() : 0,
            assignment.getCustomerLat(),
            assignment.getCustomerLng()
        );
        
        kafkaTemplate.send("delivery-status-changed", orderId.toString(),
            new DeliveryStatusChangedEvent(orderId, "ON_THE_WAY", bikerId, null, null, 
                assignment.getDeliveryFee(), distanceToCustomer,
                bikerLoc != null ? bikerLoc.getLatitude() : null,
                bikerLoc != null ? bikerLoc.getLongitude() : null,
                null));
        
        log.info("Biker {} is on the way to customer for order {}", bikerId, orderId);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment markArrived(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.ON_THE_WAY) {
            throw new RuntimeException("Delivery must be on the way first");
        }

        assignment.setStatus(DeliveryStatus.ARRIVED);
        
        DeliveryAssignment saved = repository.save(assignment);
        
        kafkaTemplate.send("delivery-status-changed", orderId.toString(),
            new DeliveryStatusChangedEvent(orderId, "ARRIVED", bikerId, null, null, 
                assignment.getDeliveryFee(), 0.0, null, null, null));
        
        log.info("Biker {} arrived at customer location for order {}", bikerId, orderId);
        
        return saved;
    }

    @Transactional
    public DeliveryAssignment markDelivered(Long orderId, Long bikerId) {
        DeliveryAssignment assignment = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery assignment not found"));

        if (!assignment.getBikerId().equals(bikerId)) {
            throw new RuntimeException("Biker not assigned to this delivery");
        }

        if (assignment.getStatus() != DeliveryStatus.ARRIVED) {
            throw new RuntimeException("Delivery must be arrived first");
        }

        assignment.setStatus(DeliveryStatus.DELIVERED);
        assignment.setDeliveredAt(LocalDateTime.now());
        
        DeliveryAssignment saved = repository.save(assignment);
        
        bikerLocationService.setBikerStatus(bikerId, "AVAILABLE");
        
        kafkaTemplate.send("delivery-status-changed", orderId.toString(),
            new DeliveryStatusChangedEvent(orderId, "COMPLETED", bikerId, null, null, 
                assignment.getDeliveryFee(), 0.0, null, null, null));
        
        log.info("Biker {} delivered order {} - payment completed", bikerId, orderId);
        
        return saved;
    }

    public DeliveryAssignment getDelivery(Long orderId) {
        return repository.findById(orderId).orElse(null);
    }

    public List<DeliveryAssignment> getBikerDeliveries(Long bikerId) {
        return repository.findByBikerId(bikerId);
    }

    public List<DeliveryAssignment> getPendingDeliveries() {
        return repository.findByStatus(DeliveryStatus.PENDING);
    }

    public double calculateDeliveryFee(double distanceKm) {
        return BASE_DELIVERY_FEE + (distanceKm * FEE_PER_KM);
    }

    private double calculateDistanceWithRouter(double fromLat, double fromLng, double toLat, double toLng) {
        try {
            RouteResponse route = routerClient.calculateRoute(fromLat, fromLng, toLat, toLng);
            if (route != null && route.distanceKm() != null) {
                log.info("Using router service: distance = {} km", route.distanceKm());
                return route.distanceKm();
            }
        } catch (Exception e) {
            log.warn("Router service unavailable, using Haversine fallback: {}", e.getMessage());
        }
        
        return calculateHaversineDistance(fromLat, fromLng, toLat, toLng);
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
}
