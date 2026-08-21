package com.Reserveit.v1.dto.response;

import com.Reserveit.v1.entity.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClinicSubscriptionResponse(
        Long id,
        Long clinicId,
        Long planId,
        String planName,
        BigDecimal priceMonthly,
        Integer maxDoctors,
        Integer currentDoctors,
        Integer remainingDoctors,
        SubscriptionStatus status,
        LocalDate startDate,
        LocalDate endDate,
        long daysRemaining,
        boolean feeCollected,
        LocalDateTime feeCollectedAt,
        LocalDateTime updatedAt
) {}
