package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctor_Id(Long doctorId);
    List<DoctorAvailability> findByDoctor_IdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}
