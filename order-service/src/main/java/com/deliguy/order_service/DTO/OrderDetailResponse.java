package com.deliguy.order_service.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.deliguy.order_service.model.OrderItem;
import com.deliguy.order_service.model.OrderStatus;
import com.deliguy.order_service.model.PaymentStatus;

import lombok.Data;

@Data
public class OrderDetailResponse {
    private Long id;
    private Long CustomerUserId;
    private String customerPhone;
    private String deliveryAddress;
    private String restaurantId;
    private String restaurantName;
    private Double totalAmount;
    private Double deliveryFee;
    private Double totalWithDelivery;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime paidAt;
    private String rejectionReason;
    private List<OrderItemDto> items;
    
    // Biker info (available when assigned)
    private Long bikerId;
    private String bikerName;
    private String bikerPhone;
    private String vehicleNumber;
    
    // Customer location
    private Double customerLat;
    private Double customerLng;
    
    // Biker location (available when picked up or later)
    private Double bikerLatitude;
    private Double bikerLongitude;
    private Double distanceToCustomerKm;
    
    @Data
    public static class OrderItemDto {
        private String foodName;
        private Double price;
        private Integer quantity;
        private Double subtotal;
        
        public static OrderItemDto fromEntity(OrderItem item) {
            OrderItemDto dto = new OrderItemDto();
            dto.setFoodName(item.getFoodName());
            dto.setPrice(item.getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setSubtotal(item.getSubtotal());
            return dto;
        }
    }
}
