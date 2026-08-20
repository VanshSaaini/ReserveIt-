package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String mobile,
        @Past LocalDate dateOfBirth
) {
}
