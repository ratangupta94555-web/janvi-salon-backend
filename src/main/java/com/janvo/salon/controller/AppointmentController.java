package com.janvo.salon.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janvo.salon.dtos.AppointmentResponse;
import com.janvo.salon.dtos.CreateAppointmentRequest;
import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PostMapping
	public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(appointmentService.getById(id));
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<AppointmentResponse>> getByCustomer(@PathVariable Long customerId) {

		return ResponseEntity.ok(appointmentService.getByCustomerId(customerId));
	}

	@GetMapping("/date/{date}")
	public ResponseEntity<List<AppointmentResponse>> getByDate(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return ResponseEntity.ok(appointmentService.getByDate(date));
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable Long id) {

		appointmentService.cancel(id);

		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody AppointmentStatus status) {

        return ResponseEntity.ok(
                appointmentService.updateStatus(id, status));
    }
}