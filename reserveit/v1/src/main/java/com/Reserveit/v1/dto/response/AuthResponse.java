package com.Reserveit.v1.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String email,
        String firstName,
        String lastName,
        String role,
        Long clinicId
) {
    public AuthResponse(String token, Long userId, String email, String firstName,
                         String lastName, String role, Long clinicId) {
        this(token, "Bearer", userId, email, firstName, lastName, role, clinicId);
    }
}
