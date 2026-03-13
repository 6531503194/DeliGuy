package com.deliguy.router_service.dto;

public record RouteStep(
    String instruction,
    Double distanceMeters,
    Integer durationSeconds
) {}
