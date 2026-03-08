package com.deliguy.biker_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAssignment {

    @Id
    private Long orderId;

    private Long bikerId;

    private String restaurantName;

    private String restaurantAddress;

    private Double restaurantLat;

    private Double restaurantLng;

    private String customerAddress;

    private Double customerLat;

    private Double customerLng;

    private Double distanceKm;

    private Double deliveryFee;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private LocalDateTime assignedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime pickedUpAt;

    private LocalDateTime deliveredAt;

    private Integer rejectionCount;
}
