package com.deliguy.biker_service.kafka;

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
