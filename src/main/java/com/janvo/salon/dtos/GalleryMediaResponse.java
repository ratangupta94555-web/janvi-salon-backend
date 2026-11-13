package com.janvo.salon.dtos;

import java.time.LocalDateTime;

import com.janvo.salon.enums.GalleryMediaType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GalleryMediaResponse {

	private Long id;
	private GalleryMediaType mediaType;
	private String secureUrl;
	private String title;
	private String category;
	private LocalDateTime createdAt;
}
