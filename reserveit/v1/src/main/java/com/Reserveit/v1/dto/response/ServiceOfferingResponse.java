package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;

public record ServiceOfferingResponse(
        Long id,
        Long clinicId,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        boolean active
) {
}
