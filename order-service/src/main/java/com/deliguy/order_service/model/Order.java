package com.deliguy.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerUsername;
    private String customerPhone;
    private String deliveryAddress;
    private String restaurantId;
    private String restaurantName;
    private Double totalAmount;
    private Double deliveryFee;
    private Double totalWithDelivery;

    private Long bikerId;
    private String bikerName;
    private String bikerPhone;
    private String vehicleNumber;

    private Double customerLat;
    private Double customerLng;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime paidAt;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();
}
