package com.deliguy.restaurent_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliguy.restaurent_service.dto.AddOnRequest;
import com.deliguy.restaurent_service.dto.CreateMenuItemRequest;
import com.deliguy.restaurent_service.model.AddOn;
import com.deliguy.restaurent_service.model.MenuItem;
import com.deliguy.restaurent_service.repository.AddOnRepository;
import com.deliguy.restaurent_service.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepo;
    private final AddOnRepository addOnRepo;

    public MenuItem createMenu(
            Long restaurantId,
            CreateMenuItemRequest request
    ) {
        MenuItem item = MenuItem.builder()
                .restaurantId(restaurantId)
                .name(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .available(true)
                .build();

        return menuItemRepo.save(item);
    }

    public AddOn addAddOn(Long menuItemId, AddOnRequest request) {

        MenuItem item = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        AddOn addOn = AddOn.builder()
                .name(request.getName())
                .price(request.getPrice())
                .menuItem(item)
                .build();

        return addOnRepo.save(addOn);
    }

    public List<MenuItem> getMenu(Long restaurantId) {
        return menuItemRepo.findByRestaurantId(restaurantId);
    }
}
