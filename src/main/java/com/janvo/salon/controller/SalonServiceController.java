package com.janvo.salon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janvo.salon.dtos.SalonServiceRequest;
import com.janvo.salon.dtos.SalonServiceResponse;
import com.janvo.salon.service.SalonServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SalonServiceController {

	private final SalonServiceService service;

	@PostMapping
	public ResponseEntity<SalonServiceResponse> create(@Valid @RequestBody SalonServiceRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
	}

	@GetMapping
	public ResponseEntity<List<SalonServiceResponse>> getAll() {

		return ResponseEntity.ok(service.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<SalonServiceResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(service.getById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SalonServiceResponse> update(@PathVariable Long id,
			@Valid @RequestBody SalonServiceRequest request) {

		return ResponseEntity.ok(service.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		service.delete(id);

		return ResponseEntity.noContent().build();
	}
}