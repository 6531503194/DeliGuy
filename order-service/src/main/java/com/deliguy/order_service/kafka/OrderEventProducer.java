package com.deliguy.order_service.kafka;

import com.deliguy.order_service.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order-created", event.orderId().toString(), event);
    }
}
