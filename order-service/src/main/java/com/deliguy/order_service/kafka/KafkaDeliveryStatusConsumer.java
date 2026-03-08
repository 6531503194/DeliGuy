package com.deliguy.order_service.kafka;

import java.time.LocalDateTime;

import com.deliguy.order_service.event.DeliveryStatusChangedEvent;
import com.deliguy.order_service.model.Biker;
import com.deliguy.order_service.model.Order;
import com.deliguy.order_service.model.OrderStatus;
import com.deliguy.order_service.model.PaymentStatus;
import com.deliguy.order_service.repository.BikerRepository;
import com.deliguy.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDeliveryStatusConsumer {

    private final OrderRepository orderRepository;
    private final BikerRepository bikerRepository;

    @KafkaListener(topics = "delivery-status-changed", groupId = "order-service-group")
    public void consumeDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
        log.info("Received delivery status update: orderId={}, status={}, bikerId={}", 
            event.orderId(), event.status(), event.bikerId());

        orderRepository.findById(event.orderId()).ifPresent(order -> {
            OrderStatus newStatus = switch (event.status()) {
                case "ASSIGNED" -> OrderStatus.ASSIGNED;
                case "PICKED_UP" -> OrderStatus.PICKED_UP;
                case "ON_THE_WAY" -> OrderStatus.ON_THE_WAY;
                case "ARRIVED" -> OrderStatus.ARRIVED;
                case "COMPLETED" -> OrderStatus.COMPLETED;
                case "CANCELLED" -> OrderStatus.CANCELLED;
                default -> order.getStatus();
            };
            
            if (event.bikerId() != null) {
                order.setBikerId(event.bikerId());
                
                // Save biker info to database (if not already exists)
                if (event.bikerName() != null) {
                    bikerRepository.findById(event.bikerId()).orElseGet(() -> {
                        Biker biker = Biker.builder()
                                .id(event.bikerId())
                                .name(event.bikerName())
                                .phone(event.bikerPhone())
                                .vehicleNumber(event.vehicleNumber() != null ? event.vehicleNumber() : "N/A")
                                .build();
                        return bikerRepository.save(biker);
                    });
                }
                
                order.setBikerName(event.bikerName());
                order.setBikerPhone(event.bikerPhone());
                order.setVehicleNumber(event.vehicleNumber());
            }
            
            if (event.deliveryFee() != null) {
                order.setDeliveryFee(event.deliveryFee());
                if (order.getTotalAmount() != null) {
                    order.setTotalWithDelivery(order.getTotalAmount() + event.deliveryFee());
                }
            }
            
            order.setStatus(newStatus);
            
            switch (event.status()) {
                case "ASSIGNED" -> order.setAcceptedAt(LocalDateTime.now());
                case "PICKED_UP" -> order.setPickedUpAt(LocalDateTime.now());
                case "COMPLETED" -> order.setDeliveredAt(LocalDateTime.now());
                default -> {}
            }
            
            orderRepository.save(order);
            
            log.info("Order {} status updated to {} via delivery event", event.orderId(), newStatus);
        });
    }
}
