package com.deliguy.restaurent_service.event;

import java.util.List;

public record OrderCreatedEvent(
    Long orderId,
    String customerUsername,
    String customerPhone,
    String deliveryAddress,
    String restaurantId,
    String restaurantName,
    Double totalAmount,
    List<OrderItemEvent> items
) {}
