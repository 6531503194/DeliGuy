package com.deliguy.order_service.event;

public record OrderStatusUpdatedEvent(
    Long orderId,
    String status,
    String restaurantId,
    String rejectionReason
) {}
