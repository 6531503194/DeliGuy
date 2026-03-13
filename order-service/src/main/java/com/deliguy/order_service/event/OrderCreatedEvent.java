package com.deliguy.order_service.event;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record OrderCreatedEvent(
    Long orderId,
    Long customerUserId,
    String customerPhone,
    String deliveryAddress,
    Double customerLat,
    Double customerLng,
    String restaurantId,
    String restaurantName,
    Double totalAmount,
    List<OrderItemEvent> items
) {}
