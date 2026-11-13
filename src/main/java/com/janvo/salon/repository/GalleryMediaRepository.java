package com.janvo.salon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.janvo.salon.entity.GalleryMedia;

public interface GalleryMediaRepository extends JpaRepository<GalleryMedia, Long> {

	List<GalleryMedia> findAllByOrderByCreatedAtDesc();
}
