package com.deliguy.order_service.kafka;

import com.deliguy.order_service.event.DeliveryAssignedEvent;
import com.deliguy.order_service.event.OrderStatusUpdatedEvent;
import com.deliguy.order_service.model.OrderStatus;
import com.deliguy.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderEventConsumer {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-status-updated", groupId = "order-service-group")
    public void consumeOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        log.info("Received order status update: orderId={}, status={}", event.orderId(), event.status());

        orderRepository.findById(event.orderId()).ifPresent(order -> {
            OrderStatus newStatus = OrderStatus.valueOf(event.status());
            order.setStatus(newStatus);
            
            if (event.rejectionReason() != null && !event.rejectionReason().isBlank()) {
                order.setRejectionReason(event.rejectionReason());
            }
            
            orderRepository.save(order);
            log.info("Order {} status updated to {}", event.orderId(), event.status());

            if (newStatus == OrderStatus.ACCEPTED) {
                sendDeliveryAssignedEvent(order);
            }
        });
    }

    private void sendDeliveryAssignedEvent(com.deliguy.order_service.model.Order order) {
        try {
            DeliveryAssignedEvent event = new DeliveryAssignedEvent(
                order.getId(),
                order.getRestaurantName(),
                order.getDeliveryAddress(),
                0.0, 
                0.0, 
                order.getDeliveryAddress(),
                0.0,
                0.0
            );
            
            kafkaTemplate.send("delivery-assigned", order.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send delivery-assigned event for order {}: {}", 
                            order.getId(), ex.getMessage());
                    } else {
                        log.info("Successfully sent delivery-assigned event for order {}", order.getId());
                    }
                });
        } catch (Exception e) {
            log.error("Error sending delivery-assigned event for order {}: {}", order.getId(), e.getMessage());
        }
    }
}
