package com.deliguy.router_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliguy.router_service.dto.RouteRequest;
import com.deliguy.router_service.dto.RouteResponse;
import com.deliguy.router_service.service.RouterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RouterController {

    private final RouterService routerService;

    @PostMapping
    public ResponseEntity<RouteResponse> calculateRoute(@Valid @RequestBody RouteRequest request) {
        log.info("Received route calculation request: from ({}, {}) to ({}, {})",
                request.fromLat(), request.fromLng(), request.toLat(), request.toLng());
        
        RouteResponse response = routerService.calculateRoute(request);
        
        log.info("Route calculated: distance={}km, duration={}s",
                response.distanceKm(), response.durationSeconds());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "router-service"));
    }
}
