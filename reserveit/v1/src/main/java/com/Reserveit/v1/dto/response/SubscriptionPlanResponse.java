package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriptionPlanResponse(
        Long id,
        String name,
        BigDecimal priceMonthly,
        Integer maxDoctors,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
