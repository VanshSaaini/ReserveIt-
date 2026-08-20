package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.SubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    Optional<SubscriptionPayment> findByClinic_IdAndBillingMonth(Long clinicId, LocalDate billingMonth);

    List<SubscriptionPayment> findByBillingMonthOrderByClinic_NameAsc(LocalDate billingMonth);

    List<SubscriptionPayment> findByClinic_IdOrderByBillingMonthDesc(Long clinicId);
}
