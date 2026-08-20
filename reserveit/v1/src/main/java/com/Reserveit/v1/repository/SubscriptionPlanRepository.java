package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByActiveTrueOrderByPriceMonthlyAsc();
    Optional<SubscriptionPlan> findByNameIgnoreCase(String name);
}
