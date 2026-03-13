package com.deliguy.order_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.deliguy.order_service.DTO.CreateOrderRequest;
import com.deliguy.order_service.event.OrderCreatedEvent;
import com.deliguy.order_service.event.OrderItemEvent;
import com.deliguy.order_service.model.Order;
import com.deliguy.order_service.model.OrderItem;
import com.deliguy.order_service.model.OrderStatus;
import com.deliguy.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    public Order createOrder(Long userId,CreateOrderRequest request) {

        Order order = buildOrder(request);
        order.setCustomerUserId(userId);
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerUserId(),
                savedOrder.getCustomerPhone(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getCustomerLat(),
                savedOrder.getCustomerLng(),
                savedOrder.getRestaurantId(),
                savedOrder.getRestaurantName(),
                savedOrder.getTotalAmount(),
                savedOrder.getItems().stream()
                        .map(i -> new OrderItemEvent(
                                i.getFoodName(),
                                i.getCategory(),
                                i.getPrice(),
                                i.getQuantity(),
                                i.getSubtotal()
                        ))
                        .toList()
        );

        eventProducer.sendOrderCreated(event);
        return savedOrder;
    }

    private Order buildOrder(CreateOrderRequest request) {

    Order order = Order.builder()
            .customerUserId(request.getCustomerUserId())
            .customerPhone(request.getCustomerPhone())
            .deliveryAddress(request.getDeliveryAddress())
            .restaurantId(request.getRestaurantId())
            .restaurantName(request.getRestaurantName())
            .customerLat(request.getCustomerLat())
            .customerLng(request.getCustomerLng())
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        var items = request.getItems().stream().map(itemReq -> {

                double subtotal = itemReq.getPrice() * itemReq.getQuantity();

                return OrderItem.builder()
                        .menuItemId(itemReq.getMenuItemId())
                        .foodName(itemReq.getFoodName())
                        .price(itemReq.getPrice())
                        .quantity(itemReq.getQuantity())
                        .subtotal(subtotal)
                        .addOns(itemReq.getAddOns())
                        .customerNote(itemReq.getCustomerNote())
                        .order(order)
                        .build();

                }).toList();

                order.setItems(items);
                order.setTotalAmount(
                        items.stream().mapToDouble(OrderItem::getSubtotal).sum()
                );

                return order;
        }


    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}
