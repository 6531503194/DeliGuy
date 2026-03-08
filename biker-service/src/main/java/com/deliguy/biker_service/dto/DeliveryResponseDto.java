package com.deliguy.biker_service.dto;

import java.time.LocalDateTime;

import com.deliguy.biker_service.model.DeliveryStatus;

import lombok.Data;

@Data
public class DeliveryResponseDto {
    private Long orderId;
    private String restaurantName;
    private String restaurantAddress;
    private Double restaurantLat;
    private Double restaurantLng;
    private String customerAddress;
    private Double customerLat;
    private Double customerLng;
    private Double distanceKm;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
}
