package com.deliguy.restaurent_service.kafka;

import com.deliguy.restaurent_service.event.OrderCreatedEvent;
import com.deliguy.restaurent_service.model.OrderStatus;
import com.deliguy.restaurent_service.model.RestaurantOrder;
import com.deliguy.restaurent_service.repository.RestaurantOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRestaurantEventConsumer {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created", groupId = "restaurant-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Received new order: orderId={}, restaurantId={}", event.orderId(), event.restaurantId());

        if (restaurantOrderRepository.existsById(event.orderId())) {
            log.warn("Order {} already exists, skipping", event.orderId());
            return;
        }

        String orderItemsJson;
        try {
            orderItemsJson = objectMapper.writeValueAsString(event.items());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order items for orderId={}: {}", event.orderId(), e.getMessage());
            orderItemsJson = "[]";
        }

        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(event.orderId())
                .restaurantId(event.restaurantId())
                .customerUserId(event.customerUserId())
                .customerPhone(event.customerPhone())
                .customerAddress(event.deliveryAddress())
                .totalAmount(event.totalAmount())
                .orderItemsJson(orderItemsJson)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        restaurantOrderRepository.save(order);
        log.info("Created restaurant order: orderId={}", event.orderId());
    }
}
