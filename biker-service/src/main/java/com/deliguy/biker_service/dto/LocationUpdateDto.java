package com.deliguy.biker_service.dto;

import lombok.Data;

@Data
public class LocationUpdateDto {
    private Double latitude;
    private Double longitude;
    private String status; // AVAILABLE, BUSY, OFFLINE
}
