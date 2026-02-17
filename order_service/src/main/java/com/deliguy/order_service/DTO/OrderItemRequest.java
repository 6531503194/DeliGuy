package com.deliguy.order_service.DTO;

import com.deliguy.order_service.model.FoodCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
    private Long menuItemId;
    private String foodName;
    private Double price;
    private Integer quantity;
    private String addOns;
    private String customerNote;
}
