package com.deliguy.restaurent_service.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long restaurantId;

    private String name;

    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    private Double price;

    private Boolean available;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddOn> addOns = new ArrayList<>();
}
