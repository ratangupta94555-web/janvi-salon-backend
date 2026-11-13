package com.janvo.salon.service;

import java.time.LocalDate;
import java.util.List;

import com.janvo.salon.dtos.AppointmentResponse;
import com.janvo.salon.dtos.CreateAppointmentRequest;
import com.janvo.salon.dtos.SalonLocationResponse;
import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.enums.SalonLocationId;

public interface AppointmentService {

	AppointmentResponse create(CreateAppointmentRequest request);

	AppointmentResponse getById(Long id);

	List<AppointmentResponse> getByCustomerId(Long customerId);

	List<AppointmentResponse> getByDate(LocalDate date);

	List<SalonLocationResponse> getLocations();

	List<AppointmentResponse> getAvailability(LocalDate date, SalonLocationId locationId);

	void cancel(Long id);
	
	AppointmentResponse updateStatus(Long id, AppointmentStatus status);
}
