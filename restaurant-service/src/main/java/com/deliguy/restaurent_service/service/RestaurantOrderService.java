package com.deliguy.restaurent_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliguy.restaurent_service.model.OrderStatus;
import com.deliguy.restaurent_service.model.RestaurantOrder;
import com.deliguy.restaurent_service.repository.RestaurantOrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantOrderService {

    private final RestaurantOrderRepository repository;

    public List<RestaurantOrder> getOrders(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId);
    }

    public RestaurantOrder decideOrder(
            Long orderId,
            boolean accept
    ) {
        RestaurantOrder order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order already processed");
        }

        order.setStatus(
            accept ? OrderStatus.ACCEPTED : OrderStatus.REJECTED
        );

        return repository.save(order);
    }
}
