package com.janvo.salon.service;

import java.util.List;

import com.janvo.salon.dtos.SubServiceRequest;
import com.janvo.salon.dtos.SubServiceResponse;

public interface SubServiceService {

	SubServiceResponse create(Long serviceId, SubServiceRequest request);

	SubServiceResponse getById(Long id);

	List<SubServiceResponse> getByServiceId(Long serviceId);

	SubServiceResponse update(Long id, SubServiceRequest request);

	void delete(Long id);

	void deactivate(Long id);

}
