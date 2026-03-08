package com.deliguy.order_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.deliguy.order_service.DTO.BikerLocationResponse;
import com.deliguy.order_service.model.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikerLocationService {

    private final RestTemplate restTemplate;

    private static final String BIKER_SERVICE_URL = "http://biker-service:8085";

    public Optional<BikerLocationResponse> getBikerLocation(Long bikerId, Double customerLat, Double customerLng) {
        try {
            String url = String.format("%s/biker/%d/location", BIKER_SERVICE_URL, bikerId);
            
            BikerLocationResponse response = restTemplate.getForObject(url, BikerLocationResponse.class);
            
            if (response != null && customerLat != null && customerLng != null) {
                double distance = calculateDistance(customerLat, customerLng, 
                    response.getLatitude(), response.getLongitude());
                response.setDistanceToCustomerKm(distance);
                response.setEstimatedArrivalMinutes((int) Math.ceil(distance * 5)); // 5 min per km
            }
            
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.warn("Failed to get biker location: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<BikerLocationResponse> getBikerInfo(Long bikerId) {
        try {
            String url = String.format("%s/biker/%d", BIKER_SERVICE_URL, bikerId);
            
            BikerLocationResponse response = restTemplate.getForObject(url, BikerLocationResponse.class);
            
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.warn("Failed to get biker info: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // km
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
}
