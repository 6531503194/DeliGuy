package com.deliguy.order_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusWebSocketController {

    @MessageMapping("/delivery/status")
    @SendTo("/topic/order/{orderId}/status")
    public String sendDeliveryStatus(String status) {
        log.info("Sending delivery status update: {}", status);
        return status;
    }
}