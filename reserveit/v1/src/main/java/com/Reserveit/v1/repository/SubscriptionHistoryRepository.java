package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {
    List<SubscriptionHistory> findByClinic_IdOrderByCreatedAtDesc(Long clinicId);
}
