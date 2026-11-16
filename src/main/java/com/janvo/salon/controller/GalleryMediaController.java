package com.janvo.salon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.janvo.salon.dtos.ApiErrorResponse;
import com.janvo.salon.dtos.GalleryMediaResponse;
import com.janvo.salon.service.GalleryMediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GalleryMediaController {

	private final GalleryMediaService galleryMediaService;
	
	GalleryMediaController(GalleryMediaService galleryMediaService) {
		this.galleryMediaService=galleryMediaService;
	}

	@GetMapping
	public ResponseEntity<List<GalleryMediaResponse>> list() {
		return ResponseEntity.ok(galleryMediaService.list());
	}

	@PostMapping(value = "/admin/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> upload(
			@RequestHeader(value = "X-Gallery-Admin-Token", required = false) String adminToken,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			@RequestParam(value = "video", required = false) List<MultipartFile> videos,
			@RequestParam(defaultValue = "Makeup") String category) {
		if (!galleryMediaService.isAdminTokenConfigured()) {
			ApiErrorResponse error = ApiErrorResponse.builder()
					.status(HttpStatus.SERVICE_UNAVAILABLE.value())
					.message("Gallery uploads are not configured on the server")
					.timestamp(java.time.LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
		}
		if (!galleryMediaService.isValidAdminToken(adminToken)) {
			ApiErrorResponse error = ApiErrorResponse.builder()
					.status(HttpStatus.UNAUTHORIZED.value())
					.message("A valid gallery admin token is required")
					.timestamp(java.time.LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
}
