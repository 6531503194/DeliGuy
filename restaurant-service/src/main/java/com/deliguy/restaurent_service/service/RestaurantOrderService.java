package com.deliguy.restaurent_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliguy.restaurent_service.dto.OrderDetailResponse;
import com.deliguy.restaurent_service.dto.OrderItemDto;
import com.deliguy.restaurent_service.kafka.KafkaRestaurantEventProducer;
import com.deliguy.restaurent_service.model.OrderStatus;
import com.deliguy.restaurent_service.model.RestaurantOrder;
import com.deliguy.restaurent_service.repository.RestaurantOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantOrderService {

    private final RestaurantOrderRepository repository;
    private final KafkaRestaurantEventProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public List<RestaurantOrder> getOrders(String restaurantId, OrderStatus status) {
        if (status != null) {
            return repository.findByRestaurantIdAndStatus(restaurantId, status);
        }
        return repository.findByRestaurantId(restaurantId);
    }

    public OrderDetailResponse getOrderById(Long orderId) {
        RestaurantOrder order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemDto> items;
        try {
            items = objectMapper.readValue(order.getOrderItemsJson(), new TypeReference<List<OrderItemDto>>() {});
        } catch (JsonProcessingException e) {
            items = List.of();
        }

        return new OrderDetailResponse(
                order.getOrderId(),
                order.getCustomerUserId(),
                order.getCustomerPhone(),
                order.getCustomerAddress(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items,
                order.getRejectionReason()
        );
    }

    public RestaurantOrder decideOrder(
            Long orderId,
            boolean accept,
            String reason
    ) {
        RestaurantOrder order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order already processed");
        }

        OrderStatus newStatus = accept ? OrderStatus.ACCEPTED : OrderStatus.REJECTED;
        order.setStatus(newStatus);

        if (!accept && reason != null && !reason.isBlank()) {
            order.setRejectionReason(reason);
        }

        RestaurantOrder savedOrder = repository.save(order);
        
        kafkaProducer.sendOrderStatusUpdated(
                orderId, 
                newStatus.name(), 
                order.getRestaurantId(),
                reason
        );
        
        return savedOrder;
    }
}
