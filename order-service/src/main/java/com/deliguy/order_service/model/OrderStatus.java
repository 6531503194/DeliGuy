package com.deliguy.order_service.model;

public enum OrderStatus {
    CREATED,
    PENDING,
    ACCEPTED,
    REJECTED,
    PREPARING,
    ASSIGNED,
    PICKED_UP,
    ON_THE_WAY,
    ARRIVED,
    COMPLETED,
    CANCELLED, 
}
