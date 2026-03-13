package com.deliguy.restaurent_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliguy.restaurent_service.model.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    
    Optional<Restaurant> findByIdAndIsActive(String id, Boolean isActive);
    
    List<Restaurant> findByIsActive(Boolean isActive);
}
