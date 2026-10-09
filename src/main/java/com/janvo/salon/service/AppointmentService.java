package com.janvo.salon.service;

import java.time.LocalDate;
import java.util.List;

import com.janvo.salon.dtos.AppointmentResponse;
import com.janvo.salon.dtos.CreateAppointmentRequest;
import com.janvo.salon.enums.AppointmentStatus;

public interface AppointmentService {

	AppointmentResponse create(CreateAppointmentRequest request);

	AppointmentResponse getById(Long id);

	List<AppointmentResponse> getByCustomerId(Long customerId);

	List<AppointmentResponse> getByDate(LocalDate date);

	void cancel(Long id);
	
	AppointmentResponse updateStatus(Long id, AppointmentStatus status);
}
