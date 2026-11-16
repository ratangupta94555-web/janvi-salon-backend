package com.janvo.salon.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.janvo.salon.dtos.GalleryMediaResponse;

public interface GalleryMediaService {

	List<GalleryMediaResponse> list();

	List<GalleryMediaResponse> upload(
			List<MultipartFile> images,
			List<MultipartFile> videos,
			String category);

	void delete(Long id);

	boolean isCloudinaryConfigured();
}
