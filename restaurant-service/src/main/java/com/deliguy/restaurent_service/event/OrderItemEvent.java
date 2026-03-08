package com.deliguy.restaurent_service.event;

public record OrderItemEvent(
    String foodName,
    String category,
    Double price,
    Integer quantity,
    Double subtotal
) {}
