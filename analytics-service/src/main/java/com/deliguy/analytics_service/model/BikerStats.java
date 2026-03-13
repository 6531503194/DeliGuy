package com.deliguy.analytics_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "biker_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikerStats {

    @Id
    private Long bikerId;

    private String bikerName;

    private Long deliveryCount;

    private Double totalEarnings;

    private Double averageRating;
}
