package com.janvo.salon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.janvo.salon.entity.GalleryMedia;
import com.janvo.salon.enums.GalleryMediaType;

public interface GalleryMediaRepository extends JpaRepository<GalleryMedia, Long> {

	List<GalleryMedia> findAllByOrderByCreatedAtDesc();

	long countByMediaType(GalleryMediaType mediaType);
}
