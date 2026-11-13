package com.janvo.salon.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.janvo.salon.entity.Appointment;
import com.janvo.salon.enums.AppointmentStatus;
import com.janvo.salon.enums.SalonLocationId;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	@Query("""
			    SELECT COUNT(a) > 0
			    FROM Appointment a
			    WHERE a.appointmentDate = :date
			    AND (a.locationId = :locationId OR a.locationId IS NULL)
			    AND a.status IN :statuses
			    AND a.startTime < :endTime
			    AND a.endTime > :startTime
			""")
	boolean existsOverlappingAppointment(@Param("date") LocalDate date,
			@Param("locationId") SalonLocationId locationId, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime, @Param("statuses") List<AppointmentStatus> statuses);

	@Query("""
			    SELECT a
			    FROM Appointment a
			    WHERE a.appointmentDate = :date
			    AND (a.locationId = :locationId OR a.locationId IS NULL)
			    ORDER BY a.startTime ASC
			""")
	List<Appointment> findAvailabilityByLocationAndDate(@Param("locationId") SalonLocationId locationId,
			@Param("date") LocalDate date);

	List<Appointment> findByCustomerIdOrderByAppointmentDateDesc(Long customerId);

	List<Appointment> findByAppointmentDateOrderByStartTimeAsc(LocalDate date);
}