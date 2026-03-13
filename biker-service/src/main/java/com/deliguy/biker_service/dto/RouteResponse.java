package com.deliguy.biker_service.dto;

import java.util.List;

public record RouteResponse(
    Double distanceKm,
    Long durationSeconds,
    Integer etaMinutes,
    String routeGeometry,
    List<RouteStep> steps
) {
    public record RouteStep(
        String instruction,
        Double distanceMeters,
        Integer durationSeconds
    ) {}
}
