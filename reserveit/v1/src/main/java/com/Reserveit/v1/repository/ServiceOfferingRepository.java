package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findByClinic_IdAndActiveTrue(Long clinicId);
    List<ServiceOffering> findByClinic_Id(Long clinicId);
}
