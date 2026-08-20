package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentBookRequest(
        @NotNull Long doctorId,
        Long serviceId,
        @NotNull LocalDate appointmentDate,
        @NotNull LocalTime startTime,
        String notes
) {
}
