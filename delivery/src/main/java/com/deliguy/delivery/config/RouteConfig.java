package com.deliguy.delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r
                .path("/auth/**")
                .filters(f -> f.addRequestHeader("X-GATEWAY-INTERNAL", "true"))
                .uri("http://localhost:8081")
            )
            .route("order-service", r -> r
                .path("/orders/**")
                .filters(f -> f.addRequestHeader("X-GATEWAY-INTERNAL", "true"))
                .uri("http://localhost:8082")
            )
            .route("restaurant-service", r -> r
                .path("/restaurant/**")
                .uri("http://localhost:8083")
            )
            .route("delivery-service", r -> r
                .path("/delivery/**")
                .uri("http://localhost:8084")
            )
            .build();
    }
}
