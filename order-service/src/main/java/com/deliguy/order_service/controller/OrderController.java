package com.deliguy.order_service.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliguy.order_service.DTO.BikerLocationResponse;
import com.deliguy.order_service.DTO.CreateOrderRequest;
import com.deliguy.order_service.DTO.OrderDetailResponse;
import com.deliguy.order_service.DTO.PaymentRequest;
import com.deliguy.order_service.model.Order;
import com.deliguy.order_service.model.OrderStatus;
import com.deliguy.order_service.model.PaymentStatus;
import com.deliguy.order_service.repository.OrderRepository;
import com.deliguy.order_service.service.BikerLocationService;
import com.deliguy.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final BikerLocationService bikerLocationService;

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse get(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return toDetailResponse(order);
    }

    @GetMapping("/{id}/biker-location")
    public ResponseEntity<BikerLocationResponse> getBikerLocation(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        
        if (order.getBikerId() == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (order.getStatus() != OrderStatus.PICKED_UP && 
            order.getStatus() != OrderStatus.ON_THE_WAY &&
            order.getStatus() != OrderStatus.ARRIVED) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<BikerLocationResponse> bikerInfoOpt = bikerLocationService.getBikerInfo(order.getBikerId());
        if (!bikerInfoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        BikerLocationResponse response = bikerInfoOpt.get();
        response.setBikerId(order.getBikerId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDetailResponse> pay(@PathVariable Long id, @RequestBody PaymentRequest request) {
        Order order = orderService.getOrderById(id);
        
        if (order.getStatus() != OrderStatus.ARRIVED) {
            return ResponseEntity.badRequest().build();
        }
        
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean paymentSuccess = processPayment(order, request);
        
        if (paymentSuccess) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setPaidAt(LocalDateTime.now());
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            
            return ResponseEntity.ok(toDetailResponse(order));
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/payment-info")
    public ResponseEntity<OrderPaymentInfo> getPaymentInfo(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        
        if (order.getStatus() != OrderStatus.ARRIVED && order.getStatus() != OrderStatus.COMPLETED) {
            return ResponseEntity.badRequest().build();
        }
        
        OrderPaymentInfo info = new OrderPaymentInfo();
        info.setOrderId(order.getId());
        info.setTotalAmount(order.getTotalAmount());
        info.setDeliveryFee(order.getDeliveryFee() != null ? order.getDeliveryFee() : 0.0);
        info.setTotalWithDelivery(order.getTotalWithDelivery() != null ? order.getTotalWithDelivery() : order.getTotalAmount());
        info.setPaymentStatus(order.getPaymentStatus());
        info.setAmountDue(order.getTotalWithDelivery() != null ? order.getTotalWithDelivery() : order.getTotalAmount());
        
        return ResponseEntity.ok(info);
    }

    private boolean processPayment(Order order, PaymentRequest request) {
        return true;
    }

    private OrderDetailResponse toDetailResponse(Order order) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setId(order.getId());
        response.setCustomerUsername(order.getCustomerUsername());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setRestaurantId(order.getRestaurantId());
        response.setRestaurantName(order.getRestaurantName());
        response.setTotalAmount(order.getTotalAmount());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTotalWithDelivery(order.getTotalWithDelivery());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setAcceptedAt(order.getAcceptedAt());
        response.setPickedUpAt(order.getPickedUpAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setPaidAt(order.getPaidAt());
        response.setRejectionReason(order.getRejectionReason());
        response.setBikerId(order.getBikerId());
        response.setBikerName(order.getBikerName());
        response.setBikerPhone(order.getBikerPhone());
        response.setVehicleNumber(order.getVehicleNumber());
        response.setCustomerLat(order.getCustomerLat());
        response.setCustomerLng(order.getCustomerLng());
        
        if (order.getItems() != null) {
            response.setItems(order.getItems().stream()
                .map(OrderDetailResponse.OrderItemDto::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return response;
    }

    public static class OrderPaymentInfo {
        private Long orderId;
        private Double totalAmount;
        private Double deliveryFee;
        private Double totalWithDelivery;
        private PaymentStatus paymentStatus;
        private Double amountDue;
        
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        public Double getDeliveryFee() { return deliveryFee; }
        public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }
        public Double getTotalWithDelivery() { return totalWithDelivery; }
        public void setTotalWithDelivery(Double totalWithDelivery) { this.totalWithDelivery = totalWithDelivery; }
        public PaymentStatus getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
        public Double getAmountDue() { return amountDue; }
        public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }
    }
}
