package com.deliguy.restaurent_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
    Long orderId,
    String customerUsername,
    String customerPhone,
    String customerAddress,
    Double totalAmount,
    String status,
    LocalDateTime createdAt,
    List<OrderItemDto> items,
    String rejectionReason
) {}
