package com.janvo.salon.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalonLocationResponse {

	private String locationId;
	private String name;
	private String address;
}
