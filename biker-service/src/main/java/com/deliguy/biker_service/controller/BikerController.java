package com.deliguy.biker_service.controller;

import com.deliguy.biker_service.model.Biker;
import com.deliguy.biker_service.model.BikerLocation;
import com.deliguy.biker_service.repository.BikerRepository;
import com.deliguy.biker_service.service.BikerLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biker")
@RequiredArgsConstructor
public class BikerController {

    private final BikerRepository bikerRepository;
    private final BikerLocationService bikerLocationService;

    @GetMapping
    public ResponseEntity<List<Biker>> getAllBikers() {
        return ResponseEntity.ok(bikerRepository.findAll());
    }

    @GetMapping("/{bikerId}")
    public ResponseEntity<Biker> getBiker(@PathVariable Long bikerId) {
        return bikerRepository.findById(bikerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{bikerId}/location")
    public ResponseEntity<BikerLocation> getBikerLocation(@PathVariable Long bikerId) {
        BikerLocation location = bikerLocationService.getLocation(bikerId);
        return location != null 
                ? ResponseEntity.ok(location) 
                : ResponseEntity.notFound().build();
    }
}