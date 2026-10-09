package com.janvo.salon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.janvo.salon.entity.SalonService;

public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {

	boolean existsByNameIgnoreCase(String name);

	List<SalonService> findByActiveTrueOrderByDisplayOrderAsc();
}
