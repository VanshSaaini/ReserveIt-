package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SubscriptionPlanRequest(
        @NotBlank(message = "Plan name is required.")
        String name,

        @NotNull(message = "Monthly price is required.")
        @DecimalMin(value = "0.0", inclusive = true, message = "Monthly price cannot be negative.")
        BigDecimal priceMonthly,

        @NotNull(message = "Maximum doctors is required.")
        @Min(value = 1, message = "Maximum doctors must be at least 1.")
        Integer maxDoctors
) {}
