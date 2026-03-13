package com.deliguy.restaurent_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliguy.restaurent_service.dto.OrderDecisionRequest;
import com.deliguy.restaurent_service.dto.OrderDetailResponse;
import com.deliguy.restaurent_service.model.OrderStatus;
import com.deliguy.restaurent_service.model.RestaurantOrder;
import com.deliguy.restaurent_service.service.RestaurantOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurant/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT')")
public class RestaurantOrderController {

    private final RestaurantOrderService service;

    @GetMapping
    public List<RestaurantOrder> myOrders(
            @RequestHeader("X-RESTAURANT-ID") String restaurantId,
            @RequestParam(required = false) OrderStatus status
    ) {
        return service.getOrders(restaurantId, status);
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrder(@RequestHeader("X-RESTAURANT-ID") String restaurantId,@PathVariable Long orderId) {
        return service.getOrderById(orderId);
    }

    @PostMapping("/{orderId}/decision")
    public RestaurantOrder decide(
            @RequestHeader("X-RESTAURANT-ID") String restaurantId,
            @PathVariable Long orderId,
            @RequestBody OrderDecisionRequest request
    ) {
        return service.decideOrder(orderId, request.isAccept(), request.getReason());
    }
}

