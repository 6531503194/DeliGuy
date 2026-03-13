package com.deliguy.restaurent_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant_orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RestaurantOrder {

    @Id
    private Long orderId; // SAME ID from order-service

    private String restaurantId;

    private Long customerUserId;

    private String customerPhone;

    private String customerAddress;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(columnDefinition = "TEXT")
    private String orderItemsJson;

    private String rejectionReason; // Internal - for restaurant only

    private LocalDateTime createdAt;
}
