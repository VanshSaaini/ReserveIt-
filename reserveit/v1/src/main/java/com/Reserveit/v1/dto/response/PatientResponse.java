package com.Reserveit.v1.dto.response;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        LocalDate dateOfBirth
) {
}
