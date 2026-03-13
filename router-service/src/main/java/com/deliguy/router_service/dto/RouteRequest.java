package com.deliguy.router_service.dto;

import jakarta.validation.constraints.NotNull;

public record RouteRequest(
    @NotNull(message = "fromLat is required")
    Double fromLat,
    
    @NotNull(message = "fromLng is required")
    Double fromLng,
    
    @NotNull(message = "toLat is required")
    Double toLat,
    
    @NotNull(message = "toLng is required")
    Double toLng
) {}
