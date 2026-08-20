package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.Appointment;
import com.Reserveit.v1.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_IdOrderByAppointmentDateDescStartTimeDesc(Long patientId);

    List<Appointment> findByDoctor_IdOrderByAppointmentDateDescStartTimeDesc(Long doctorId);

    List<Appointment> findByClinic_IdOrderByAppointmentDateDescStartTimeDesc(Long clinicId);

    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate date);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId, LocalDate date, AppointmentStatus excludedStatus);
}
