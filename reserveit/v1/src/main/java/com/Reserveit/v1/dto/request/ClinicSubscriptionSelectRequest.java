package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotNull;

public record ClinicSubscriptionSelectRequest(
        @NotNull(message = "Plan id is required.")
        Long planId
) {}
