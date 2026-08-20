package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Used by a clinic admin to add a doctor to their own clinic. */
public record DoctorCreateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String mobile,
        @NotBlank String password,
        String specialization,
        String qualifications,
        Integer experienceYears,
        Integer defaultSlotMinutes
) {
}
