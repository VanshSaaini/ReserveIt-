package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionPaymentResponse(
        Long id,
        Long clinicId,
        String clinicName,
        Long planId,
        String planName,
        LocalDate billingMonth,
        BigDecimal amount,
        boolean paid,
        LocalDateTime paidAt,
        String markedBy
) {}
