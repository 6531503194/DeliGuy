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

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();
}
