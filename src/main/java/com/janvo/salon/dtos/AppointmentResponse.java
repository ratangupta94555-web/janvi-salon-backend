package com.janvo.salon.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.enums.PaymentStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppointmentResponse {

	private Long id;

	private Long customerId;

	private String customerName;

	private String phone;

	private LocalDate appointmentDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private Integer totalDurationMinutes;

	private BigDecimal subtotal;

	private BigDecimal discount;

	private BigDecimal totalAmount;

	private AppointmentStatus status;

	private PaymentStatus paymentStatus;

	private String notes;

	private List<AppointmentItemResponse> items;
}