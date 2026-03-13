package com.deliguy.order_service.DTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Customer user ID is required")
    private Long customerUserId;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    @NotBlank(message = "Restaurant name is required")
    private String restaurantName;
    
    @NotNull(message = "Customer latitude is required")
    private Double customerLat;
    
    @NotNull(message = "Customer longitude is required")
    private Double customerLng;
    
    private List<OrderItemRequest> items;
}
