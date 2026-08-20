package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ServiceOfferingRequest(
        @NotBlank String name,
        String description,
        Integer durationMinutes,
        BigDecimal price
) {
}
