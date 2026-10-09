package com.janvo.salon.service;

import java.util.List;

import com.janvo.salon.dtos.SalonServiceRequest;
import com.janvo.salon.dtos.SalonServiceResponse;

public interface SalonServiceService {

	SalonServiceResponse create(SalonServiceRequest request);

	SalonServiceResponse getById(Long id);

	List<SalonServiceResponse> getAll();

	SalonServiceResponse update(Long id, SalonServiceRequest request);

	void delete(Long id);

	void deactivate(Long id);
}