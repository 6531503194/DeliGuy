package com.deliguy.analytics_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantStats {

    @Id
    private String restaurantId;

    private String restaurantName;

    private Long orderCount;

    private Double totalRevenue;

    private Long acceptedCount;

    private Long rejectedCount;
}
