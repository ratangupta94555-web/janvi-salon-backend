package com.janvo.salon.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.janvo.salon.controller.AdminAuthController;
import com.janvo.salon.dtos.ApiErrorResponse;
import com.janvo.salon.dtos.AppointmentResponse;
import com.janvo.salon.dtos.CreateAppointmentRequest;
import com.janvo.salon.dtos.SalonLocationResponse;
import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.enums.SalonLocationId;
import com.janvo.salon.service.AppointmentService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService appointmentService;

	@DeleteMapping("/{id}/admin")
	public ResponseEntity<?> delete(
			@PathVariable Long id,
			@RequestHeader(value = "X-Admin-Request", required = false) String adminRequest,
			HttpSession session) {
		if (!Boolean.TRUE.equals(session.getAttribute(AdminAuthController.ADMIN_SESSION_ATTRIBUTE))) {
			return adminError(HttpStatus.UNAUTHORIZED, "Admin sign-in is required to delete appointments");
		}
		if (!"true".equals(adminRequest)) {
			return adminError(HttpStatus.FORBIDDEN, "The appointment deletion request could not be verified");
		}
		appointmentService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
	}

	@GetMapping("/locations")
	public ResponseEntity<List<SalonLocationResponse>> getLocations() {
		return ResponseEntity.ok(appointmentService.getLocations());
	}

	@GetMapping("/availability")
	public ResponseEntity<List<AppointmentResponse>> getAvailability(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam SalonLocationId locationId) {
		return ResponseEntity.ok(appointmentService.getAvailability(date, locationId));
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

	private ResponseEntity<ApiErrorResponse> adminError(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(ApiErrorResponse.builder()
				.status(status.value())
				.message(message)
				.timestamp(LocalDateTime.now())
				.build());
	}
}