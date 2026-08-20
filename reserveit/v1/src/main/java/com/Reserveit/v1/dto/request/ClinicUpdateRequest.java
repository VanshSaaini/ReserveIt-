package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClinicUpdateRequest(
        @NotBlank String name,
        @NotBlank String address,
        String phone,
        String email
) {
}
