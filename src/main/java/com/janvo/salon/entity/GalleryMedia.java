package com.janvo.salon.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.janvo.salon.enums.GalleryMediaType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gallery_media")
@Getter
@Setter
@NoArgsConstructor
public class GalleryMedia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private GalleryMediaType mediaType;

	@Column(nullable = false, unique = true, length = 255)
	private String cloudinaryPublicId;

	@Column(nullable = false, length = 1000)
	private String secureUrl;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(nullable = false, length = 40)
	private String category;

	@CreationTimestamp
	private LocalDateTime createdAt;
}
