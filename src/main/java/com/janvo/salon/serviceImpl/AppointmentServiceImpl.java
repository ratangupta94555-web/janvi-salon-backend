package com.janvo.salon.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.janvo.salon.dtos.AppointmentItemResponse;
import com.janvo.salon.dtos.AppointmentResponse;
import com.janvo.salon.dtos.CreateAppointmentRequest;
import com.janvo.salon.entity.Appointment;
import com.janvo.salon.entity.AppointmentItem;
import com.janvo.salon.entity.Customer;
import com.janvo.salon.entity.SubService;
import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.enums.PaymentStatus;
import com.janvo.salon.exceptions.ResourceNotFoundException;
import com.janvo.salon.exceptions.SlotNotAvailableException;
import com.janvo.salon.repository.AppointmentRepository;
import com.janvo.salon.repository.CustomerRepository;
import com.janvo.salon.repository.SubServiceRepository;
import com.janvo.salon.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final CustomerRepository customerRepository;
	private final SubServiceRepository subServiceRepository;

	@Override
	public AppointmentResponse create(CreateAppointmentRequest request) {

		String phone = request.getPhone().trim();

		Customer customer = customerRepository.findByPhone(phone).orElseGet(() -> {
			Customer newCustomer = Customer.builder().firstName(request.getFirstName().trim())
					.lastName(request.getLastName().trim()).phone(phone).email(request.getEmail()).build();

			return customerRepository.save(newCustomer);
		});

		List<SubService> subServices = subServiceRepository.findByIdIn(request.getSubServiceIds());

		if (subServices.size() != request.getSubServiceIds().size()) {
			throw new ResourceNotFoundException("One or more sub-services not found");
		}

		boolean inactiveExists = subServices.stream().anyMatch(sub -> !Boolean.TRUE.equals(sub.getActive()));

		if (inactiveExists) {
			throw new IllegalStateException("One or more selected services are inactive");
		}

		int totalDuration = subServices.stream().mapToInt(SubService::getDurationMinutes).sum();

		LocalTime startTime = request.getPreferredStartTime();
		LocalTime endTime = startTime.plusMinutes(totalDuration);

		boolean alreadyBooked = appointmentRepository.existsOverlappingAppointment(request.getAppointmentDate(),
				startTime, endTime,
				List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED, AppointmentStatus.IN_PROGRESS));

		if (alreadyBooked) {
			throw new SlotNotAvailableException("Selected time slot is not available");
		}

		BigDecimal subtotal = subServices.stream().map(SubService::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal total = subServices.stream().map(this::getEstimatedPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal discount = subtotal.subtract(total);

		Appointment appointment = Appointment.builder().customer(customer).appointmentDate(request.getAppointmentDate())
				.startTime(startTime).endTime(endTime).totalDurationMinutes(totalDuration).subtotal(subtotal)
				.discount(discount).totalAmount(total).status(AppointmentStatus.PENDING)
				.paymentStatus(PaymentStatus.UNPAID).notes(request.getNotes()).build();

		for (SubService subService : subServices) {
			BigDecimal estimatedPrice = getEstimatedPrice(subService);

			AppointmentItem item = AppointmentItem.builder().appointment(appointment).subService(subService)
					.serviceNameSnapshot(subService.getService().getName()).subServiceNameSnapshot(subService.getName())
					.unitPrice(subService.getPrice()).durationMinutes(subService.getDurationMinutes()).quantity(1)
					.totalPrice(estimatedPrice).build();

			appointment.getItems().add(item);
		}

		Appointment saved = appointmentRepository.save(appointment);

		return mapToResponse(saved);
	}

	private BigDecimal getEstimatedPrice(SubService subService) {
		return subService.getOfferPrice() != null ? subService.getOfferPrice() : subService.getPrice();
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentResponse getById(Long id) {

		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));

		return mapToResponse(appointment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentResponse> getByCustomerId(Long customerId) {

		return appointmentRepository.findByCustomerIdOrderByAppointmentDateDesc(customerId).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentResponse> getByDate(LocalDate date) {

		return appointmentRepository.findByAppointmentDateOrderByStartTimeAsc(date).stream().map(this::mapToResponse)
				.toList();
	}

	@Override
	public void cancel(Long id) {

		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));

		if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
			throw new IllegalStateException("Completed appointment cannot be cancelled");
		}

		appointment.setStatus(AppointmentStatus.CANCELLED);
	}
	
	
	@Override
	public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {

	    if (status == null) {
	        throw new IllegalArgumentException("Appointment status is required");
	    }

	    Appointment appointment = appointmentRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Appointment not found: " + id));

	    if (appointment.getStatus() == AppointmentStatus.COMPLETED
	            && status != AppointmentStatus.COMPLETED) {
	        throw new IllegalStateException(
	                "Completed appointment status cannot be changed");
	    }

	    appointment.setStatus(status);

	    return mapToResponse(appointment);
	}

	private AppointmentResponse mapToResponse(Appointment appointment) {

		List<AppointmentItemResponse> items = appointment.getItems().stream()
				.map(item -> AppointmentItemResponse.builder().id(item.getId())
						.subServiceId(item.getSubService().getId()).serviceName(item.getServiceNameSnapshot())
						.subServiceName(item.getSubServiceNameSnapshot()).unitPrice(item.getUnitPrice())
						.durationMinutes(item.getDurationMinutes()).quantity(item.getQuantity())
						.totalPrice(item.getTotalPrice()).build())
				.toList();

		return AppointmentResponse.builder().id(appointment.getId()).customerId(appointment.getCustomer().getId())
				.customerName(appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName())
				.phone(appointment.getCustomer().getPhone()).appointmentDate(appointment.getAppointmentDate())
				.startTime(appointment.getStartTime()).endTime(appointment.getEndTime())
				.totalDurationMinutes(appointment.getTotalDurationMinutes()).subtotal(appointment.getSubtotal())
				.discount(appointment.getDiscount()).totalAmount(appointment.getTotalAmount())
				.status(appointment.getStatus()).paymentStatus(appointment.getPaymentStatus())
				.notes(appointment.getNotes()).items(items).build();
	}
}