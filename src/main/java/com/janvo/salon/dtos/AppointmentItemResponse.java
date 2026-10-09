package com.janvo.salon.dtos;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppointmentItemResponse {

	private Long id;

	private Long subServiceId;

	private String serviceName;

	private String subServiceName;

	private BigDecimal unitPrice;

	private Integer durationMinutes;

	private Integer quantity;

	private BigDecimal totalPrice;
}
