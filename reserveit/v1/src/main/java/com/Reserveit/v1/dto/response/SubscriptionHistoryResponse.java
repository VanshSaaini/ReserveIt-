package com.Reserveit.v1.dto.response;

import com.Reserveit.v1.entity.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionHistoryResponse(
        Long id,
        Long planId,
        String planName,
        BigDecimal priceMonthly,
        Integer maxDoctors,
        SubscriptionStatus status,
        String eventType,
        LocalDate startDate,
        LocalDate endDate,
        String notes,
        LocalDateTime createdAt
) {}
