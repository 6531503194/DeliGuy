package com.deliguy.restaurent_service.dto;

public record OrderItemDto(
    String foodName,
    String category,
    Double price,
    Integer quantity,
    Double subtotal,
    String addOns,
    String customerNote
) {}
