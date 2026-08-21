package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DoctorUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String mobile,
        String specialization,
        String qualifications,
        Integer experienceYears,
        Integer defaultSlotMinutes
) {}
