package com.deliguy.order_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.deliguy.order_service.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreated(OrderCreatedEvent event) {
        System.out.println("*************************************");
        System.out.println("Producing order created event for order ID: " + event.orderId());
        kafkaTemplate.send("order-created", event.orderId().toString(), event);
    }
}

