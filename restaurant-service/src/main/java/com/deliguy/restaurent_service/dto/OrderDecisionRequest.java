package com.deliguy.restaurent_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderDecisionRequest {
    private boolean accept;
    private String reason; // Optional - for restaurant to provide reason when rejecting
}
