package com.deliguy.restaurent_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliguy.restaurent_service.dto.AddOnRequest;
import com.deliguy.restaurent_service.dto.CreateMenuItemRequest;
import com.deliguy.restaurent_service.model.AddOn;
import com.deliguy.restaurent_service.model.MenuItem;
import com.deliguy.restaurent_service.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurant/menu")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT')")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public MenuItem createMenu(
            @RequestHeader("X-RESTAURANT-ID") Long restaurantId,
            @RequestBody CreateMenuItemRequest request
    ) {
        return menuService.createMenu(restaurantId, request);
    }

    @PostMapping("/{menuItemId}/addons")
    public AddOn addAddOn(
            @PathVariable Long menuItemId,
            @RequestBody AddOnRequest request
    ) {
        return menuService.addAddOn(menuItemId, request);
    }

    @GetMapping
    public List<MenuItem> myMenu(
            @RequestHeader("X-RESTAURANT-ID") Long restaurantId
    ) {
        return menuService.getMenu(restaurantId);
    }
}
