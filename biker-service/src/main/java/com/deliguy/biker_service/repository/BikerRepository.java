package com.deliguy.biker_service.repository;

import com.deliguy.biker_service.model.Biker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BikerRepository extends JpaRepository<Biker, Long> {
}