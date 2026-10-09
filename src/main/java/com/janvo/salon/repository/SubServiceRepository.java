package com.janvo.salon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.janvo.salon.entity.SubService;

public interface SubServiceRepository extends JpaRepository<SubService, Long> {

	List<SubService> findByServiceIdAndActiveTrueOrderByDisplayOrderAsc(Long serviceId);

	boolean existsByServiceIdAndNameIgnoreCase(Long serviceId, String name);

	List<SubService> findByIdIn(List<Long> ids);

	List<SubService> findByServiceId(Long serviceId);

}
