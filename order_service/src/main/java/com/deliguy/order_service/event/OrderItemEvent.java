package com.deliguy.order_service.event;

import com.deliguy.order_service.model.FoodCategory;

public record OrderItemEvent(
    String itemName,
    FoodCategory category,
    Double price,
    Integer quantity,
    Double subtotal
) {}
