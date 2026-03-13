package com.deliguy.admin_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;

    private Double longitude;

    @NotBlank(message = "Phone is required")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String description;
}
