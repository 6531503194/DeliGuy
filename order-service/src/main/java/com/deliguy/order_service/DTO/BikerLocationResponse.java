package com.deliguy.order_service.DTO;

import lombok.Data;

@Data
public class BikerLocationResponse {
    private Long bikerId;
    private String bikerName;
    private String bikerPhone;
    private String vehicleNumber;
    private Double latitude;
    private Double longitude;
    private Double distanceToCustomerKm;
    private Integer estimatedArrivalMinutes;
}
