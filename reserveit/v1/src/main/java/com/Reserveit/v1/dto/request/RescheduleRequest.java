package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RescheduleRequest(
        @NotNull LocalDate appointmentDate,
        @NotNull LocalTime startTime
) {
}
