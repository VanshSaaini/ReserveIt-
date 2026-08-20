package com.Reserveit.v1.dto.response;

public record RegisterResponse(
        Long userId,
        String email,
        String role,
        Long clinicId,
        String message
) {
}
