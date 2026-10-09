package com.janvo.salon.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.janvo.salon.dtos.SalonServiceRequest;
import com.janvo.salon.dtos.SalonServiceResponse;
import com.janvo.salon.dtos.SubServiceResponse;
import com.janvo.salon.entity.SalonService;
import com.janvo.salon.entity.SubService;
import com.janvo.salon.exceptions.DuplicateResourceException;
import com.janvo.salon.exceptions.ResourceNotFoundException;
import com.janvo.salon.repository.SalonServiceRepository;
import com.janvo.salon.service.SalonServiceService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SalonServiceServiceImpl implements SalonServiceService {

	private final SalonServiceRepository serviceRepository;

	@Override
	public SalonServiceResponse create(SalonServiceRequest request) {

		if (serviceRepository.existsByNameIgnoreCase(request.getName())) {

			throw new DuplicateResourceException("Service already exists");
		}

		SalonService service = SalonService.builder().name(request.getName()).description(request.getDescription())
				.icon(request.getIcon()).displayOrder(request.getDisplayOrder()).active(true).build();

		SalonService saved = serviceRepository.save(service);

		return mapToResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public SalonServiceResponse getById(Long id) {

		SalonService service = serviceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));

		return mapToResponse(service);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalonServiceResponse> getAll() {

		return serviceRepository.findByActiveTrueOrderByDisplayOrderAsc().stream().map(this::mapToResponse).toList();
	}

	@Override
	public SalonServiceResponse update(Long id, SalonServiceRequest request) {

		SalonService service = serviceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));

		service.setName(request.getName());
		service.setDescription(request.getDescription());
		service.setIcon(request.getIcon());
		service.setDisplayOrder(request.getDisplayOrder());

		return mapToResponse(service);
	}

	@Override
	public void delete(Long id) {

		SalonService service = serviceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));

		serviceRepository.delete(service);
	}

	@Override
	public void deactivate(Long id) {

		SalonService service = serviceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));

		service.setActive(false);
	}

	private SalonServiceResponse mapToResponse(SalonService service) {

		return SalonServiceResponse.builder().id(service.getId()).name(service.getName())
				.description(service.getDescription()).icon(service.getIcon()).displayOrder(service.getDisplayOrder())
				.active(service.getActive())
				.subServices(service.getSubServices().stream().map(this::mapSubService).toList()).build();
	}

	private SubServiceResponse mapSubService(SubService sub) {

		return SubServiceResponse.builder().id(sub.getId()).serviceId(sub.getService().getId()).name(sub.getName())
				.description(sub.getDescription()).price(sub.getPrice()).offerPrice(sub.getOfferPrice())
				.durationMinutes(sub.getDurationMinutes()).displayOrder(sub.getDisplayOrder()).active(sub.getActive())
				.build();
	}
}