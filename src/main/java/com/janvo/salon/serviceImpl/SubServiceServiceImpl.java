package com.janvo.salon.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.janvo.salon.dtos.SubServiceRequest;
import com.janvo.salon.dtos.SubServiceResponse;
import com.janvo.salon.entity.SalonService;
import com.janvo.salon.entity.SubService;
import com.janvo.salon.exceptions.DuplicateResourceException;
import com.janvo.salon.exceptions.ResourceNotFoundException;
import com.janvo.salon.repository.SalonServiceRepository;
import com.janvo.salon.repository.SubServiceRepository;
import com.janvo.salon.service.SubServiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubServiceServiceImpl implements SubServiceService {

	private final SubServiceRepository subServiceRepository;

	private final SalonServiceRepository serviceRepository;

	@Override
	public SubServiceResponse create(Long serviceId, SubServiceRequest request) {

		SalonService service = serviceRepository.findById(serviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceId));

		if (subServiceRepository.existsByServiceIdAndNameIgnoreCase(serviceId, request.getName())) {

			throw new DuplicateResourceException("Sub service already exists");
		}

		SubService subService = SubService.builder().service(service).name(request.getName())
				.description(request.getDescription()).price(request.getPrice()).offerPrice(request.getOfferPrice())
				.durationMinutes(request.getDurationMinutes()).displayOrder(request.getDisplayOrder()).active(true)
				.build();

		SubService saved = subServiceRepository.save(subService);

		return mapToResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubServiceResponse> getByServiceId(Long serviceId) {

		if (!serviceRepository.existsById(serviceId)) {
			throw new ResourceNotFoundException("Service not found: " + serviceId);
		}

		return subServiceRepository.findByServiceIdAndActiveTrueOrderByDisplayOrderAsc(serviceId).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SubServiceResponse getById(Long id) {

		SubService subService = subServiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sub service not found: " + id));

		return mapToResponse(subService);
	}

	@Override
	public SubServiceResponse update(Long id, SubServiceRequest request) {

		SubService subService = subServiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sub service not found: " + id));

		subService.setName(request.getName());
		subService.setDescription(request.getDescription());
		subService.setPrice(request.getPrice());
		subService.setOfferPrice(request.getOfferPrice());
		subService.setDurationMinutes(request.getDurationMinutes());
		subService.setDisplayOrder(request.getDisplayOrder());

		return mapToResponse(subService);
	}

	@Override
	public void delete(Long id) {

		SubService subService = subServiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sub service not found: " + id));

		subServiceRepository.delete(subService);
	}

	@Override
	public void deactivate(Long id) {

		SubService subService = subServiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sub service not found: " + id));

		subService.setActive(false);
	}

	private SubServiceResponse mapToResponse(SubService subService) {

		return SubServiceResponse.builder().id(subService.getId()).serviceId(subService.getService().getId())
				.name(subService.getName()).description(subService.getDescription()).price(subService.getPrice())
				.offerPrice(subService.getOfferPrice()).durationMinutes(subService.getDurationMinutes())
				.displayOrder(subService.getDisplayOrder()).active(subService.getActive()).build();
	}
}