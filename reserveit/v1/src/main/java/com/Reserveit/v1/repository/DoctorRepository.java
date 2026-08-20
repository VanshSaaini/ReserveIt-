package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser_Id(Long userId);
    List<Doctor> findByClinic_Id(Long clinicId);
    List<Doctor> findByClinic_IdAndUser_ActiveTrue(Long clinicId);
}
