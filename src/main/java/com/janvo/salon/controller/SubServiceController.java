package com.janvo.salon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janvo.salon.dtos.SubServiceRequest;
import com.janvo.salon.dtos.SubServiceResponse;
import com.janvo.salon.service.SubServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sub-services")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SubServiceController {

    private final SubServiceService subServiceService;

    // Create sub-service
    @PostMapping("/service/{serviceId}")
    public ResponseEntity<SubServiceResponse> create(
            @PathVariable Long serviceId,
            @Valid @RequestBody SubServiceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subServiceService.create(serviceId, request));
    }

    // Get sub-service by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubServiceResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subServiceService.getById(id)
        );
    }

    // Get all sub-services by service ID
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<SubServiceResponse>> getByServiceId(
            @PathVariable Long serviceId) {

        return ResponseEntity.ok(
                subServiceService.getByServiceId(serviceId)
        );
    }

    // Update sub-service
    @PutMapping("/{id}")
    public ResponseEntity<SubServiceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubServiceRequest request) {

        return ResponseEntity.ok(
                subServiceService.update(id, request)
        );
    }

    // Hard delete sub-service
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        subServiceService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // Deactivate sub-service
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        subServiceService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}