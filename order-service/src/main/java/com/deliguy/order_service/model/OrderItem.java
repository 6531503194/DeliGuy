package com.deliguy.order_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuItemId;
    private String foodName;
    private Double price;

    @Enumerated(EnumType.STRING)
    private FoodCategory category;
    
    private Integer quantity;
    private Double subtotal;

    private String addOns; 

    private String customerNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}


