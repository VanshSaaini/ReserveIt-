package com.Reserveit.v1.dto.response;

public record ClinicResponse(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        boolean active,
        Long adminUserId,
        String adminName
) {
}
