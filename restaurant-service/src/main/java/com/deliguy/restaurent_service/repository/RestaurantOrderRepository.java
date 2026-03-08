package com.deliguy.restaurent_service.repository;

import com.deliguy.restaurent_service.model.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantOrderRepository
        extends JpaRepository<RestaurantOrder, Long> {

    List<RestaurantOrder> findByRestaurantId(Long restaurantId);
}
