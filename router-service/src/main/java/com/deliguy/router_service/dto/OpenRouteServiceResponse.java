package com.deliguy.router_service.dto;

import java.util.List;

public record OpenRouteServiceResponse(
    List<Route> routes
) {
    public record Route(
        Summary summary,
        Geometry geometry,
        List<Segment> segments
    ) {}
    
    public record Summary(
        Double distance,
        Long duration
    ) {}
    
    public record Geometry(
        String type,
        List<List<Double>> coordinates
    ) {}
    
    public record Segment(
        List<Step> steps,
        Double distance,
        Long duration
    ) {}
    
    public record Step(
        String instruction,
        Double distance,
        Long duration
    ) {}
}
