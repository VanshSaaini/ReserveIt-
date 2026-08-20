package com.Reserveit.v1.dto.request;

import com.Reserveit.v1.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(
        @NotNull AppointmentStatus status,
        String notes
) {
}
