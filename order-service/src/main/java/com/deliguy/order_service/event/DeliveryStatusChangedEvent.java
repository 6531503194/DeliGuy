package com.deliguy.order_service.event;

public record DeliveryStatusChangedEvent(
    Long orderId,
    String status,
    Long bikerId,
    String bikerName,
    String bikerPhone,
    Double deliveryFee,
    Double distanceKm,
    Double bikerLatitude,
    Double bikerLongitude,
    String vehicleNumber,
    String reason
) {}
