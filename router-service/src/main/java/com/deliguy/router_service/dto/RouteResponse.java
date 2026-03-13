package com.deliguy.router_service.dto;

import java.util.List;

public record RouteResponse(
    Double distanceKm,
    Long durationSeconds,
    Integer etaMinutes,
    String routeGeometry,
    List<RouteStep> steps,
    String fromAddress,
    String toAddress
) {
    public static RouteResponse fallback(Double distanceKm, Long durationSeconds) {
        return new RouteResponse(
            distanceKm,
            durationSeconds,
            durationSeconds != null ? (int)(durationSeconds / 60) : null,
            null,
            null,
            null,
            null
        );
    }
}
