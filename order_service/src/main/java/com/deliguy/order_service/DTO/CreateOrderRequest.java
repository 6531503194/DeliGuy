package com.deliguy.order_service.DTO;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private String customerUsername;
    private String customerPhone;
    private String deliveryAddress;
    private String restaurantId;
    private String restaurantName;
    private List<OrderItemRequest> items;
}

