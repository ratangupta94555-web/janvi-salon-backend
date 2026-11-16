package com.janvo.salon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import com.janvo.salon.dtos.ApiErrorResponse;
import com.janvo.salon.dtos.GalleryMediaResponse;
import com.janvo.salon.service.GalleryMediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryMediaController {

	private final GalleryMediaService galleryMediaService;

	@GetMapping
	public ResponseEntity<List<GalleryMediaResponse>> list() {
		return ResponseEntity.ok(galleryMediaService.list());
	}

	@PostMapping(value = "/admin/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> upload(
			@RequestHeader(value = "X-Admin-Request", required = false) String adminRequest,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			@RequestParam(value = "video", required = false) List<MultipartFile> videos,
			@RequestParam(defaultValue = "Makeup") String category,
			HttpSession session) {
		if (!Boolean.TRUE.equals(session.getAttribute(AdminAuthController.ADMIN_SESSION_ATTRIBUTE))) {
			ApiErrorResponse error = ApiErrorResponse.builder()
					.status(HttpStatus.UNAUTHORIZED.value())
					.message("Admin sign-in is required to upload gallery media")
					.timestamp(java.time.LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}
		if (!"true".equals(adminRequest)) {
			ApiErrorResponse error = ApiErrorResponse.builder()
					.status(HttpStatus.FORBIDDEN.value())
					.message("The gallery upload request could not be verified")
					.timestamp(java.time.LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
		}
		if (!galleryMediaService.isCloudinaryConfigured()) {
			ApiErrorResponse error = ApiErrorResponse.builder()
					.status(HttpStatus.SERVICE_UNAVAILABLE.value())
					.message("Cloudinary credentials are not configured on the server")
					.timestamp(java.time.LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(galleryMediaService.upload(images, videos, category));
	}

	@DeleteMapping("/admin/{id}")
	public ResponseEntity<?> delete(
			@RequestHeader(value = "X-Admin-Request", required = false) String adminRequest,
			@PathVariable Long id,
			HttpSession session) {
		if (!Boolean.TRUE.equals(session.getAttribute(AdminAuthController.ADMIN_SESSION_ATTRIBUTE))) {
			return adminError(HttpStatus.UNAUTHORIZED, "Admin sign-in is required to delete gallery media");
		}
		if (!"true".equals(adminRequest)) {
			return adminError(HttpStatus.FORBIDDEN, "The gallery deletion request could not be verified");
		}
		if (!galleryMediaService.isCloudinaryConfigured()) {
			return adminError(HttpStatus.SERVICE_UNAVAILABLE, "Cloudinary credentials are not configured on the server");
		}
		galleryMediaService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<ApiErrorResponse> adminError(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(ApiErrorResponse.builder()
				.status(status.value())
				.message(message)
				.timestamp(java.time.LocalDateTime.now())
				.build());
	}
}
