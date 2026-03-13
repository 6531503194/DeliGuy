package com.deliguy.restaurent_service.dto;

import com.deliguy.restaurent_service.model.FoodCategory;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateMenuItemRequest {
    private String name;
    private String description;
    private String imageUrl;
    private FoodCategory category;
    private Double price;
}
