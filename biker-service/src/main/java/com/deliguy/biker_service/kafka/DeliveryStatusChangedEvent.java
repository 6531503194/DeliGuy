package com.deliguy.biker_service.kafka;

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
    String reason
) {}