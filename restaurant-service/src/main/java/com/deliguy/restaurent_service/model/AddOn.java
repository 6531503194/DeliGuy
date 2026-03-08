package com.deliguy.restaurent_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_addons")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // fried egg, fried pork

    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;
}
