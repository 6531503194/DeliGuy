package com.deliguy.biker_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.deliguy.biker_service.model.Biker;
import com.deliguy.biker_service.model.BikerLocation;
import com.deliguy.biker_service.repository.BikerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikerLocationService {

    private static final String BIKER_LOCATION_KEY = "biker:location:";
    private static final String AVAILABLE_BIKERS_KEY = "biker:available";

    private final RedisTemplate<String, String> redisTemplate;
    private final BikerRepository bikerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void updateLocation(Long bikerId, Double latitude, Double longitude, String status) {
        String key = BIKER_LOCATION_KEY + bikerId;
        BikerLocation location = BikerLocation.builder()
                .bikerId(bikerId)
                .latitude(latitude)
                .longitude(longitude)
                .status(status)
                .lastUpdated(LocalDateTime.now())
                .build();
        
        // Get biker info from database
        Optional<Biker> bikerOpt = bikerRepository.findById(bikerId);
        bikerOpt.ifPresent(biker -> {
            location.setBikerName(biker.getName());
            location.setBikerPhone(biker.getPhone());
            location.setVehicleNumber(biker.getVehicleNumber());
        });
        
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(location));
        } catch (Exception e) {
            log.error("Error serializing biker location", e);
            return;
        }
        
        if ("AVAILABLE".equals(status)) {
            redisTemplate.opsForSet().add(AVAILABLE_BIKERS_KEY, bikerId.toString());
        } else {
            redisTemplate.opsForSet().remove(AVAILABLE_BIKERS_KEY, bikerId.toString());
        }
        
        log.info("Updated biker {} location: lat={}, lng={}, status={}", bikerId, latitude, longitude, status);
    }

    public BikerLocation getLocation(Long bikerId) {
        String key = BIKER_LOCATION_KEY + bikerId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, BikerLocation.class);
        } catch (Exception e) {
            log.error("Error deserializing biker location", e);
            return null;
        }
    }

    public void setBikerStatus(Long bikerId, String status) {
        BikerLocation location = getLocation(bikerId);
        if (location != null) {
            location.setStatus(status);
            location.setLastUpdated(LocalDateTime.now());
            try {
                redisTemplate.opsForValue().set(BIKER_LOCATION_KEY + bikerId, objectMapper.writeValueAsString(location));
            } catch (Exception e) {
                log.error("Error serializing biker location", e);
                return;
            }
            
            if ("AVAILABLE".equals(status)) {
                redisTemplate.opsForSet().add(AVAILABLE_BIKERS_KEY, bikerId.toString());
            } else if ("OFFLINE".equals(status) || "BUSY".equals(status)) {
                redisTemplate.opsForSet().remove(AVAILABLE_BIKERS_KEY, bikerId.toString());
            }
        }
    }

    public List<BikerLocation> findNearestBikers(Double latitude, Double longitude, int limit) {
        Set<String> availableBikerIds = redisTemplate.opsForSet().members(AVAILABLE_BIKERS_KEY);
        
        if (availableBikerIds == null || availableBikerIds.isEmpty()) {
            return List.of();
        }

        List<BikerLocation> bikersWithDistance = new ArrayList<>();
        
        for (String bikerIdStr : availableBikerIds) {
            Long bikerId = Long.parseLong(bikerIdStr);
            BikerLocation location = getLocation(bikerId);
            
            if (location != null && "AVAILABLE".equals(location.getStatus())) {
                double distance = calculateDistance(latitude, longitude, 
                    location.getLatitude(), location.getLongitude());
                
                bikersWithDistance.add(location);
            }
        }

        return bikersWithDistance.stream()
                .sorted((a, b) -> {
                    double distA = calculateDistance(latitude, longitude, a.getLatitude(), a.getLongitude());
                    double distB = calculateDistance(latitude, longitude, b.getLatitude(), b.getLongitude());
                    return Double.compare(distA, distB);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Long findNearestAvailableBikerId(Double latitude, Double longitude) {
        List<BikerLocation> nearest = findNearestBikers(latitude, longitude, 1);
        return nearest.isEmpty() ? null : nearest.get(0).getBikerId();
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

    public List<BikerLocation> getAllAvailableBikers() {
        Set<String> availableBikerIds = redisTemplate.opsForSet().members(AVAILABLE_BIKERS_KEY);
        
        if (availableBikerIds == null || availableBikerIds.isEmpty()) {
            return List.of();
        }
        
        return availableBikerIds.stream()
                .map(id -> getLocation(Long.parseLong(id)))
                .filter(loc -> loc != null)
                .collect(Collectors.toList());
    }
}
