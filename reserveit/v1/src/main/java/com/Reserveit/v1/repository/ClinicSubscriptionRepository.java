package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.ClinicSubscription;
import com.Reserveit.v1.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClinicSubscriptionRepository extends JpaRepository<ClinicSubscription, Long> {

    Optional<ClinicSubscription> findByClinic_Id(Long clinicId);

    Optional<ClinicSubscription> findByClinic_IdAndStatusIn(Long clinicId, List<SubscriptionStatus> statuses);

    List<ClinicSubscription> findByStatusInAndEndDateLessThanEqual(
            List<SubscriptionStatus> statuses, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ClinicSubscription> findWithLockByClinic_Id(Long clinicId);
}
