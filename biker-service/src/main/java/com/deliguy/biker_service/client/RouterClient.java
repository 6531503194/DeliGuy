package com.deliguy.biker_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.deliguy.biker_service.dto.RouteResponse;

@Component
public class RouterClient {

    private static final Logger log = LoggerFactory.getLogger(RouterClient.class);
    
    private final RestTemplate restTemplate;
    private final String routerServiceUrl;

    public RouterClient(
            @Value("${router.service.base-url}") String routerServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.routerServiceUrl = routerServiceUrl;
    }

    public RouteResponse calculateRoute(Double fromLat, Double fromLng, Double toLat, Double toLng) {
        try {
            String url = String.format("%s/api/route", routerServiceUrl);
            
            String requestBody = String.format(
                "{\"fromLat\":%f,\"fromLng\":%f,\"toLat\":%f,\"toLng\":%f}",
                fromLat, fromLng, toLat, toLng
            );
            
            return restTemplate.postForObject(url, requestBody, RouteResponse.class);
        } catch (Exception e) {
            log.error("Failed to call router service: {}", e.getMessage());
            return null;
        }
    }

    public RouteResponse calculateRouteToRestaurant(Double bikerLat, Double bikerLng, 
                                                      Double restaurantLat, Double restaurantLng) {
        return calculateRoute(bikerLat, bikerLng, restaurantLat, restaurantLng);
    }

    public RouteResponse calculateRouteToCustomer(Double bikerLat, Double bikerLng,
                                                   Double customerLat, Double customerLng) {
        return calculateRoute(bikerLat, bikerLng, customerLat, customerLng);
    }
}
