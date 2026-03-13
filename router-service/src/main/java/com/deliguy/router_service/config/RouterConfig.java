package com.deliguy.router_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RouterConfig {

    @Value("${router.api.base-url}")
    private String baseUrl;

    @Value("${router.api.api-key}")
    private String apiKey;

    @Value("${router.api.profile}")
    private String profile;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProfile() {
        return profile;
    }
}
