package com.deliguy.biker_service.controller;

import com.deliguy.biker_service.model.BikerLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BikerLocationWebSocketController {

    @MessageMapping("/biker/location")
    @SendTo("/topic/order/{orderId}/location")
    public BikerLocation sendLocation(BikerLocation location) {
        log.info("Sending biker location to order {}", location.getBikerId());
        return location;
    }
}