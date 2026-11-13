package com.janvo.salon.serviceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.janvo.salon.dtos.GalleryMediaResponse;
import com.janvo.salon.entity.GalleryMedia;
import com.janvo.salon.enums.GalleryMediaType;
import com.janvo.salon.repository.GalleryMediaRepository;
import com.janvo.salon.service.GalleryMediaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleryMediaServiceImpl implements GalleryMediaService {

	private static final int MAX_IMAGES = 5;
	private static final int MAX_VIDEOS = 1;
	private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024;
	private static final long MAX_VIDEO_BYTES = 100L * 1024 * 1024;
	private static final Set<String> CATEGORIES = Set.of("Makeup", "Hair", "Bridal", "Studio");

	private final Cloudinary cloudinary;
	private final GalleryMediaRepository galleryMediaRepository;

	@Value("${cloudinary.cloud-name:}")
	private String cloudName;

	@Value("${cloudinary.api-key:}")
	private String apiKey;

	@Value("${cloudinary.api-secret:}")
	private String apiSecret;

	@Value("${gallery.admin-token:}")
	private String adminToken;

	@Override
	@Transactional(readOnly = true)
	public List<GalleryMediaResponse> list() {
		return galleryMediaRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public List<GalleryMediaResponse> upload(
			List<MultipartFile> images,
			List<MultipartFile> videos,
			String category) {
		List<MultipartFile> imageFiles = images == null
				? List.of()
				: images.stream().filter(file -> !file.isEmpty()).toList();
		List<MultipartFile> videoFiles = videos == null
				? List.of()
				: videos.stream().filter(file -> !file.isEmpty()).toList();

		if (imageFiles.isEmpty() && videoFiles.isEmpty()) {
			throw new IllegalArgumentException("Select at least one image or video to upload");
		}
		if (imageFiles.size() > MAX_IMAGES) {
			throw new IllegalArgumentException("You can upload at most 5 images at a time");
		}
		if (videoFiles.size() > MAX_VIDEOS) {
			throw new IllegalArgumentException("You can upload at most 1 video at a time");
		}
		if (!CATEGORIES.contains(category)) {
			throw new IllegalArgumentException("Choose a valid gallery category");
		}
		imageFiles.forEach(this::validateImage);
		videoFiles.forEach(this::validateVideo);

		List<GalleryMediaResponse> uploaded = new ArrayList<>();
		for (MultipartFile image : imageFiles) {
			uploaded.add(uploadOne(image, GalleryMediaType.IMAGE, category));
		}
		videoFiles.forEach(video -> uploaded.add(uploadOne(video, GalleryMediaType.VIDEO, category)));
		return uploaded;
	}

	@Override
	public boolean isAdminTokenConfigured() {
		return !isBlank(adminToken);
	}

	@Override
	public boolean isValidAdminToken(String token) {
		return !isBlank(adminToken) && !isBlank(token)
				&& MessageDigest.isEqual(adminToken.getBytes(StandardCharsets.UTF_8),
						token.trim().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean isCloudinaryConfigured() {
		return !isBlank(cloudName) && !isBlank(apiKey) && !isBlank(apiSecret);
	}

	private GalleryMediaResponse uploadOne(MultipartFile file, GalleryMediaType mediaType, String category) {
		String resourceType = mediaType == GalleryMediaType.IMAGE ? "image" : "video";
		try {
			Map<?, ?> result = cloudinary.uploader().upload(
					file.getBytes(),
					ObjectUtils.asMap(
							"resource_type", resourceType,
							"folder", "janvi-gallery",
							"use_filename", true,
							"unique_filename", true));

			GalleryMedia media = new GalleryMedia();
			media.setMediaType(mediaType);
			media.setCloudinaryPublicId((String) result.get("public_id"));
			media.setSecureUrl((String) result.get("secure_url"));
			media.setTitle(titleFrom(file.getOriginalFilename()));
			media.setCategory(category);
			return toResponse(galleryMediaRepository.save(media));
		} catch (IOException exception) {
			throw new IllegalStateException("Cloudinary could not upload the selected file", exception);
		}
	}

	private void validateImage(MultipartFile file) {
		if (file.getSize() > MAX_IMAGE_BYTES) {
			throw new IllegalArgumentException("Each image must be 10 MB or smaller");
		}
		if (file.getContentType() == null || !file.getContentType().toLowerCase().startsWith("image/")) {
			throw new IllegalArgumentException("Only image files are allowed in the images field");
		}
	}

	private void validateVideo(MultipartFile file) {
		if (file.getSize() > MAX_VIDEO_BYTES) {
			throw new IllegalArgumentException("The video must be 100 MB or smaller");
		}
		if (file.getContentType() == null || !file.getContentType().toLowerCase().startsWith("video/")) {
			throw new IllegalArgumentException("Select a valid video file");
		}
	}

	private String titleFrom(String originalFilename) {
		if (isBlank(originalFilename)) {
			return "Janvi Makeover";
		}
		String filename = originalFilename.replace('\\', '/');
		filename = filename.substring(filename.lastIndexOf('/') + 1);
		int extensionIndex = filename.lastIndexOf('.');
		String title = extensionIndex > 0 ? filename.substring(0, extensionIndex) : filename;
		title = title.replace('_', ' ').replace('-', ' ').trim();
		return title.isEmpty() ? "Janvi Makeover" : title.substring(0, Math.min(title.length(), 120));
	}

	private GalleryMediaResponse toResponse(GalleryMedia media) {
		return GalleryMediaResponse.builder()
				.id(media.getId())
				.mediaType(media.getMediaType())
				.secureUrl(media.getSecureUrl())
				.title(media.getTitle())
				.category(media.getCategory())
				.createdAt(media.getCreatedAt())
				.build();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
