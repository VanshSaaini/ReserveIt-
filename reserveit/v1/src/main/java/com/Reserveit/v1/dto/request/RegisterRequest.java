package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Mirrors the React registration form (src/pages/Register.jsx). accountType is
 * either "patient" or "clinic"; dob is used for patients, clinicName/clinicAddress
 * for clinics. confirmPassword/agree are validated client-side and not required here.
 */
public record RegisterRequest(
        @NotBlank @Pattern(regexp = "patient|clinic", message = "accountType must be 'patient' or 'clinic'")
        String accountType,

        @NotBlank String firstName,
        @NotBlank String lastName,

        @NotBlank @Email String email,

        @NotBlank String mobile,

        String dob,

        String clinicName,

        String clinicAddress,

        @NotBlank String password
) {
}
