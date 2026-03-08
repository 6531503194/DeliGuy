package com.deliguy.biker_service.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikerLocation implements Serializable {

    private Long bikerId;

    private Double latitude;

    private Double longitude;

    private String status; // AVAILABLE, BUSY, OFFLINE

    private LocalDateTime lastUpdated;

    private String bikerName;

    private String bikerPhone;

    private String vehicleNumber;
}
