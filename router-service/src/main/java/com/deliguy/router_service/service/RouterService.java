package com.deliguy.router_service.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.deliguy.router_service.config.RouterConfig;
import com.deliguy.router_service.dto.OpenRouteServiceResponse;
import com.deliguy.router_service.dto.RouteRequest;
import com.deliguy.router_service.dto.RouteResponse;
import com.deliguy.router_service.dto.RouteStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;
    private final RouterConfig routerConfig;

    @Value("${router.api.cache-ttl-minutes:60}")
    private int cacheTtlMinutes;

    private static final String CACHE_PREFIX = "route:";
    private static final double EARTH_RADIUS_KM = 6371.0;

    public RouteResponse calculateRoute(RouteRequest request) {
        String cacheKey = buildCacheKey(request.fromLat(), request.fromLng(), request.toLat(), request.toLng());

        try {
            RouteResponse cached = (RouteResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for route: {}", cacheKey);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis cache read failed: {}", e.getMessage());
        }

        log.info("Calling OpenRouteService API for route calculation");
        RouteResponse response = callOpenRouteService(request);

        if (response != null) {
            try {
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(cacheTtlMinutes));
                log.debug("Cached route: {} for {} minutes", cacheKey, cacheTtlMinutes);
            } catch (Exception e) {
                log.warn("Redis cache write failed: {}", e.getMessage());
            }
            return response;
        }

        log.warn("OpenRouteService API failed, using Haversine fallback");
        return calculateHaversineFallback(request);
    }

    private RouteResponse callOpenRouteService(RouteRequest request) {
        try {
            String url = String.format("%s/v2/directions/%s?api_key=%s&start=%f,%f&end=%f,%f",
                    routerConfig.getBaseUrl(),
                    routerConfig.getApiKey(),
                    routerConfig.getProfile(),
                    request.fromLng(), request.fromLat(),
                    request.toLng(), request.toLat());

            OpenRouteServiceResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenRouteServiceResponse.class)
                    .block();

            if (response != null && response.routes() != null && !response.routes().isEmpty()) {
                OpenRouteServiceResponse.Route route = response.routes().get(0);
                
                List<RouteStep> steps = new ArrayList<>();
                if (route.segments() != null && !route.segments().isEmpty()) {
                    OpenRouteServiceResponse.Segment segment = route.segments().get(0);
                    if (segment.steps() != null) {
                        for (OpenRouteServiceResponse.Step step : segment.steps()) {
                            steps.add(new RouteStep(
                                step.instruction(),
                                step.distance(),
                                step.duration() != null ? step.duration().intValue() : null
                            ));
                        }
                    }
                }

                String geometry = null;
                if (route.geometry() != null && route.geometry().coordinates() != null) {
                    geometry = encodePolyline(route.geometry().coordinates());
                }

                return new RouteResponse(
                    route.summary().distance() / 1000.0,
                    route.summary().duration(),
                    route.summary().duration() != null ? (int)(route.summary().duration() / 60) : null,
                    geometry,
                    steps,
                    null,
                    null
                );
            }
        } catch (Exception e) {
            log.error("OpenRouteService API call failed: {}", e.getMessage());
        }
        return null;
    }

    private RouteResponse calculateHaversineFallback(RouteRequest request) {
        double distance = calculateHaversineDistance(
            request.fromLat(), request.fromLng(),
            request.toLat(), request.toLng()
        );
        
        long durationSeconds = (long) (distance * 180);
        
        return RouteResponse.fallback(distance, durationSeconds);
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private String buildCacheKey(double fromLat, double fromLng, double toLat, double toLng) {
        return CACHE_PREFIX + String.format("%.4f,%.4f:%.4f,%.4f", fromLat, fromLng, toLat, toLng);
    }

    private String encodePolyline(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        int prevLat = 0;
        int prevLng = 0;

        for (List<Double> coord : coordinates) {
            int lat = (int) Math.round(coord.get(1) * 1e5);
            int lng = (int) Math.round(coord.get(0) * 1e5);

            result.append(encodeValue(lat - prevLat));
            result.append(encodeValue(lng - prevLng));

            prevLat = lat;
            prevLng = lng;
        }

        return result.toString();
    }

    private String encodeValue(int value) {
        if (value < 0) {
            value = ~value;
        }
        
        StringBuilder result = new StringBuilder();
        while (value >= 0x20) {
            result.append((char) ((0x20 | (value & 0x1f)) + 63));
            value >>= 5;
        }
        result.append((char) (value + 63));
        
        return result.toString();
    }

    public RouteResponse calculateRouteToRestaurant(Double bikerLat, Double bikerLng, 
                                                       Double restaurantLat, Double restaurantLng) {
        return calculateRoute(new RouteRequest(bikerLat, bikerLng, restaurantLat, restaurantLng));
    }

    public RouteResponse calculateRouteToCustomer(Double bikerLat, Double bikerLng,
                                                   Double customerLat, Double customerLng) {
        return calculateRoute(new RouteRequest(bikerLat, bikerLng, customerLat, customerLng));
    }

    public RouteResponse calculateRouteFromRestaurantToCustomer(Double restaurantLat, Double restaurantLng,
                                                                  Double customerLat, Double customerLng) {
        return calculateRoute(new RouteRequest(restaurantLat, restaurantLng, customerLat, customerLng));
    }
}
