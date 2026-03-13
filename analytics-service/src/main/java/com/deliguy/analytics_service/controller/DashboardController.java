package com.deliguy.analytics_service.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliguy.analytics_service.model.BikerStats;
import com.deliguy.analytics_service.model.DailyStats;
import com.deliguy.analytics_service.model.RestaurantStats;
import com.deliguy.analytics_service.repository.BikerStatsRepository;
import com.deliguy.analytics_service.repository.DailyStatsRepository;
import com.deliguy.analytics_service.repository.RestaurantStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DailyStatsRepository dailyStatsRepository;
    private final RestaurantStatsRepository restaurantStatsRepository;
    private final BikerStatsRepository bikerStatsRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();
        
        DailyStats todayStats = dailyStatsRepository.findByDate(LocalDate.now())
                .orElse(DailyStats.builder()
                        .orderCount(0L)
                        .totalRevenue(0.0)
                        .completedDeliveries(0L)
                        .activeRestaurants(0L)
                        .activeBikers(0L)
                        .build());
        
        List<DailyStats> allStats = dailyStatsRepository.findAll();
        long totalOrders = allStats.stream().mapToLong(DailyStats::getOrderCount).sum();
        double totalRevenue = allStats.stream().mapToDouble(DailyStats::getTotalRevenue).sum();
        
        stats.put("todayOrders", todayStats.getOrderCount());
        stats.put("todayRevenue", todayStats.getTotalRevenue());
        stats.put("todayCompletedDeliveries", todayStats.getCompletedDeliveries());
        stats.put("activeRestaurants", todayStats.getActiveRestaurants());
        stats.put("activeBikers", todayStats.getActiveBikers());
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders/today")
    public ResponseEntity<Map<String, Object>> getTodayOrders() {
        DailyStats stats = dailyStatsRepository.findByDate(LocalDate.now())
                .orElse(DailyStats.builder()
                        .orderCount(0L)
                        .totalRevenue(0.0)
                        .completedDeliveries(0L)
                        .build());
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderCount", stats.getOrderCount());
        result.put("date", LocalDate.now());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue/today")
    public ResponseEntity<Map<String, Object>> getTodayRevenue() {
        DailyStats stats = dailyStatsRepository.findByDate(LocalDate.now())
                .orElse(DailyStats.builder()
                        .totalRevenue(0.0)
                        .build());
        
        Map<String, Object> result = new HashMap<>();
        result.put("revenue", stats.getTotalRevenue());
        result.put("date", LocalDate.now());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<Map<String, Object>> getRestaurantStats() {
        List<RestaurantStats> allRestaurants = restaurantStatsRepository.findAll();
        
        long totalRestaurants = allRestaurants.size();
        long totalOrders = allRestaurants.stream().mapToLong(RestaurantStats::getOrderCount).sum();
        double totalRevenue = allRestaurants.stream().mapToDouble(RestaurantStats::getTotalRevenue).sum();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRestaurants", totalRestaurants);
        result.put("totalOrders", totalOrders);
        result.put("totalRevenue", totalRevenue);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bikers")
    public ResponseEntity<Map<String, Object>> getBikerStats() {
        List<BikerStats> allBikers = bikerStatsRepository.findAll();
        
        long totalBikers = allBikers.size();
        long totalDeliveries = allBikers.stream().mapToLong(BikerStats::getDeliveryCount).sum();
        double totalEarnings = allBikers.stream().mapToDouble(BikerStats::getTotalEarnings).sum();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalBikers", totalBikers);
        result.put("totalDeliveries", totalDeliveries);
        result.put("totalEarnings", totalEarnings);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-restaurants")
    public ResponseEntity<List<RestaurantStats>> getTopRestaurants() {
        List<RestaurantStats> topRestaurants = restaurantStatsRepository
                .findTop10ByOrderCountGreaterThanOrderByOrderCountDesc(0L);
        return ResponseEntity.ok(topRestaurants);
    }

    @GetMapping("/top-bikers")
    public ResponseEntity<List<BikerStats>> getTopBikers() {
        List<BikerStats> topBikers = bikerStatsRepository
                .findTop10ByDeliveryCountGreaterThanOrderByDeliveryCountDesc(0L);
        return ResponseEntity.ok(topBikers);
    }
}
