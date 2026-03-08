package com.deliguy.order_service.event;

public record DeliveryAssignedEvent(
    Long orderId,
    String restaurantName,
    String restaurantAddress,
    Double restaurantLat,
    Double restaurantLng,
    String customerAddress,
    Double customerLat,
    Double customerLng
) {}
