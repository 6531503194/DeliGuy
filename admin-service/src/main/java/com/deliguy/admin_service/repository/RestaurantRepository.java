package com.deliguy.admin_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliguy.admin_service.model.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    
    List<Restaurant> findByIsActive(Boolean isActive);
    
    long countByIsActive(Boolean isActive);
}
