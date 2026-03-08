package com.deliguy.restaurent_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deliguy.restaurent_service.model.AddOn;

public interface AddOnRepository extends JpaRepository<AddOn, Long> {

    public AddOn save(AddOn addOn);

    public void deleteById(Long id);


    public AddOn findById(long id);

    
    
}
