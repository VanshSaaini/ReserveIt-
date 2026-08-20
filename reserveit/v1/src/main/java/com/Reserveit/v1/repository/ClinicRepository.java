package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findByAdmin_Id(Long adminUserId);
    List<Clinic> findByActiveTrue();
    List<Clinic> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
